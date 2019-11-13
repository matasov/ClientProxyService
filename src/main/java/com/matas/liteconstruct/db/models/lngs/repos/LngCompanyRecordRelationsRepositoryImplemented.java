package com.matas.liteconstruct.db.models.lngs.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.aspect.log.LogExecutionTime;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.lngs.abstractmodel.LngCompanyAccess;
import com.matas.liteconstruct.db.models.lngs.abstractmodel.LngCompanyRecordRelation;
import com.matas.liteconstruct.db.models.lngs.model.LngCompanyAccessImplemented;
import com.matas.liteconstruct.db.models.lngs.model.LngCompanyRecordRelationImplemented;

public class LngCompanyRecordRelationsRepositoryImplemented
    implements LngCompanyRecordRelationsRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addLngRecord(LngCompanyRecordRelation lng) {
    String sql =
        String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s', '%7$s')",
            DBConstants.LNG_RECORD_RELATIONS, lng.getClassId(), lng.getCompanyId(), lng.getLngId(),
            lng.getRecordId(), lng.getFieldId(), lng.getValue());
    jdbcTemplate.execute(sql);
  }

  @Override
  public void removeLngRecordField(UUID classId, UUID lngId, UUID recordId, UUID fieldId) {
    String sql = String.format(
        "delete from %1$s where class_id = '%2$s' and lng_id = '%3$s' and record_id = '%4$s' and field_id = '%5$s'",
        DBConstants.LNG_RECORD_RELATIONS, classId, lngId, recordId, fieldId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void removeLngFullRecordFieldByCompanyAndId(UUID classId, UUID lngId, UUID recordId) {
    String sql = String.format(
        "delete from %1$s where class_id = '%2$s' and lng_id = '%3$s' and record_id = '%4$s'",
        DBConstants.LNG_RECORD_RELATIONS, classId, lngId, recordId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void removeLngRecordsByCompanyId(UUID companyId) {
    String sql = String.format("delete from %1$s where company_id = '%2$s'",
        DBConstants.LNG_RECORD_RELATIONS, companyId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void removeLngRecordsByClassId(UUID classId) {
    String sql = String.format("delete from %1$s where class_id = '%2$s'",
        DBConstants.LNG_RECORD_RELATIONS, classId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void updateLngRecord(LngCompanyRecordRelation lng) {
    String sql = String.format(
        "update %1$s set value = '%7$s' where class_id = '%2$s' and company_id = '%3$s' and lng_id = '%4$s' and record_id = '%5$s' and field_id = '%6$s'",
        DBConstants.LNG_RECORD_RELATIONS, lng.getClassId(), lng.getCompanyId(), lng.getLngId(),
        lng.getRecordId(), lng.getFieldId(), lng.getValue());
    jdbcTemplate.update(sql);
  }

  @Override
  public LngCompanyRecordRelation getLngRecordByLngRecordField(UUID lngId, UUID recordId,
      UUID fieldId) {
    String sql = String.format(
        "SELECT * FROM %1$s where lng_id = '%2$s' and record_id = '%3$s' and field_id = '%4$s'",
        DBConstants.LNG_RECORD_RELATIONS, lngId, recordId, fieldId);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new LngCompanyRecordRelationImplemented((UUID) rows.get(0).get("class_id"),
          (UUID) rows.get(0).get("company_id"), (UUID) rows.get(0).get("lng_id"),
          (UUID) rows.get(0).get("record_id"), (UUID) rows.get(0).get("field_id"),
          (String) rows.get(0).get("value"));
    else
      return null;
  }

  @Override
  public List<LngCompanyRecordRelation> getLngRecordsByCompanyLngClass(UUID classId, UUID companyId,
      UUID lngId) {
    String sql = String.format(
        "SELECT * FROM %1$s where class_id = '%2$s' and company_id = '%3$s' and lng_id = '%4$s'",
        DBConstants.LNG_RECORD_RELATIONS, companyId);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new LngCompanyRecordRelationImplemented(
            CommonMethods.getUUID(rs.getString("class_id")),
            CommonMethods.getUUID(rs.getString("company_id")),
            CommonMethods.getUUID(rs.getString("lng_id")),
            CommonMethods.getUUID(rs.getString("record_id")),
            CommonMethods.getUUID(rs.getString("field_id")), rs.getString("value")));
  }

  @Override
  public List<LngCompanyRecordRelation> getLngRecordsByField(UUID fieldId) {
    String sql = String.format("SELECT * FROM %1$s where field_id = '%2$s'",
        DBConstants.LNG_RECORD_RELATIONS, fieldId);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new LngCompanyRecordRelationImplemented(
            CommonMethods.getUUID(rs.getString("class_id")),
            CommonMethods.getUUID(rs.getString("company_id")),
            CommonMethods.getUUID(rs.getString("lng_id")),
            CommonMethods.getUUID(rs.getString("record_id")),
            CommonMethods.getUUID(rs.getString("field_id")), rs.getString("value")));
  }

  @LogExecutionTime
  @Override
  public List<LngCompanyRecordRelation> getLngRecordsByArray(List<String> recordsArray) {
    String sql = String.format("SELECT * FROM %1$s where record_id = ANY(ARRAY['%2$s']::uuid[])",
        DBConstants.LNG_RECORD_RELATIONS,
        recordsArray.parallelStream().collect(Collectors.joining("','")));
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new LngCompanyRecordRelationImplemented(
            CommonMethods.getUUID(rs.getString("class_id")),
            CommonMethods.getUUID(rs.getString("company_id")),
            CommonMethods.getUUID(rs.getString("lng_id")),
            CommonMethods.getUUID(rs.getString("record_id")),
            CommonMethods.getUUID(rs.getString("field_id")), rs.getString("value")));
  }

}
