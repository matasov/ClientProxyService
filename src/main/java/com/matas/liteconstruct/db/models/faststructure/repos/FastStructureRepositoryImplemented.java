package com.matas.liteconstruct.db.models.faststructure.repos;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.collectioncase.model.CollectionCase;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.model.FastStructureModel;
import com.matas.liteconstruct.db.models.faststructure.model.FastStructureModelManagement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FastStructureRepositoryImplemented implements FastStructureRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addFastStructure(FastStructureModelAbstract fastStructure) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s')",
        DBConstants.TBL_CLASS_STRUCTURE_USE_FAST, fastStructure.getDynamicRoleId(),
        fastStructure.getCollectionId(), fastStructure.getClassId(),
        fastStructure.getFastStructureJSON());
    log.info("addFastStructure: {}", sql);
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeFastStructure(FastStructureModelAbstract fastStructure) {
    String sql =
        String.format("delete from %1$s where dynamic_role_id = '%2$s' AND collection_id = '%3$s'",
            DBConstants.TBL_CLASS_STRUCTURE_USE_FAST, fastStructure.getDynamicRoleId(),
            fastStructure.getCollectionId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeFastStructuresByclassId(UUID classId) {
    String sql = String.format("delete from %1$s where class_id = '%2$s'",
        DBConstants.TBL_CLASS_STRUCTURE_USE_FAST, classId);
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateFastStructure(FastStructureModelAbstract fastStructure) {
    String sql = String.format(
        "update %1$s set json_structure = '%4$s' where dynamic_role_id = '%2$s' AND collection_id = '%3$s'",
        DBConstants.TBL_CLASS_STRUCTURE_USE_FAST, fastStructure.getDynamicRoleId(),
        fastStructure.getCollectionId(), fastStructure.getFastStructureJSON());
    log.info("updateFastStructure: {}", sql);
    jdbcTemplate.update(sql);
  }

  @Override
  public List<FastStructureModelAbstract> getFastStructuresForClass(UUID classId) {
    String sql =
        String.format(
            new StringBuilder()
                .append("SELECT %1$s.*, %3$s.case_key FROM (SELECT %1$s.*, %2$s.active FROM %1$s ")
                .append("JOIN %2$s ON %1$s.collection_id = %2$s.collection_id) as %1$s left join ")
                .append(
                    "%3$s on %1$s.collection_id = %3$s.collection_id WHERE %1$s.class_id = '%4$s'")
                .toString(),
            DBConstants.TBL_CLASS_STRUCTURE_USE_FAST,
            DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, DBConstants.TBL_COLLECTION_CASE,
            classId);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new FastStructureModel(
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("collection_id")),
            CommonMethods.getUUID(rs.getString("class_id")), rs.getString("json_structure"),
            rs.getInt("active") == 1,
            rs.getString("case_key") == null ? new CollectionCase()
                : new CollectionCase(rs.getString("case_key"),
                    CommonMethods.getUUID(rs.getString("class_id")),
                    CommonMethods.getUUID(rs.getString("collection_id")),
                    CommonMethods.getUUID(rs.getString("dynamic_role_id")))));
  }

  @Override
  public List<FastStructureModelAbstract> getFastStructuresForDynamicRoleAndCollection(
      UUID dynamicRoleId, UUID collectionId) {
    String sql = String.format(new StringBuilder()
        .append("SELECT %1$s.*, %3$s.case_key FROM (SELECT %1$s.*, %2$s.active FROM %1$s ")
        .append("JOIN %2$s ON %1$s.collection_id = %2$s.collection_id) as %1$s left join ")
        .append(
            "%3$s on %1$s.collection_id = %3$s.collection_id WHERE %1$s.dynamic_role_id = '%4$s' and %1$s.collection_id = '%5$s'")
        .toString(), DBConstants.TBL_CLASS_STRUCTURE_USE_FAST,
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, DBConstants.TBL_COLLECTION_CASE,
        dynamicRoleId, collectionId);

    log.info("getFastStructuresForDynamicRoleAndCollection: {}", sql);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new FastStructureModel(
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("collection_id")),
            CommonMethods.getUUID(rs.getString("class_id")), rs.getString("json_structure"),
            rs.getInt("active") == 1,
            rs.getString("case_key") == null ? new CollectionCase()
                : new CollectionCase(rs.getString("case_key"),
                    CommonMethods.getUUID(rs.getString("class_id")),
                    CommonMethods.getUUID(rs.getString("collection_id")),
                    CommonMethods.getUUID(rs.getString("dynamic_role_id")))));

  }

  @Override
  public FastStructureModelAbstract getActiveFastStructuresForClassByDynamicRole(UUID dynamicRoleId,
      UUID classId, String keyCollectionCase) {
    String sql = String.format(new StringBuilder()
        .append("SELECT %1$s.*, %3$s.case_key FROM (SELECT %1$s.*, %2$s.active FROM %1$s ")
        .append("JOIN %2$s ON %1$s.collection_id = %2$s.collection_id) as %1$s left join ")
        .append(
            "%3$s on %1$s.collection_id = %3$s.collection_id WHERE %1$s.dynamic_role_id = '%4$s'")
        .append(" and %1$s.class_id = '%5$s'").toString(), DBConstants.TBL_CLASS_STRUCTURE_USE_FAST,
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, DBConstants.TBL_COLLECTION_CASE,
        dynamicRoleId, classId);
    if (keyCollectionCase == null) {
      sql += String.format(" and %1$s.active = '1'", DBConstants.TBL_CLASS_STRUCTURE_USE_FAST);
    } else {
      sql += String.format(" and %1$s.active = '1' or %2$s.case_key = '%3$s'",
          DBConstants.TBL_CLASS_STRUCTURE_USE_FAST, DBConstants.TBL_COLLECTION_CASE,
          keyCollectionCase);
    }
    log.info("getActiveFastStructuresForClassByDynamicRole: {}", sql);

    List<FastStructureModelAbstract> result = jdbcTemplate.query(sql,
        (rs, rowNum) -> new FastStructureModel(
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("collection_id")),
            CommonMethods.getUUID(rs.getString("class_id")), rs.getString("json_structure"),
            rs.getInt("active") == 1,
            rs.getString("case_key") == null ? new CollectionCase()
                : new CollectionCase(rs.getString("case_key"),
                    CommonMethods.getUUID(rs.getString("class_id")),
                    CommonMethods.getUUID(rs.getString("collection_id")),
                    CommonMethods.getUUID(rs.getString("dynamic_role_id")))));
    if (result == null)
      return null;
    FastStructureModelAbstract keyCaseStructure = result.parallelStream()
        .filter(item -> item.getCollectionCase().getMetaKey() != null).findAny().orElse(null);
    if (keyCaseStructure == null) {
      return result.get(0);
    } else {
      return keyCaseStructure;
    }
  }

  @Override
  public List<FastStructureModelManagement> getAllFastStructuresForClassByCompany(UUID classId,
      UUID companyId) {
    String sql = String.format(new StringBuilder().append("SELECT %1$s.*, %4$s.case_key, ").append(
        "(SELECT \"6235200f-5c07-4aa3-8ead-ff37c2317a4b\" FROM \"cc_2ed029b6-d745-4f85-8d9f-2dccd2a7da37_data_use\" WHERE \"d700550b-7890-4836-91ae-b52e8a4cde6d\" = \"%1$s\".\"company_id\" limit 1) as \"company_name\", (SELECT \"d76789d5-3812-46a1-9a63-2125802b632f\" FROM \"cc_3db2f640-e01a-42ac-904e-87a46e0373fd_data_use\" WHERE \"b5afac44-2df9-42b5-88c3-694e63d3dd0a\" = \"%1$s\".\"service_id\" limit 1) as \"service_name\", (SELECT \"f5aa4922-2a94-464b-b658-d8893fb8e614\" FROM \"cc_97086af0-956b-4380-a385-ea823cff377a_data_use\" WHERE \"ea4d5b30-1c60-4bce-a2cd-452e9b075434\" = \"%1$s\".\"role_id\" limit 1) as \"role_name\"")
        .append("FROM (SELECT %1$s.*, ")

        .append("%2$s.company_id, %2$s.active, %2$s.service_id, %2$s.role_id FROM %1$s JOIN ")
        .append(
            "(SELECT %2$s.*, %3$s.company_id, %3$s.service_id, %3$s.role_id FROM %2$s JOIN %3$s ON ")
        .append("%2$s.dynamic_role_id = %3$s.id) as %2$s ON %1$s.collection_id = ")
        .append("%2$s.collection_id) as %1$s left join %4$s on %1$s.collection_id = ")
        .append("%4$s.collection_id where %1$s.class_id = '%5$s' and %1$s.company_id = '%6$s'")
        .toString(), DBConstants.TBL_CLASS_STRUCTURE_USE_FAST,
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, DBConstants.TBL_REGISTRY_DYNAMIC_ROLE,
        DBConstants.TBL_COLLECTION_CASE, classId, companyId);

    log.info("getAllFastStructuresForClassByCompany: {}", sql);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new FastStructureModelManagement(
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("collection_id")),
            CommonMethods.getUUID(rs.getString("class_id")), rs.getString("json_structure"),
            rs.getString("company_name"), rs.getString("service_name"),
            CommonMethods.getUUID(rs.getString("role_id")), rs.getString("role_name"),
            rs.getInt("active") == 1,
            rs.getString("case_key") == null ? new CollectionCase()
                : new CollectionCase(rs.getString("case_key"),
                    CommonMethods.getUUID(rs.getString("class_id")),
                    CommonMethods.getUUID(rs.getString("collection_id")),
                    CommonMethods.getUUID(rs.getString("dynamic_role_id")))));
  }

  @Override
  public boolean checkActiveFastStructuresForClassByCompanyServiceRole(UUID classId, UUID companyId,
      UUID serviceId, UUID roleId) {
    String sql = String.format(new StringBuilder()
        .append("SELECT %1$s.*, %4$s.case_key FROM (SELECT %1$s.*, ")
        .append("%2$s.company_id, %2$s.active, %2$s.service_id, %2$s.role_id FROM %1$s JOIN ")
        .append(
            "(SELECT %2$s.*, %3$s.company_id, %3$s.service_id, %3$s.role_id FROM %2$s JOIN %3$s ON ")
        .append("%2$s.dynamic_role_id = %3$s.id) as %2$s ON %1$s.collection_id = ")
        .append("%2$s.collection_id) as %1$s left join %4$s on %1$s.collection_id = ")
        .append("%4$s.collection_id where %1$s.active = '1' and %1$s.class_id = '%5$s' ")
        .append(
            "and %1$s.company_id = '%6$s' and %1$s.service_id = '%7$s' and %1$s.role_id = '%8$s'")
        .toString(), DBConstants.TBL_CLASS_STRUCTURE_USE_FAST,
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, DBConstants.TBL_REGISTRY_DYNAMIC_ROLE,
        DBConstants.TBL_COLLECTION_CASE, classId, companyId, serviceId, roleId);

    FastStructureModelAbstract item = jdbcTemplate
        .query(sql,
            (rs, rowNum) -> new FastStructureModel(
                CommonMethods.getUUID(rs.getString("dynamic_role_id")),
                CommonMethods.getUUID(rs.getString("collection_id")), classId, null,
                rs.getInt("active") == 1,
                rs.getString("case_key") == null ? new CollectionCase()
                    : new CollectionCase(rs.getString("case_key"),
                        CommonMethods.getUUID(rs.getString("class_id")),
                        CommonMethods.getUUID(rs.getString("collection_id")),
                        CommonMethods.getUUID(rs.getString("dynamic_role_id")))))
        .stream().findAny().orElse(null);
    return item != null;
  }

  @Override
  public void setAllInActiveForClassByCompanyServiceRole(UUID classId, UUID companyId,
      UUID serviceId, UUID roleId) {
    String sql = String.format(
        "update \"%2$s\" set \"active\" = 0 where \"class_id\" = '%6$s' AND \"dynamic_role_id\" in (SELECT \"%1$s\".\"id\" FROM \"%1$s\" WHERE \"company_id\" = '%3$s' AND \"service_id\" = '%4$s' AND \"role_id\" = '%5$s' limit 1)",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE, DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION,
        companyId, serviceId, roleId, classId);
    jdbcTemplate.update(sql);
  }

  @Override
  public List<FastStructureModelAbstract> getAllWhichIncludesClassAndRole(UUID includeClassId,
      List<DynamicRoleModelAbstract> whiteList, List<DynamicRoleModelAbstract> blackList) {

    String value = new StringBuilder().append("SELECT %1$s.*, %4$s.case_key, ").append(
        "(SELECT \"6235200f-5c07-4aa3-8ead-ff37c2317a4b\" FROM \"cc_2ed029b6-d745-4f85-8d9f-2dccd2a7da37_data_use\" WHERE \"d700550b-7890-4836-91ae-b52e8a4cde6d\" = \"%1$s\".\"company_id\" limit 1) as \"company_name\", (SELECT \"d76789d5-3812-46a1-9a63-2125802b632f\" FROM \"cc_3db2f640-e01a-42ac-904e-87a46e0373fd_data_use\" WHERE \"b5afac44-2df9-42b5-88c3-694e63d3dd0a\" = \"%1$s\".\"service_id\" limit 1) as \"service_name\", (SELECT \"f5aa4922-2a94-464b-b658-d8893fb8e614\" FROM \"cc_97086af0-956b-4380-a385-ea823cff377a_data_use\" WHERE \"ea4d5b30-1c60-4bce-a2cd-452e9b075434\" = \"%1$s\".\"role_id\" limit 1) as \"role_name\"")
        .append("FROM (SELECT %1$s.*, ")

        .append("%2$s.company_id, %2$s.active, %2$s.service_id, %2$s.role_id FROM %1$s JOIN ")
        .append(
            "(SELECT %2$s.*, %3$s.company_id, %3$s.service_id, %3$s.role_id FROM %2$s JOIN %3$s ON ")
        .append("%2$s.dynamic_role_id = %3$s.id) as %2$s ON %1$s.collection_id = ")
        .append("%2$s.collection_id) as %1$s left join %4$s on %1$s.collection_id = ")
        .append("%4$s.collection_id where %1$s.json_structure::character varying like ").toString();
    String sql = String.format(value, DBConstants.TBL_CLASS_STRUCTURE_USE_FAST,
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, DBConstants.TBL_REGISTRY_DYNAMIC_ROLE,
        DBConstants.TBL_COLLECTION_CASE)
        + new StringBuilder().append("'%").append(includeClassId).append("%'").toString();
    if (whiteList != null) {
      sql += whiteList.parallelStream()
          .map(collection -> String.format("%1$s.dynamic_role_id = '%2$s'",
              DBConstants.TBL_CLASS_STRUCTURE_USE_FAST, collection.getId()))
          .collect(Collectors.joining(" or ", "and (", ")"));
    } else if (blackList != null) {
      sql += blackList.parallelStream()
          .map(collection -> String.format("%1$s.dynamic_role_id != '%2$s'",
              DBConstants.TBL_CLASS_STRUCTURE_USE_FAST, collection.getId()))
          .collect(Collectors.joining(" and ", "and (", ")"));
    }
    log.info("getAllWhichIncludesClassAndRole {}", sql);

    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new FastStructureModel(
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("collection_id")),
            CommonMethods.getUUID(rs.getString("class_id")), rs.getString("json_structure"),
            rs.getInt("active") == 1,
            rs.getString("case_key") == null ? new CollectionCase()
                : new CollectionCase(rs.getString("case_key"),
                    CommonMethods.getUUID(rs.getString("class_id")),
                    CommonMethods.getUUID(rs.getString("collection_id")),
                    CommonMethods.getUUID(rs.getString("dynamic_role_id")))));
  }

  @Override
  public List<FastStructureModelAbstract> getAllDependsStructures(UUID forClassId, UUID companyId,
      UUID serviceId, boolean isStructureUpdate, List<DynamicRoleModelAbstract> whiteList,
      List<DynamicRoleModelAbstract> blackList) {
    String sql = String.format(new StringBuilder().append("SELECT %1$s.*, %4$s.case_key, ").append(
        "(SELECT \"6235200f-5c07-4aa3-8ead-ff37c2317a4b\" FROM \"cc_2ed029b6-d745-4f85-8d9f-2dccd2a7da37_data_use\" ")
        .append(
            "WHERE \"d700550b-7890-4836-91ae-b52e8a4cde6d\" = \"%1$s\".\"company_id\" limit 1) as \"company_name\", ")
        .append(
            "(SELECT \"d76789d5-3812-46a1-9a63-2125802b632f\" FROM \"cc_3db2f640-e01a-42ac-904e-87a46e0373fd_data_use\" ")
        .append(
            "WHERE \"b5afac44-2df9-42b5-88c3-694e63d3dd0a\" = \"%1$s\".\"service_id\" limit 1) as \"service_name\", ")
        .append(
            "(SELECT \"f5aa4922-2a94-464b-b658-d8893fb8e614\" FROM \"cc_97086af0-956b-4380-a385-ea823cff377a_data_use\" ")
        .append(
            "WHERE \"ea4d5b30-1c60-4bce-a2cd-452e9b075434\" = \"%1$s\".\"role_id\" limit 1) as \"role_name\"")
        .append("FROM (SELECT %1$s.*, ")

        .append("%2$s.company_id, %2$s.active, %2$s.service_id, %2$s.role_id FROM %1$s JOIN ")
        .append(
            "(SELECT %2$s.*, %3$s.company_id, %3$s.service_id, %3$s.role_id FROM %2$s JOIN %3$s ON ")
        .append("%2$s.dynamic_role_id = %3$s.id) as %2$s ON %1$s.collection_id = ")
        .append("%2$s.collection_id) as %1$s left join %4$s on %1$s.collection_id = ")
        .append("%4$s.collection_id where %1$s.class_id = '%5$s'").toString(),
        DBConstants.TBL_CLASS_STRUCTURE_USE_FAST, DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION,
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE, DBConstants.TBL_COLLECTION_CASE, forClassId,
        companyId, serviceId);
    if (!isStructureUpdate) {
      sql += " and %1$s.company_id = '%6$s' and %1$s.service_id = '%7$s'";
      if (whiteList != null) {
        sql += whiteList.parallelStream()
            .map(collection -> String.format("%1$s.dynamic_role_id = '%2$s'",
                DBConstants.TBL_CLASS_STRUCTURE_USE_FAST, collection.getId()))
            .collect(Collectors.joining(" or ", "and (", ")"));
      } else if (blackList != null) {
        sql += blackList.parallelStream()
            .map(collection -> String.format("%1$s.dynamic_role_id != '%2$s'",
                DBConstants.TBL_CLASS_STRUCTURE_USE_FAST, collection.getId()))
            .collect(Collectors.joining(" and ", "and (", ")"));
      }
    }
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new FastStructureModel(
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("collection_id")),
            CommonMethods.getUUID(rs.getString("class_id")), rs.getString("json_structure"),
            rs.getInt("active") == 1,
            rs.getString("case_key") == null ? new CollectionCase()
                : new CollectionCase(rs.getString("case_key"),
                    CommonMethods.getUUID(rs.getString("class_id")),
                    CommonMethods.getUUID(rs.getString("collection_id")),
                    CommonMethods.getUUID(rs.getString("dynamic_role_id")))));
  }

}
