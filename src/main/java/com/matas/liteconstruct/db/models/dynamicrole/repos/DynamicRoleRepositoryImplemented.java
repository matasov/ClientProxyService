package com.matas.liteconstruct.db.models.dynamicrole.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.dynamicrole.model.DynamicRoleModel;
import com.matas.liteconstruct.db.models.serviceauthorized.abstractmodel.AuthorizedContactAbstract;
import com.matas.liteconstruct.db.models.streamliterals.model.LiteralModel;
import com.matas.liteconstruct.service.management.structure.StructureCollectionsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicRoleRepositoryImplemented implements DynamicRoleRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addDynamicRole(DynamicRoleModelAbstract dynamicRole) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s')",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE, dynamicRole.getId(), dynamicRole.getName(),
        dynamicRole.getCompanyId(), dynamicRole.getServiceId(), dynamicRole.getRoleId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeDynamicRole(DynamicRoleModelAbstract dynamicRole) {
    String sql = String.format("delete from %1$s where id = '%2$s'",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE, dynamicRole.getId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateDynamicRole(DynamicRoleModelAbstract dynamicRole) {
    String sql = String.format(
        "update %1$s set name = '%3$s', company_id = '%4$s', service_id = '%5$s', role_id = '%6$s' where id = '%2$s'",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE, dynamicRole.getId(), dynamicRole.getName(),
        dynamicRole.getCompanyId(), dynamicRole.getServiceId(), dynamicRole.getRoleId());
    jdbcTemplate.update(sql);
  }

  @Override
  public DynamicRoleModelAbstract getDynamicRoleById(UUID dynamicRoleId) {
    List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM "
        + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + " WHERE id = '" + dynamicRoleId + "' limit 1");
    if (rows != null && rows.size() > 0)
      return new DynamicRoleModel((UUID) rows.get(0).get("id"), (String) rows.get(0).get("name"),
          (UUID) rows.get(0).get("company_id"), (UUID) rows.get(0).get("service_id"),
          (UUID) rows.get(0).get("role_id"));
    else
      return null;
  }

  // @Override
  // public List<DynamicRoleModelAbstract> getDynamicRoleByAnotherClass(UUID collectionId, UUID
  // anotherclassId, int isActive) {
  //
  // List<Map<String, Object>> rows = jdbcTemplate.queryForList(
  // "SELECT id, class_structure_fields.class, name, fieldclass, class_structure_fields.inner,
  // show_name FROM class_structure_fields WHERE id = '"
  // + id + "' limit 1");
  // if (rows != null && rows.size() > 0) {
  // List<DynamicRoleModelAbstract> result = new ArrayList<>(rows.size());
  // for(Map<String, Object> line : rows) {
  // result.add(new DynamicRoleModel((UUID) line.get("id"), (String) line.get("name"),(UUID)
  // line.get("company_id"),(UUID) line.get("service_id"), (UUID) line.get("role_id")));
  // }
  // return result;
  // }else
  // return null;
  // }
  //
  @Override
  public DynamicRoleModelAbstract getDynamicRoleByCompanyServiceRole(UUID company, UUID service,
      UUID role) {
    String sql = "SELECT * FROM " + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + " WHERE company_id = '"
        + company + "' and service_id = '" + service + "' and role_id = '" + role + "' limit 1";
//    log.info("getDynamicRoleByCompanyServiceRole: {}", sql);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    if (rows != null && !rows.isEmpty()) {
      return new DynamicRoleModel((UUID) rows.get(0).get("id"), (String) rows.get(0).get("name"),
          (UUID) rows.get(0).get("company_id"), (UUID) rows.get(0).get("" + "service_id"),
          (UUID) rows.get(0).get("role_id"));
    } else
      return null;
  }

  @Override
  public List<DynamicRoleModelAbstract> getDynamicRolesByCompanyServiceContact(UUID company,
      UUID service, UUID contact) {
    String sql =
        new StringBuilder().append("SELECT \"").append(DBConstants.REGISTRY_DYNAMIC_ROLE_CONTACT)
            .append("\".\"contact_id\", \"").append(DBConstants.TBL_REGISTRY_DYNAMIC_ROLE)
            .append("\".* FROM \"").append(DBConstants.REGISTRY_DYNAMIC_ROLE_CONTACT)
            .append("\" join \"").append(DBConstants.TBL_REGISTRY_DYNAMIC_ROLE).append("\" on \"")
            .append(DBConstants.TBL_REGISTRY_DYNAMIC_ROLE).append("\".\"id\" = \"")
            .append(DBConstants.REGISTRY_DYNAMIC_ROLE_CONTACT)
            .append("\".\"dynamic_role_id\" where " + "\"")
            .append(DBConstants.REGISTRY_DYNAMIC_ROLE_CONTACT).append("\".\"contact_id\" = '")
            .append(contact).append("' and \"").append(DBConstants.TBL_REGISTRY_DYNAMIC_ROLE)
            .append("\".\"company_id\" = '").append(company).append("' " + "and \"")
            .append(DBConstants.TBL_REGISTRY_DYNAMIC_ROLE).append("\".\"service_id\" = '")
            .append(service).append("'").toString();
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new DynamicRoleModel(CommonMethods.getUUID(rs.getString("id")),
            rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
            CommonMethods.getUUID(rs.getString("service_id")),
            CommonMethods.getUUID(rs.getString("role_id"))));
  }

  @Override
  public List<DynamicRoleModelAbstract> getDynamicRolesByCompanyServiceLogin(UUID company,
      UUID service, String contactLogin) {
    String sql =
        new StringBuilder().append("SELECT \"").append(DBConstants.REGISTRY_DYNAMIC_ROLE_CONTACT)
            .append("\".\"contact_id\", \"").append(DBConstants.TBL_REGISTRY_DYNAMIC_ROLE)
            .append("\".* FROM \"").append(DBConstants.REGISTRY_DYNAMIC_ROLE_CONTACT)
            .append("\" join \"").append(DBConstants.TBL_REGISTRY_DYNAMIC_ROLE).append("\" on \"")
            .append(DBConstants.TBL_REGISTRY_DYNAMIC_ROLE).append("\".\"id\" = \"")
            .append(DBConstants.REGISTRY_DYNAMIC_ROLE_CONTACT)
            .append("\".\"dynamic_role_id\" where " + "\"")
            .append(DBConstants.REGISTRY_DYNAMIC_ROLE_CONTACT).append("\".\"contact_id\" = (")
            .append("SELECT \"d2a47321-e0da-4ee5-bc76-110a4e67090c\" FROM \"")
            .append(DBConstants.TBL_AUTH_CONTACT)
            .append("\" WHERE \"060f16c7-7573-413f-8f38-fe8d4bf177aa\" = '").append(contactLogin)

            .append("') and \"").append(DBConstants.TBL_REGISTRY_DYNAMIC_ROLE)
            .append("\".\"company_id\" = '").append(company).append("' " + "and \"")
            .append(DBConstants.TBL_REGISTRY_DYNAMIC_ROLE).append("\".\"service_id\" = '")
            .append(service).append("'").toString();
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new DynamicRoleModel(CommonMethods.getUUID(rs.getString("id")),
            rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
            CommonMethods.getUUID(rs.getString("service_id")),
            CommonMethods.getUUID(rs.getString("role_id"))));
  }

  @Override
  public UUID getContactByLogin(String contactLogin) {
    String sql =
        new StringBuilder().append("SELECT \"d2a47321-e0da-4ee5-bc76-110a4e67090c\" FROM \"")
            .append(DBConstants.TBL_AUTH_CONTACT)
            .append("\" WHERE \"060f16c7-7573-413f-8f38-fe8d4bf177aa\" = '").append(contactLogin)
            .append("'").toString();
    System.out.println("getContactByLogin: " + sql);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && !rows.isEmpty()) {
      return (UUID) rows.get(0).get("d2a47321-e0da-4ee5-bc76-110a4e67090c");
    } else
      return null;
  }

  @Override
  public UUID getContactByLoginPassword(String contactLogin, String contactPassword) {
    String sql =
        new StringBuilder().append("SELECT \"d2a47321-e0da-4ee5-bc76-110a4e67090c\" FROM \"")
            .append(DBConstants.TBL_AUTH_CONTACT)
            .append("\" WHERE \"060f16c7-7573-413f-8f38-fe8d4bf177aa\" = '").append(contactLogin)
            .append("' AND \"28765a7e-fd96-47eb-851f-19f54f149789\" = '").append(contactPassword)
            .append("'").toString();
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && !rows.isEmpty()) {
      return (UUID) rows.get(0).get("d2a47321-e0da-4ee5-bc76-110a4e67090c");
    } else
      return null;
  }

}
