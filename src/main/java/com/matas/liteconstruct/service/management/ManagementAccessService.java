package com.matas.liteconstruct.service.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactory;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactoryImplemented;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepository;
import com.matas.liteconstruct.db.models.serviceauthorized.abstractmodel.AuthorizedContactAbstract;
import com.matas.liteconstruct.db.models.serviceauthorized.model.AuthorozedContact.MapKey;
import com.matas.liteconstruct.db.models.serviceauthorized.repos.AuthorizedContactRepository;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import com.matas.liteconstruct.service.common.HttpRequestService;
import com.matas.liteconstruct.service.dynamic.CacheMainParams;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ManagementAccessService {
  DynamicRoleRepository dynamicRoleRepository;

  @Autowired
  public void setDynamicRoleRepositoryImplemented(DynamicRoleRepository dynamicRoleRepository) {
    this.dynamicRoleRepository = dynamicRoleRepository;
  }

  private AccessRuleQueryFactory accessRuleQueryFactoryImplemented;

  @Autowired
  void setAccessRuleQueryFactoryImplemented(
      AccessRuleQueryFactoryImplemented accessRuleQueryFactoryImplemented) {
    this.accessRuleQueryFactoryImplemented = accessRuleQueryFactoryImplemented;
  }

  private AuthorizedContactRepository authorizedContactRepository;

  @Autowired
  void setAuthorozedContactRepository(AuthorizedContactRepository authorozedContactRepository) {
    this.authorizedContactRepository = authorozedContactRepository;
  }

  private CacheMainParams cacheMainParams;

  @Autowired
  public void setCacheMainParams(CacheMainParams cacheMainParams) {
    this.cacheMainParams = cacheMainParams;
  }

  private HttpRequestService httpRequestService;

  @Autowired
  public void setHttpRequestService(HttpRequestService httpRequestService) {
    this.httpRequestService = httpRequestService;
  }

  public Map<String, Object> getCorrectPermissionsForAdminPage(Map<String, Object> permissions) {
    if (permissions == null)
      return null;
    List<DynamicRoleModelAbstract> presentRoles =
        dynamicRoleRepository.getDynamicRolesByCompanyServiceContact(
            UUID.fromString((String) permissions
                .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID))),
            UUID.fromString((String) permissions
                .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.SERVICE_ID))),
            UUID.fromString((String) permissions
                .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID))));

    DynamicRoleModelAbstract foundRole = DBConstants.getDefaultRoleForList(presentRoles, null);
    if (foundRole != null) {
      return new HashMap() {
        {
          put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID),
              foundRole.getCompanyId().toString());
          put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.SERVICE_ID),
              foundRole.getServiceId().toString());
          put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID),
              foundRole.getRoleId().toString());
          put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID),
              foundRole.getId().toString());
          put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID), (String) permissions
              .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID)));
        }
      };
    }
    return null;
  }

  public Map<String, Object> getCorrectPermissionsForAdminPage(UserDetails userDetail,
      UUID classId) {
    Map<String, Object> permissions = accessRulesByUserPermissionsFilter(userDetail, classId);
    if (permissions == null)
      return null;
    return getCorrectPermissionsForAdminPage(permissions);
  }

  public Map<String, Object> getCorrectPermissionsForAdminPage(UUID classId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetail = (UserDetails) auth.getPrincipal();
    Map<String, Object> permissions = accessRulesByUserPermissionsFilter(userDetail, classId);
    if (permissions == null)
      return null;
    return getCorrectPermissionsForAdminPage(permissions);
  }

  public Map<String, Object> accessRulesByUserPermissionsFilter(UserDetails userDetail,
      UUID classId) {
    if (classId == null) {
      return null;
    }
    return accessRuleQueryFactoryImplemented.getResultPermissionForAccess(classId,
        (Map<String, Object>) authorizedContactRepository
            .getAuthorizedContactByName(userDetail.getUsername()).getPermissions()
            .get(MapKey.PERMISSIONS));
  }

  public AuthorizedContactAbstract getAuthorizedContactByLogin() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetail = (UserDetails) auth.getPrincipal();
    return authorizedContactRepository.getAuthorizedContactByName(userDetail.getUsername());
  }

  public UUID initUserByIncomingRequestForManagement(String keyCollectionCase, String tokenLng)
      throws NullPointerException {
    String errorMessage = null;
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetail = (UserDetails) auth.getPrincipal();
    AuthorizedContactAbstract authorizedContact =
        authorizedContactRepository.getAuthorizedContactByName(userDetail.getUsername());
    Map<String, Object> selfAdminPermissions = getCorrectPermissionsForAdminPage(
        (Map<String, Object>) authorizedContact.getPermissions().get(MapKey.PERMISSIONS));
    if (selfAdminPermissions == null)
      throw new NullPointerException("Not found admin permissions!!!");
    log.info("initUserByIncomingRequestForManagement authorizedContact: {}", selfAdminPermissions);
    authorizedContact.setRoleId(UUID.fromString((String) selfAdminPermissions
        .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID))));
    authorizedContact.setDynamicRoleId(UUID.fromString((String) selfAdminPermissions
        .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID))));
    errorMessage = cacheMainParams.initContact(authorizedContact,
        httpRequestService.getKeyToken(keyCollectionCase), tokenLng);
    if (errorMessage != null) {
      throw new NullPointerException("Cant init contact.");
    }
    return authorizedContact.getContactId();
  }

  public String initUserByIncomingRequestForself(String keyCollectionCase, String tokenLng) {
    String errorMessage = null;
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetail = (UserDetails) auth.getPrincipal();
    AuthorizedContactAbstract authorizedContact =
        authorizedContactRepository.getAuthorizedContactByName(userDetail.getUsername());
    cacheMainParams.initContact(authorizedContact,
        httpRequestService.getKeyToken(keyCollectionCase), tokenLng);
    return errorMessage;
  }



}
