package com.matas.liteconstruct.db.models.serviceauthorized.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.serviceauthorized.abstractmodel.AuthorizedContactAbstract;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthorizedContactRepositoryImplemented implements AuthorizedContactRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addAuthorizedContact(AuthorizedContactAbstract authorizedContact) {
    String sql = String.format(
        "insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s', '%7$s', '%8$s', '%9$s')",
        DBConstants.TBL_SITE_AUTHORIZED_CONTACTS, authorizedContact.getContactId(),
        authorizedContact.getName(), authorizedContact.getDynamicRoleId(),
        authorizedContact.getCompanyId(), authorizedContact.getServiceId(),
        authorizedContact.getRoleId(), authorizedContact.getDomainId(), authorizedContact.getCompanyContactClassId());
    
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeAuthorizedContact(AuthorizedContactAbstract authorizedContact) {
    String sql = String.format("delete from %1$s where name = '%2$s'",
        DBConstants.TBL_SITE_AUTHORIZED_CONTACTS, authorizedContact.getName());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateAuthorizedContact(AuthorizedContactAbstract authorizedContact) {
    String sql = String.format(
        "update %1$s set contact_id = '%2$s', dynamic_role_id = '%4$s', company_id = '%5$s', service_id = '%6$s', role_id = '%7$s', domain_id = '%8$s', company_contact_class_id = '%9$s' where name = '%3$s'",
        DBConstants.TBL_SITE_AUTHORIZED_CONTACTS, authorizedContact.getContactId(),
        authorizedContact.getName(), authorizedContact.getDynamicRoleId(),
        authorizedContact.getCompanyId(), authorizedContact.getServiceId(),
        authorizedContact.getRoleId(), authorizedContact.getDomainId(), authorizedContact.getCompanyContactClassId());
    jdbcTemplate.update(sql);
  }

  @Override
  public AuthorizedContactAbstract getAuthorizedContactByNamePassword(String authorizedContactName,
      String authorizedContactPassword) {
    String sql = String.format(
        "SELECT \"%1$s\".*, \"%2$s\".\"class_id\", \"%3$s\".\"80ca0790-c30b-41ba-b74d-868943a3b9cd\" as \"owner_id\" "
            + "FROM \"%1$s\" join \"%2$s\" on \"%1$s\".\"company_id\" = "
            + "\"%2$s\".\"company_id\" join \"%3$s\" on \"%3$s\".\"d2a47321-e0da-4ee5-bc76-110a4e67090c\" = "
            + "\"%1$s\".\"contact_id\" WHERE \"%1$s\".\"name\" = '%4$s' and \"%1$s\".\"password\" = '%5$s'",
        DBConstants.TBL_SITE_AUTHORIZED_CONTACTS, DBConstants.TBL_COMPANY_CONTACT_CLASS_RELATION,
        "cc_7052a1e5-8d00-43fd-8f57-f2e4de0c8b24_data_use", authorizedContactName,
        authorizedContactPassword);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new com.matas.liteconstruct.db.models.serviceauthorized.model.AuthorozedContact(
          (UUID) rows.get(0).get("contact_id"), (UUID) rows.get(0).get("owner_id"),
          (String) rows.get(0).get("name"), (UUID) rows.get(0).get("dynamic_role_id"),
          (UUID) rows.get(0).get("company_id"), (UUID) rows.get(0).get("service_id"),
          (UUID) rows.get(0).get("role_id"), (UUID) rows.get(0).get("domain_id"),
          (UUID) rows.get(0).get("company_contact_class_id"));
    else
      return null;
  }

  @Override
  public AuthorizedContactAbstract getAuthorizedContactByName(String authorizedContactName) {
    String sql = String.format(
        "SELECT \"%1$s\".*, \"%2$s\".\"class_id\", \"%3$s\".\"80ca0790-c30b-41ba-b74d-868943a3b9cd\" as \"owner_id\" "
            + "FROM \"%1$s\" join \"%2$s\" on \"%1$s\".\"company_id\" = "
            + "\"%2$s\".\"company_id\" join \"%3$s\" on \"%3$s\".\"d2a47321-e0da-4ee5-bc76-110a4e67090c\" = "
            + "\"%1$s\".\"contact_id\" WHERE \"%1$s\".\"name\" = '%4$s'",
        DBConstants.TBL_SITE_AUTHORIZED_CONTACTS, DBConstants.TBL_COMPANY_CONTACT_CLASS_RELATION,
        "cc_7052a1e5-8d00-43fd-8f57-f2e4de0c8b24_data_use", authorizedContactName);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new com.matas.liteconstruct.db.models.serviceauthorized.model.AuthorozedContact(
          (UUID) rows.get(0).get("contact_id"), (UUID) rows.get(0).get("owner_id"),
          (String) rows.get(0).get("name"), (UUID) rows.get(0).get("dynamic_role_id"),
          (UUID) rows.get(0).get("company_id"), (UUID) rows.get(0).get("service_id"),
          (UUID) rows.get(0).get("role_id"), (UUID) rows.get(0).get("domain_id"),
          (UUID) rows.get(0).get("company_contact_class_id"));
    else
      return null;
  }

}
