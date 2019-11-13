package com.matas.liteconstruct.db.models.dynamicclass.repos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.dynamicclass.abstractmodel.DynamicClassRecordLineAbstract;
import com.matas.liteconstruct.db.models.dynamicclass.model.DynamicClassRecordLine;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import com.matas.liteconstruct.db.tools.StructrueCollectionEnum;
import com.matas.liteconstruct.db.tools.permissions.PermissionHandler;
import com.matas.liteconstruct.service.management.structure.StructureCollectionsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicClassesRepositoryImplemented implements DynamicClassesRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private String getSubstringForAddRecord(DynamicClassRecordLineAbstract dynamicRecord) {
    List<List<String>> result = new ArrayList<>(2);
    List<String> fields = new ArrayList<>(10);
    List<String> values = new ArrayList<>(10);
    result.add(fields);
    result.add(values);
    try {
      log.info("dynamic values: {}", dynamicRecord.getFieldValues());
      dynamicRecord.getFieldValues().entrySet().stream()
          .filter(
              x -> !x.getKey().equalsIgnoreCase(dynamicRecord.getIdentificatorFieldId().toString())
                  && !x.getKey().equalsIgnoreCase(dynamicRecord.getOwnerFieldId().toString())
                  && !x.getKey().equalsIgnoreCase(dynamicRecord.getDateCreateFieldId().toString())
                  && !x.getKey().equalsIgnoreCase(dynamicRecord.getDateChangeFieldId().toString()))
          .forEach(x -> {
            result.get(0).add(x.getKey());
            result.get(1).add((String) x.getValue());
          });
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
    if (result.get(0) == null || result.get(0).isEmpty()) {
      return null;
    }
    return new StringBuilder().append("(\"").append(dynamicRecord.getIdentificatorFieldId())
        .append("\", \"").append(dynamicRecord.getOwnerFieldId()).append("\", \"")
        .append(dynamicRecord.getDateCreateFieldId()).append("\", \"")
        .append(dynamicRecord.getDateChangeFieldId()).append("\", ")
        .append(result.get(0).stream().collect(Collectors.joining("\", \"", "\"", "\"")))
        .append(") values ('").append(dynamicRecord.getRecordId()).append("', '")
        .append(dynamicRecord.getOwnerRecordId()).append("', '")
        .append(dynamicRecord.getTimeStamp()).append("', '").append(dynamicRecord.getTimeStamp())
        .append("', ").append(result.get(1).stream().collect(Collectors.joining("', '", "'", "'")))
        .append(")").toString();
  }

  private String getSubstringForUpdateRecord(DynamicClassRecordLineAbstract dynamicRecord) {
    try {
      return new StringBuilder()
          .append(dynamicRecord.getFieldValues().entrySet().stream().filter(
              x -> !x.getKey().equalsIgnoreCase(dynamicRecord.getIdentificatorFieldId().toString()))
              .map(x -> "\"" + x.getKey() + "\" = '" + x.getValue() + "'")
              .collect(Collectors.joining(", ")))
          .append(", \"").append(dynamicRecord.getDateChangeFieldId()).append("\" = '")
          .append(dynamicRecord.getTimeStamp()).append("'").toString();
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  @Override
  public void addDynamicClassesRecord(DynamicClassRecordLineAbstract dynamicRecord,
      long timeStamp) {
    String sql = String.format("insert into \"%1$s\" %2$s", dynamicRecord.getTblClassDataUse(),
        getSubstringForAddRecord(dynamicRecord));
    log.info("addDynamicClassesRecord: {}", sql);
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeDynamicClassesRecord(DynamicClassRecordLineAbstract dynamicRecord) {
    String sql = String.format("delete from \"%1$s\" where \"%2$s\" = '%3$s'",
        dynamicRecord.getTblClassDataUse(), dynamicRecord.getIdentificatorFieldId(),
        dynamicRecord.getRecordId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateDynamicClassesRecord(DynamicClassRecordLineAbstract dynamicRecord,
      long timeStamp) {
    String sql = String.format("update \"%1$s\" set %2$s where \"%3$s\" = '%4$s'",
        dynamicRecord.getTblClassDataUse(), getSubstringForUpdateRecord(dynamicRecord),
        dynamicRecord.getIdentificatorFieldId(), dynamicRecord.getRecordId());
    System.out.println("updateDynamicClassesRecord sql: " + sql);
    jdbcTemplate.update(sql);
  }

  @Override
  public DynamicClassRecordLineAbstract getDynamicClassesRecordById(UUID classId,
      UUID dynamicRecord, Map<String, Object> fastStructure) {
    String sql = String.format("SELECT %4$s FROM \"cc_%1$s_data_use\" where \"%2$s\" = '%3$s'",
        classId, (String) ((Map<String, Object>) fastStructure.get("0")).get("id"), dynamicRecord,
        getSubqueryForInsertedFields(fastStructure));
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new DynamicClassRecordLine(classId, dynamicRecord,
          UUID.fromString((String) ((Map<String, Object>) fastStructure.get("1")).get("id")),
          (UUID) rows.get(0).get(((Map<String, Object>) fastStructure.get("1")).get("id")),
          UUID.fromString((String) ((Map<String, Object>) fastStructure.get("2")).get("id")),
          UUID.fromString((String) ((Map<String, Object>) fastStructure.get("3")).get("id")),
          rows.get(0)
              .get(((Map<String, Object>) fastStructure.get("3")).get("id")) instanceof Double
                  ? Math.round((Double) rows.get(0)
                      .get(((Map<String, Object>) fastStructure.get("3")).get("id")))
                  : (Long) rows.get(0)
                      .get(((Map<String, Object>) fastStructure.get("3")).get("id")),
          UUID.fromString((String) ((Map<String, Object>) fastStructure.get("0")).get("id")),
          rows.get(0));
    return null;
  }

  @Override
  public DynamicClassRecordLineAbstract getDynamicClassesRecordByOverOwnerId(UUID classId,
      Map<String, Object> actualPermissions, Map<String, Object> fastStructure,
      Map<String, Object> conditions) {
    String conditionsStr = getSubstringForSelectConditions(conditions);
    if (conditionsStr == null) {
      conditionsStr = "";
    } else {
      conditionsStr = " and " + conditionsStr;
    }
    String sql = String.format(
        "SELECT \"cc_%1$s_data_use\".* FROM \"class_record_owner\" join \"cc_%1$s_data_use\" on "
            + "\"cc_%1$s_data_use\".\"%2$s\" = \"class_record_owner\".\"id\" WHERE \"%3$s\" = '%7$s' "
            + "AND \"%4$s\" = '%8$s' AND \"%5$s\" = '%9$s' AND \"%6$s\" = '%10$s' %12$s order by \"cc_%1$s_data_use\".\"%11$s\" desc limit 1",
        classId, (String) ((Map<String, Object>) fastStructure.get("1")).get("id"),
        DBConstants.CONTACT_ID, DBConstants.COMPANY_ID, DBConstants.SERVICE_ID, DBConstants.ROLE_ID,
        actualPermissions.get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID)),
        actualPermissions.get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID)),
        actualPermissions.get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.SERVICE_ID)),
        actualPermissions.get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID)),
        (String) ((Map<String, Object>) fastStructure.get("3")).get("id"), conditionsStr);
    System.out.println("getDynamicClassesRecordByOverOwnerId sql: " + sql);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new DynamicClassRecordLine(classId,
          (UUID) rows.get(0).get(((Map<String, Object>) fastStructure.get("0")).get("id")),
          UUID.fromString((String) ((Map<String, Object>) fastStructure.get("1")).get("id")),
          (UUID) rows.get(0).get(((Map<String, Object>) fastStructure.get("1")).get("id")),
          UUID.fromString((String) ((Map<String, Object>) fastStructure.get("2")).get("id")),
          UUID.fromString((String) ((Map<String, Object>) fastStructure.get("3")).get("id")),
          rows.get(0)
              .get(((Map<String, Object>) fastStructure.get("3")).get("id")) instanceof Double
                  ? Math.round((Double) rows.get(0)
                      .get(((Map<String, Object>) fastStructure.get("3")).get("id")))
                  : (Long) rows.get(0)
                      .get(((Map<String, Object>) fastStructure.get("3")).get("id")),
          UUID.fromString((String) ((Map<String, Object>) fastStructure.get("0")).get("id")),
          rows.get(0));
    return null;
  }

  private String getSubstringForSelectConditions(Map<String, Object> conditions) {
    try {
      return Optional.ofNullable(conditions.entrySet()).map(Collection::stream)
          .orElseGet(Stream::empty).map(x -> "\"" + x.getKey() + "\" = '" + x.getValue() + "'")
          .collect(Collectors.joining(" AND "));
    } catch (NullPointerException nuex) {
      return null;
    }
  }

  @Override
  public DynamicClassRecordLineAbstract getAdminDynamicClassesRecordByOverOwnerId(UUID classId,
      Map<String, Object> fastStructure, Map<String, Object> conditions, UUID sortID,
      boolean increase) {
    String conditionsStr = getSubstringForSelectConditions(conditions);
    if (conditionsStr == null) {
      return null;
    } else {
      conditionsStr = "" + conditionsStr;
    }
    String sortStr =
        sortID == null ? "" : "order by \"" + sortID + "\" " + (increase ? "asc" : "desc");

    String sql = String.format(
        "SELECT \"cc_%1$s_data_use\".* FROM \"cc_%1$s_data_use\" WHERE %2$s %3$s limit 1", classId,
        conditionsStr, sortStr);
    log.info("getDynamicClassesRecordByOverOwnerId sql: {}", sql);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new DynamicClassRecordLine(classId,
          (UUID) rows.get(0).get(((Map<String, Object>) fastStructure.get("0")).get("id")),
          UUID.fromString((String) ((Map<String, Object>) fastStructure.get("1")).get("id")),
          (UUID) rows.get(0).get(((Map<String, Object>) fastStructure.get("1")).get("id")),
          UUID.fromString((String) ((Map<String, Object>) fastStructure.get("2")).get("id")),
          UUID.fromString((String) ((Map<String, Object>) fastStructure.get("3")).get("id")),
          rows.get(0)
              .get(((Map<String, Object>) fastStructure.get("3")).get("id")) instanceof Double
                  ? Math.round((Double) rows.get(0)
                      .get(((Map<String, Object>) fastStructure.get("3")).get("id")))
                  : (Long) rows.get(0)
                      .get(((Map<String, Object>) fastStructure.get("3")).get("id")),
          UUID.fromString((String) ((Map<String, Object>) fastStructure.get("0")).get("id")),
          rows.get(0));
    return null;
  }

  @Override
  public boolean hasDynamicClassesRecordById(UUID classId, UUID dynamicRecord,
      Map<String, Object> fastStructure) {
    String sql = String.format("SELECT %4$s FROM \"cc_%1$s_data_use\" where \"%2$s\" = '%3$s'",
        classId, (String) ((Map<String, Object>) fastStructure.get("0")).get("id"), dynamicRecord,
        getSimpleSubqueryForEditFields(fastStructure));
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    return (rows != null && rows.size() > 0);
  }

  @Override
  public List<String> getDynamicSpaceShapeClassBySubquery(String sql) {
    // System.out.println(sql);
    // System.out.println(
    // Optional.ofNullable(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("row_to_json")))
    // .map(Collection::stream).orElseGet(Stream::empty).collect(Collectors.toList()));
    try {
      return Optional
          .ofNullable(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("row_to_json")))
          .map(Collection::stream).orElseGet(Stream::empty).collect(Collectors.toList());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  @Override
  public Map<String, Object> getDynamicGarbadge(String sql) {
    try {
      return jdbcTemplate.queryForMap(sql);
    } catch (IncorrectResultSizeDataAccessException emptyex) {
      return null;
    }
  }

  @Override
  public List<Map<String, Object>> getDynamicGarbadgeList(String sql) {
    try {
      return jdbcTemplate.queryForList(sql);
    } catch (EmptyResultDataAccessException emptyex) {
      return null;
    }
  }

  private String getSubqueryForInsertedFields(Map<String, Object> fastStructure) {
    return fastStructure.entrySet().parallelStream()
        .filter(field -> PermissionHandler.isInsert(
            Integer.parseInt((String) ((Map<String, Object>) field.getValue()).get(StructrueCollectionEnum.PERM.toString()))))
        .map(field -> (String) ((Map<String, Object>) field.getValue()).get("id"))
        .collect(Collectors.joining("\", \"", "\"", "\""));
  }

  private String getSimpleSubqueryForEditFields(Map<String, Object> fastStructure) {
    log.info("getSimpleSubqueryForEditFields: {}", fastStructure);
    return fastStructure.entrySet().parallelStream()
        .filter(field -> PermissionHandler.isEdit(
            Integer.parseInt((String) ((Map<String, Object>) field.getValue()).get(StructrueCollectionEnum.PERM.toString()))))
        .map(field -> (String) ((Map<String, Object>) field.getValue()).get("id"))
        .collect(Collectors.joining("\", \"", "\"", "\""));
  }

}
