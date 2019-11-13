package com.matas.liteconstruct.db.models.security.repos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepository;
import com.matas.liteconstruct.db.models.security.model.ContactAuth;
import com.matas.liteconstruct.db.models.security.model.RoleAuth;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContactAuthRepositoryImplemented implements ContactAuthRepository {

  private JdbcTemplate jdbcTemplate;

  private DynamicRoleRepository dynamicRoleRepositoryImplemented;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Autowired
  public void setDynamicRoleRepositoryImplemented(
      DynamicRoleRepository dynamicRoleRepositoryImplemented) {
    this.dynamicRoleRepositoryImplemented = dynamicRoleRepositoryImplemented;
  }

  @Override
  public UUID addContactAuth(ContactAuth authContact) throws NullPointerException {
    if (authContact == null) {
      throw new NullPointerException("Contact data is null!");
    }
    if (authContact.getRoles() == null || authContact.getRoles().isEmpty())
      throw new NullPointerException("Not found dynamic role for contact.");
    UUID recordOwnerId = UUID.randomUUID();
    if (!isPresentValueInDB(authContact.getRoles().get(0).getCompanyId(),
        UUID.fromString(DBConstants.FLD_AUTH_COMPANY_ID),
        UUID.fromString(DBConstants.COMPANY_ID))) {
      throw new NullPointerException("Not found value for company.");
    } else if (!isPresentValueInDB(authContact.getRoles().get(0).getServiceId(),
        UUID.fromString(DBConstants.FLD_AUTH_SERVICE_ID),
        UUID.fromString(DBConstants.SERVICE_ID))) {
      throw new NullPointerException("Not found value for service.");
    } else if (!isPresentValueInDB(authContact.getRoles().get(0).getRoleId(),
        UUID.fromString(DBConstants.FLD_AUTH_ROLE_ID), UUID.fromString(DBConstants.ROLE_ID))) {
      throw new NullPointerException("Not found value for role.");
    }
    if (authContact.getId() == null) {
      authContact.setId(UUID.randomUUID());
    }
    DynamicRoleModelAbstract presentDynamicRole = dynamicRoleRepositoryImplemented
        .getDynamicRoleByCompanyServiceRole(authContact.getRoles().get(0).getCompanyId(),
            authContact.getRoles().get(0).getServiceId(),
            authContact.getRoles().get(0).getRoleId());
    if (presentDynamicRole == null) {
      throw new NullPointerException("Not found value for dynamic role.");
    }

    String sqlDynamicRoleContact = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s')",
        DBConstants.REGISTRY_DYNAMIC_ROLE_CONTACT, UUID.randomUUID(), presentDynamicRole.getId(),
        authContact.getId());
    jdbcTemplate.update(sqlDynamicRoleContact);

    String sqlRecordOwner = String.format(
        "insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s')",
        DBConstants.TBL_RECORD_OWNER, recordOwnerId, authContact.getId(),
        authContact.getRoles().get(0).getCompanyId(), authContact.getRoles().get(0).getServiceId(),
        authContact.getRoles().get(0).getRoleId());
    jdbcTemplate.update(sqlRecordOwner);

    String extraFields = "";
    String extraValues = "";
    if (authContact.getContactExtraData() != null && !authContact.getContactExtraData().isEmpty()) {
      List<String> extraStrings = getSubstringForAddRecord(authContact.getContactExtraData());
      extraFields = extraStrings.get(0);
      extraValues = extraStrings.get(1);
    }
    String sql = String.format(
        "insert into \"%1$s\" (\"%2$s\", \"%3$s\", \"%4$s\", \"%5$s\" %10$s) values ('%6$s', '%7$s', '%8$s', '%9$s' %11$s)",
        DBConstants.TBL_AUTH_CONTACT, DBConstants.FLD_AUTH_CONTACT_ID,
        DBConstants.FLD_AUTH_CONTACT_OWNER, "060f16c7-7573-413f-8f38-fe8d4bf177aa",
        "28765a7e-fd96-47eb-851f-19f54f149789", authContact.getId(), recordOwnerId,
        authContact.getLogin(), authContact.getPassword(), extraFields, extraValues);
    jdbcTemplate.update(sql);
    return recordOwnerId;
  }

  private List<String> getSubstringForAddRecord(Map<String, String> additionals) {
    if (additionals == null || additionals.isEmpty()) {
      return null;
    }
    List<String> result = new ArrayList<>(2);
    result.add("");
    result.add("");

    try {
      additionals.entrySet().stream()
          .filter(x -> x.getKey() != null && !x.getKey().equals("")
              && !x.getKey().equalsIgnoreCase("d2a47321-e0da-4ee5-bc76-110a4e67090c")
              && !x.getKey().equalsIgnoreCase("80ca0790-c30b-41ba-b74d-868943a3b9cd")
              && !x.getKey().equalsIgnoreCase("a09c3abf-6030-4625-856e-5391aa511590")
              && !x.getKey().equalsIgnoreCase("d3c85cdf-c344-40b8-b2c2-a3aea7159734"))
          .forEach(x -> {
            result.set(0, (result.get(0) + ", \"" + x.getKey() + "\""));
            result.set(1, (result.get(1) + ", '" + x.getValue() + "'"));
          });
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }

    return result;
  }

  @Override
  public ContactAuth getContactAuthById(UUID authContactId) {
    String sql = new StringBuilder(500).append("SELECT \"registry_dynamic_role\".*, \"")
        .append(DBConstants.FLD_AUTH_CONTACT_LOGIN).append("\" as login, \"")
        .append(DBConstants.FLD_AUTH_CONTACT_PASSWORD).append("\" as password FROM \"")
        .append(DBConstants.TBL_AUTH_CONTACT).append("\" join ")
        .append(
            "(SELECT \"registry_dynamic_role_contact\".\"contact_id\", \"registry_dynamic_role\".* ")
        .append(
            "FROM \"registry_dynamic_role\" join \"registry_dynamic_role_contact\" on \"registry_dynamic_role\".\"id\" = ")
        .append(
            "\"registry_dynamic_role_contact\".\"dynamic_role_id\") as \"registry_dynamic_role\" ")
        .append("on \"registry_dynamic_role\".\"contact_id\" = \"")
        .append(DBConstants.TBL_AUTH_CONTACT).append("\".\"")
        .append(DBConstants.FLD_AUTH_CONTACT_ID).append("\" " + "where \"")
        .append(DBConstants.TBL_AUTH_CONTACT).append("\".\"")
        .append(DBConstants.FLD_AUTH_CONTACT_ID).append("\" = '").append(authContactId).append("'")
        .toString();
    List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
    if (list == null || list.isEmpty())
      return null;
    ContactAuth result = new ContactAuth();
    result.setId((UUID) list.get(0).get("contact_id"));
    result.setLogin((String) list.get(0).get("login"));
    result.setPassword((String) list.get(0).get("password"));

    result.setRoles(list.parallelStream()
        .map(x -> new RoleAuth((UUID) x.get("id"), (String) x.get("name"),
            (UUID) x.get("company_id"), (UUID) x.get("service_id"), (UUID) x.get("role_id")))
        .collect(Collectors.toList()));
    return result;
  }

  @Override
  public ContactAuth getContactAuthByLogin(String authContactLogin) {
    List<Map<String, Object>> list = null;
    try {
      String sql = new StringBuilder(500).append("SELECT  \"registry_dynamic_role\".*, \"")
          .append(DBConstants.FLD_AUTH_CONTACT_LOGIN).append("\" as login, \"")
          .append(DBConstants.FLD_AUTH_CONTACT_PASSWORD).append("\" as password FROM \"")
          .append(DBConstants.TBL_AUTH_CONTACT).append("\" join ")
          .append(
              "(SELECT \"registry_dynamic_role_contact\".\"contact_id\", \"registry_dynamic_role\".* ")
          .append(
              "FROM \"registry_dynamic_role\" join \"registry_dynamic_role_contact\" on \"registry_dynamic_role\".\"id\" = ")
          .append(
              "\"registry_dynamic_role_contact\".\"dynamic_role_id\") as \"registry_dynamic_role\" ")
          .append("on \"registry_dynamic_role\".\"contact_id\" = \"")
          .append(DBConstants.TBL_AUTH_CONTACT).append("\".\"")
          .append(DBConstants.FLD_AUTH_CONTACT_ID).append("\" " + "where LOWER(\"")
          .append(DBConstants.TBL_AUTH_CONTACT).append("\".\"")
          .append(DBConstants.FLD_AUTH_CONTACT_LOGIN).append("\") = '")
          .append(authContactLogin.toLowerCase()).append("'").toString();
      list = jdbcTemplate.queryForList(sql);
    } catch (ClassCastException | NullPointerException ex) {
      log.error("error in authorization query.", ex.getLocalizedMessage());
    }
    if (list == null || list.isEmpty())
      return null;
    ContactAuth result = new ContactAuth();
    result.setId((UUID) list.get(0).get("contact_id"));
    result.setLogin((String) list.get(0).get("login"));
    result.setPassword((String) list.get(0).get("password"));

    result.setRoles(list.parallelStream()
        .map(x -> new RoleAuth((UUID) x.get("id"), (String) x.get("name"),
            (UUID) x.get("company_id"), (UUID) x.get("service_id"), (UUID) x.get("role_id")))
        .collect(Collectors.toList()));
    return result;
  }

  @Override
  public boolean checkContactAuthByIdOrLogin(String contactId, String authContactLogin) {
    String sql =
        new StringBuilder(500).append("SELECT  * from \"").append(DBConstants.TBL_AUTH_CONTACT)
            .append("\" WHERE LOWER(\"").append(DBConstants.FLD_AUTH_CONTACT_LOGIN)
            .append("\") = '").append(authContactLogin.toLowerCase()).append("' OR \"")
            .append(DBConstants.FLD_AUTH_CONTACT_ID).append("\" = '").append(contactId).append("'")
            .toString();
    List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
    return (list != null && !list.isEmpty());
  }

  // check present values block

  private boolean isPresentValueInDB(UUID itemID, UUID fieldRecordId, UUID classId)
      throws NullPointerException {
    if (itemID == null || fieldRecordId == null || classId == null) {
      throw new NullPointerException("Arguments is null!");
    }
    String sql = "select * from \"cc_" + classId + "_data_use\" where \"" + fieldRecordId + "\" = '"
        + itemID + "'";
    List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
    if (list != null && !list.isEmpty()) {
      return true;
    } else {
      return false;
    }
  }

}
