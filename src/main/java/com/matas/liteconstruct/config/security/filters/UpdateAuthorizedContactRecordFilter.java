package com.matas.liteconstruct.config.security.filters;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.companycontactclass.abstractmodel.CompanyContactRelationAbstract;
import com.matas.liteconstruct.db.models.companycontactclass.repos.CompanyContactRelationRepository;
import com.matas.liteconstruct.db.models.companydomain.abstractmodel.CompanyDomainAbstract;
import com.matas.liteconstruct.db.models.companydomain.repos.CompanyDomainRepository;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepository;
import com.matas.liteconstruct.db.models.recordowner.repos.RecordsOwnerRepository;
import com.matas.liteconstruct.db.models.security.abstractmodel.SystemRoles;
import com.matas.liteconstruct.db.models.serviceauthorized.abstractmodel.AuthorizedContactAbstract;
import com.matas.liteconstruct.db.models.serviceauthorized.model.AuthorozedContact;
import com.matas.liteconstruct.db.models.serviceauthorized.repos.AuthorizedContactRepository;
import com.matas.liteconstruct.service.SQLProtection;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@NoArgsConstructor
public class UpdateAuthorizedContactRecordFilter implements Filter {

  private PasswordEncoder passwordEncoder;

  @Autowired
  void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  private AuthorizedContactRepository authorizedContactRepository;

  @Autowired
  void setAuthorozedContactRepository(AuthorizedContactRepository authorozedContactRepository) {
    this.authorizedContactRepository = authorozedContactRepository;
  }

  private CompanyDomainRepository companyDomainRepositoryImplemented;

  @Autowired
  void setCompanyDomainRepositoryImplemented(
      CompanyDomainRepository companyDomainRepositoryImplemented) {
    this.companyDomainRepositoryImplemented = companyDomainRepositoryImplemented;
  }

  private DynamicRoleRepository dynamicRoleRepository;

  @Autowired
  void setDynamicRoleRepositoryImplemented(DynamicRoleRepository dynamicRoleRepository) {
    this.dynamicRoleRepository = dynamicRoleRepository;
  }

  private RecordsOwnerRepository recordsOwnerRepository;

  @Autowired
  void setRecordsOwnerRepository(RecordsOwnerRepository recordsOwnerRepository) {
    this.recordsOwnerRepository = recordsOwnerRepository;
  }

  private CompanyContactRelationRepository companyContactRelationRepository;

  @Autowired
  public void setCompanyContactRelationRepository(
      CompanyContactRelationRepository companyContactRelationRepository) {
    this.companyContactRelationRepository = companyContactRelationRepository;
  }

  final UUID DEFAULT_CUSTOMER_ROLE = SystemRoles.SYSTEM_USER.getUUID();

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain chain) throws IOException, ServletException {
    log.info("start work in UpdateAuthorize");
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    boolean isWrong = true;
    String grantType =
        (String) SQLProtection.protectRequestObject(request.getParameter("grant_type"));

    try {
      if (request.getRequestURI().endsWith("/oauth/token") && grantType != null
          && !grantType.equalsIgnoreCase("refresh_token")) {
        String userName =
            (String) SQLProtection.protectRequestObject(request.getParameter("username"));
        if (userName != null) {
          userName = userName.toLowerCase();
          String sitePage =
              (String) SQLProtection.protectRequestObject(request.getParameter("sitepage"));
          log.info("site page: {}", sitePage);
          // String refreshToken =
          // (String) SQLProtection.protectString(request.getParameter("refresh_token"));

          if (sitePage != null) {

            request = new RelativeParamsWrappedRequest(request, userName);
            CompanyDomainAbstract domainValue =
                companyDomainRepositoryImplemented.getCompanyDomainByValue(sitePage);
            log.info("domainValue: ", domainValue); 
            if (domainValue != null) {
              AuthorizedContactAbstract contactValue =
                  authorizedContactRepository.getAuthorizedContactByName(userName);
              List<DynamicRoleModelAbstract> presentRoles =
                  dynamicRoleRepository.getDynamicRolesByCompanyServiceLogin(
                      domainValue.getCompanyId(), domainValue.getServiceId(), userName);

              UUID contactId = dynamicRoleRepository.getContactByLogin(userName);
              DynamicRoleModelAbstract currentDynamicRole =
                  DBConstants.getDefaultRoleForList(presentRoles, DEFAULT_CUSTOMER_ROLE);
              log.info("currentDynamicRole: ", currentDynamicRole); 
              if (currentDynamicRole != null && contactId != null) {
                isWrong = false;
                CompanyContactRelationAbstract companyClassRelation =
                    companyContactRelationRepository
                        .getCompanyContactRelationByCompanyId(currentDynamicRole.getCompanyId());
                if (companyClassRelation == null)
                  throw new NullPointerException("wrong relation.");
                if (contactValue == null) {
                  authorizedContactRepository.addAuthorizedContact(new AuthorozedContact(contactId,
                      null, userName, currentDynamicRole.getId(), currentDynamicRole.getCompanyId(),
                      currentDynamicRole.getServiceId(), currentDynamicRole.getRoleId(),
                      domainValue.getId(), companyClassRelation.getClassId()));
                } else {
                  contactValue.setDynamicRoleId(currentDynamicRole.getId());
                  contactValue.setCompanyId(currentDynamicRole.getCompanyId());
                  contactValue.setServiceId(currentDynamicRole.getServiceId());
                  contactValue.setRoleId(currentDynamicRole.getRoleId());
                  contactValue.setCompanyContactClassId(companyClassRelation.getClassId());
                  contactValue.setDomainId(domainValue.getId());
                  authorizedContactRepository.updateAuthorizedContact(contactValue);
                }
              }
            }
          }
        }
      } else {
        isWrong = false;
      }
    } catch (Exception ex) {
      System.err.println("error in thread: " + ExceptionUtils.getStackTrace(ex));
    }

    if (isWrong) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    } else {
      chain.doFilter(request, servletResponse);
    }

  }

  @Override
  public void init(FilterConfig filterConfig) {}

  @Override
  public void destroy() {}
}
