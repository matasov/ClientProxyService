package com.matas.liteconstruct.db.models.collectiondynamicrole.repos;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.collectioncase.model.CollectionCase;
import com.matas.liteconstruct.db.models.collectiondynamicrole.abstractmodel.CollectionDynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.collectiondynamicrole.model.CollectionDynamicRoleModel;
import com.matas.liteconstruct.db.models.dynamicrole.model.DynamicRoleModel;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepositoryImplemented;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CollectionDynamicRoleRepositoryImplemented implements CollectionDynamicRoleRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addCollectionDynamicRole(CollectionDynamicRoleModelAbstract dynamicRole) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s')",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, dynamicRole.getId(),
        dynamicRole.getDynamicRole().getId(), dynamicRole.getCollectionId(),
        dynamicRole.getClassId(), dynamicRole.getActive());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeCollectionDynamicRole(CollectionDynamicRoleModelAbstract dynamicRole) {
    String sql = String.format("delete from %1$s where id = '%2$s'",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, dynamicRole.getId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeCollectionDynamicRolesByclassId(UUID classId) {
    String sql = String.format("delete from %1$s where class_id = '%2$s'",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, classId);
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateCollectionDynamicRole(CollectionDynamicRoleModelAbstract dynamicRole) {
    String sql = String.format(
        "update %1$s set dynamic_role_id = '%3$s', collection_id = '%4$s', class_id = '%5$s', active = '%6$s' where id = '%2$s'",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, dynamicRole.getId(),
        dynamicRole.getDynamicRole().getId(), dynamicRole.getCollectionId(),
        dynamicRole.getClassId(), dynamicRole.getActive());
    jdbcTemplate.update(sql);
  }

  @Override
  public List<CollectionDynamicRoleModelAbstract> getCollectionDynamicRoleByclassId(UUID classId) {
    String sql = String.format(
        "SELECT %1$s.*, %1$s.class_id as c_id, %3$s.case_key FROM (SELECT %1$s.*, %2$s.class_id, %2$s.collection_id, %2$s.active "
            + "FROM %1$s JOIN %2$s ON %1$s.id = %2$s.dynamic_role_id) as %1$s left join %3$s on %1$s.collection_id = "
            + "%3$s.collection_id where %1$s.class_id = '%4$s'",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE, DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION,
        DBConstants.TBL_COLLECTION_CASE, classId);

    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new CollectionDynamicRoleModel(CommonMethods.getUUID(rs.getString("c_id")),
            CommonMethods.getUUID(rs.getString("collection_id")), rs.getInt("active"),
            CommonMethods.getUUID(rs.getString("class_id")),
            new DynamicRoleModel(CommonMethods.getUUID(rs.getString("id")), rs.getString("name"),
                CommonMethods.getUUID(rs.getString("company_id")),
                CommonMethods.getUUID(rs.getString("service_id")),
                CommonMethods.getUUID(rs.getString("role_id"))),
            rs.getString("case_key") == null ? new CollectionCase()
                : new CollectionCase(rs.getString("case_key"),
                    CommonMethods.getUUID(rs.getString("class_id")),
                    CommonMethods.getUUID(rs.getString("id")),
                    CommonMethods.getUUID(rs.getString("collection_id")))));
  }

  @Override
  public List<CollectionDynamicRoleModelAbstract> getCollectionDynamicRoleByDroleClass(
      UUID dynamicRoleId, UUID classId, int active) {
    String sql = String.format(
        "SELECT %1$s.*, %1$s.class_id as c_id, %3$s.case_key FROM (SELECT %1$s.*, %2$s.class_id, %2$s.collection_id, %2$s.active "
            + "FROM %1$s JOIN %2$s ON %1$s.id = %2$s.dynamic_role_id) as %1$s left join %3$s on %1$s.collection_id = "
            + "%3$s.collection_id where %1$s.class_id = '%4$s' and %1$s.id = '%5$s'",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE, DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION,
        DBConstants.TBL_COLLECTION_CASE, classId, dynamicRoleId);
    // String sql = "SELECT " + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + ".*, "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".class_id, "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".id as c_id, "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".collection_id, "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".active FROM "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + " inner join "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + " on "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + ".id = "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".dynamic_role_id where "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + ".id = '" + dynamicRoleId + "' and "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".class_id = '" + classId + "'";
    if (active > -1) {
      sql += " and " + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + ".active = '" + active + "'";
    }
    log.info("getCollectionDynamicRoleByDroleClass: {}", sql);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new CollectionDynamicRoleModel(CommonMethods.getUUID(rs.getString("c_id")),
            CommonMethods.getUUID(rs.getString("collection_id")), rs.getInt("active"),
            CommonMethods.getUUID(rs.getString("class_id")),
            new DynamicRoleModel(CommonMethods.getUUID(rs.getString("id")), rs.getString("name"),
                CommonMethods.getUUID(rs.getString("company_id")),
                CommonMethods.getUUID(rs.getString("service_id")),
                CommonMethods.getUUID(rs.getString("role_id"))),
            rs.getString("case_key") == null ? new CollectionCase()
                : new CollectionCase(rs.getString("case_key"),
                    CommonMethods.getUUID(rs.getString("class_id")),
                    CommonMethods.getUUID(rs.getString("id")),
                    CommonMethods.getUUID(rs.getString("collection_id")))));
  }

  @Override
  public CollectionDynamicRoleModelAbstract getCollectionDynamicRoleByCollectionId(
      UUID collectionId) {
    String sql = String.format(
        "SELECT %1$s.*, %1$s.class_id as c_id, %3$s.case_key FROM (SELECT %1$s.*, %2$s.class_id, %2$s.collection_id, %2$s.active "
            + "FROM %1$s JOIN %2$s ON %1$s.id = %2$s.dynamic_role_id) as %1$s left join %3$s on %1$s.collection_id = "
            + "%3$s.collection_id where %1$s.collection_id = '%4$s'",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE, DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION,
        DBConstants.TBL_COLLECTION_CASE, collectionId);
    // String sql = "SELECT " + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + ".*, "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".class_id, "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".id as c_id, "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".collection_id, "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".active FROM "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + " inner join "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + " on "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + ".id = "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".dynamic_role_id where "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".collection_id = '" + collectionId
    // + "'";

    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new CollectionDynamicRoleModel(CommonMethods.getUUID(rs.getString("c_id")),
            CommonMethods.getUUID(rs.getString("collection_id")), rs.getInt("active"),
            CommonMethods.getUUID(rs.getString("class_id")),
            new DynamicRoleModel(CommonMethods.getUUID(rs.getString("id")), rs.getString("name"),
                CommonMethods.getUUID(rs.getString("company_id")),
                CommonMethods.getUUID(rs.getString("service_id")),
                CommonMethods.getUUID(rs.getString("role_id"))),
            rs.getString("case_key") == null ? new CollectionCase()
                : new CollectionCase(rs.getString("case_key"),
                    CommonMethods.getUUID(rs.getString("class_id")),
                    CommonMethods.getUUID(rs.getString("id")),
                    CommonMethods.getUUID(rs.getString("collection_id")))))
        .get(0);
  }

  @Override
  public CollectionDynamicRoleModelAbstract getCollectionDynamicRoleByCompanyServiceRole(
      UUID classId, UUID company, UUID service, UUID role, int active) {
    String sql = String.format(
        "SELECT %1$s.*, %1$s.class_id as c_id, %3$s.case_key FROM (SELECT %1$s.*, %2$s.class_id, %2$s.collection_id, %2$s.active "
            + "FROM %1$s JOIN %2$s ON %1$s.id = %2$s.dynamic_role_id) as %1$s left join %3$s on %1$s.collection_id = "
            + "%3$s.collection_id where %1$s.company_id = '%4$s' and %1$s.service_id = '%5$s' and %1$s.role_id = '%6$s' and %1$s.class_id = '%7$s'",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE, DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION,
        DBConstants.TBL_COLLECTION_CASE, company, service, role, classId);
    // String sql = "SELECT " + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + ".*, "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".class_id, "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".id as c_id, "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".collection_id, "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".active FROM "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + " inner join "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + " on "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + ".id = "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION + ".dynamic_role_id where "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + ".company_id = '" + company + "' and "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + ".service_id = '" + service + "' and "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + ".role_id = '" + role + "' and "
    // + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION
    // + ".class_id = 'fd27729c-0f30-444b-a124-e3e16069e7d0'";
    if (active > -1) {
      sql += " and " + DBConstants.TBL_REGISTRY_DYNAMIC_ROLE + ".active = '" + active + "'";
    }
    // List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    // if (rows != null && rows.size() > 0) {
    // List<CollectionDynamicRoleModelAbstract> result = new ArrayList<>(rows.size());
    // for (Map<String, Object> line : rows) {
    // result.add(new CollectionDynamicRoleModel((UUID) line.get("c_id"),
    // (UUID) line.get("collection_id"), (UUID) line.get("class_id"), (int) line.get("active"),
    // new DynamicRoleModel((UUID) line.get("id"), (String) line.get("name"),
    // (UUID) line.get("company_id"), (UUID) line.get("service_id"),
    // (UUID) line.get("role_id"))));
    // }
    // return result;
    // } else
    // return null;
    List<CollectionDynamicRoleModelAbstract> result = jdbcTemplate.query(sql,
        (rs, rowNum) -> new CollectionDynamicRoleModel(CommonMethods.getUUID(rs.getString("c_id")),
            CommonMethods.getUUID(rs.getString("collection_id")), rs.getInt("active"),
            CommonMethods.getUUID(rs.getString("class_id")),
            new DynamicRoleModel(CommonMethods.getUUID(rs.getString("id")), rs.getString("name"),
                CommonMethods.getUUID(rs.getString("company_id")),
                CommonMethods.getUUID(rs.getString("service_id")),
                CommonMethods.getUUID(rs.getString("role_id"))),
            rs.getString("case_key") == null ? new CollectionCase()
                : new CollectionCase(rs.getString("case_key"),
                    CommonMethods.getUUID(rs.getString("class_id")),
                    CommonMethods.getUUID(rs.getString("id")),
                    CommonMethods.getUUID(rs.getString("collection_id")))));
    if (result != null && !result.isEmpty()) {
      return result.get(0);
    }
    return null;
  }

}
