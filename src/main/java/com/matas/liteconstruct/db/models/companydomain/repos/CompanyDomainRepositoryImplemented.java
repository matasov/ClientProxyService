package com.matas.liteconstruct.db.models.companydomain.repos;


import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.companydomain.abstractmodel.CompanyDomainAbstract;
import com.matas.liteconstruct.db.models.companydomain.abstractmodel.RecaptchaByDomainAbstract;
import com.matas.liteconstruct.db.models.companydomain.model.CompanyDomain;
import com.matas.liteconstruct.db.models.companydomain.model.RecaptchaByDomain;
import com.matas.liteconstruct.service.signup.SignupServiceHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompanyDomainRepositoryImplemented implements CompanyDomainRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addCompanyDomain(CompanyDomainAbstract companyDomain) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s')",
        DBConstants.TBL_SITE_DOMAINS, companyDomain.getId(), companyDomain.getValue(),
        companyDomain.getSsl(), companyDomain.getCompanyId(), companyDomain.getServiceId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeCompanyDomain(CompanyDomainAbstract companyDomain) {
    String sql = String.format("delete from %1$s where id = '%2$s'", DBConstants.TBL_SITE_DOMAINS,
        companyDomain.getId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateCompanyDomain(CompanyDomainAbstract companyDomain) {
    String sql = String.format(
        "update %1$s set value = '%3$s', is_ssl = '%4$s', company_id = '%5$s', service_id = '%6$s' where id = '%2$s'",
        DBConstants.TBL_SITE_DOMAINS, companyDomain.getId(), companyDomain.getValue(),
        companyDomain.getSsl(), companyDomain.getCompanyId(), companyDomain.getServiceId());
    jdbcTemplate.update(sql);
  }

  @Override
  public CompanyDomainAbstract getCompanyDomainById(UUID companyDomainId) {
    List<Map<String, Object>> rows =
        jdbcTemplate.queryForList(String.format("SELECT * FROM %1$s WHERE id = '%2$s' limit 1",
            DBConstants.TBL_SITE_DOMAINS, companyDomainId));
    if (rows != null && rows.size() > 0)
      return new CompanyDomain((UUID) rows.get(0).get("id"), (String) rows.get(0).get("value"),
          (boolean) rows.get(0).get("is_ssl"), (UUID) rows.get(0).get("company_id"),
          (UUID) rows.get(0).get("service_id"));
    else
      return null;
  }

  @Override
  public CompanyDomainAbstract getCompanyDomainByCompanyService(UUID company, UUID service) {
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(String.format(
        "SELECT * FROM %1$s WHERE company_id = '%2$s' and service_id = '%3$s' limit 1",
        DBConstants.TBL_SITE_DOMAINS, company, service));
    if (rows != null && rows.size() > 0)
      return new CompanyDomain((UUID) rows.get(0).get("id"), (String) rows.get(0).get("value"),
          (boolean) rows.get(0).get("is_ssl"), (UUID) rows.get(0).get("company_id"),
          (UUID) rows.get(0).get("service_id"));
    else
      return null;
  }

  @Override
  public List<CompanyDomainAbstract> getCompanyDomainsByCompany(UUID company) {
    String sql = String.format("SELECT * FROM %1$s WHERE company_id = '%2$s'",
        DBConstants.TBL_SITE_DOMAINS, company);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new CompanyDomain(CommonMethods.getUUID(rs.getString("id")),
            rs.getString("value"), rs.getBoolean("is_ssl"),
            CommonMethods.getUUID(rs.getString("company_id")),
            CommonMethods.getUUID(rs.getString("service_id"))));
  }

  @Override
  public CompanyDomainAbstract getCompanyDomainByValue(String value) {
    String sql = String.format("SELECT * FROM %1$s WHERE value = '%2$s' limit 1",
        DBConstants.TBL_SITE_DOMAINS, value);
    log.info("getCompanyDomainByValue: {}", sql);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new CompanyDomain((UUID) rows.get(0).get("id"), (String) rows.get(0).get("value"),
          (boolean) rows.get(0).get("is_ssl"), (UUID) rows.get(0).get("company_id"),
          (UUID) rows.get(0).get("service_id"));
    else
      return null;
  }

  @Override
  public RecaptchaByDomainAbstract getRecaptchaByValue(String value) {
    // List<Map<String, Object>> rows = jdbcTemplate.queryForList(String.format(
    // "SELECT * FROM %1$s WHERE value = '%2$s' limit 1",
    // DBConstants.TBL_SITE_DOMAINS, value));
    // if (rows != null && rows.size() > 0)
    // return new RecaptchaByDomain((UUID) rows.get(0).get("id"), (String) rows.get(0).get("value"),
    // (boolean) rows.get(0).get("is_ssl"), (UUID) rows.get(0).get("company_id"),
    // (UUID) rows.get(0).get("service_id"));
    // else
    return null;
  }

}
