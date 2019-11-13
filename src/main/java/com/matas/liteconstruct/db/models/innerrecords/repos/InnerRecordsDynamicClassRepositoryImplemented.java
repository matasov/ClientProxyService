package com.matas.liteconstruct.db.models.innerrecords.repos;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.innerrecords.abstractmodel.BelongRecord;
import com.matas.liteconstruct.db.models.innerrecords.abstractmodel.InnerRecord;
import com.matas.liteconstruct.db.models.innerrecords.model.BelongRecordImplemented;
import com.matas.liteconstruct.db.models.innerrecords.model.InnerRecordImplemented;

public class InnerRecordsDynamicClassRepositoryImplemented
    implements InnerRecordsDynamicClassRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private String getImplementedRecordsFromToken(UUID classId) {
    return String.format(DBConstants.TBL_TOKEN_IMPLEMENTED_RECORDS, classId);
  }

  @Override
  public void addInnerRecord(InnerRecord nestedRecord) {
    String sql = String.format("insert into \"%1$s\" values ('%2$s', '%3$s', '%4$s')",
        getImplementedRecordsFromToken(nestedRecord.getClassId()), nestedRecord.getImplementedId(),
        nestedRecord.getRecordId(), nestedRecord.getTurn());
    jdbcTemplate.update(sql);
  }

  @Override
  public void addBelongRecord(BelongRecord belongRecord) {
    String sql = String.format("insert into \"%1$s\" values ('%2$s', '%3$s', '%4$s', '%5$s')",
        DBConstants.TBL_BELONG_RECORDS, belongRecord.getInnerClassId(),
        belongRecord.getImplementedId(), belongRecord.getParentClassId(),
        belongRecord.getFieldId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateInnerRecord(InnerRecord nestedRecord) {
    String sql = String.format(
        "update \"%1$s\" set implemented_id = '%2$s', record_id = '%3$s', turn = '%4$s')",
        getImplementedRecordsFromToken(nestedRecord.getClassId()), nestedRecord.getImplementedId(),
        nestedRecord.getRecordId(), nestedRecord.getTurn());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateBelongRecord(BelongRecord belongRecord) {
    String sql = String.format(
        "update \"%1$s\" set inner_class_id = '%2$s', implemented_id = '%3$s', parent_class_id = '%4$s', field_id = '%5$s')",
        DBConstants.TBL_BELONG_RECORDS, belongRecord.getInnerClassId(),
        belongRecord.getImplementedId(), belongRecord.getParentClassId(),
        belongRecord.getFieldId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void deleteInnerRecord(UUID classId, UUID implementedId, UUID recordId) {
    String sql =
        String.format("delete from \"%1$s\" where implemented_id = '%2$s' and record_id = '%3$s'",
            getImplementedRecordsFromToken(classId), implementedId, recordId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void deleteBelongRecord(UUID implementedId) {
    String sql = String.format("delete from \"%1$s\" where implemented_id = '%2$s'",
        DBConstants.TBL_BELONG_RECORDS, implementedId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public List<InnerRecord> getInnerRecordsForImplemented(UUID classId, UUID implementedId) {
    String sql = String.format("SELECT * FROM \"%1$s\" WHERE implemented_id = '%2$s'",
        getImplementedRecordsFromToken(classId), implementedId);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new InnerRecordImplemented(classId,
            CommonMethods.getUUID(rs.getString("implemented_id")),
            CommonMethods.getUUID(rs.getString("record_id")), rs.getInt("turn")));
  }

  @Override
  public BelongRecord getBelongRecordForImplemented(UUID innerClassId, UUID implementedId) {
    String sql = String.format(
        "SELECT * FROM \"%1$s\" WHERE inner_class_id = '%2$s' and implemented_id = '%3$s'",
        DBConstants.TBL_BELONG_RECORDS, innerClassId, implementedId);
    List<BelongRecord> result = jdbcTemplate.query(sql,
        (rs, rowNum) -> new BelongRecordImplemented(innerClassId,
            CommonMethods.getUUID(rs.getString("implemented_id")),
            CommonMethods.getUUID(rs.getString("parent_class_id")),
            CommonMethods.getUUID(rs.getString("field_id"))));
    return result != null && !result.isEmpty() ? result.get(0) : null;
  }

  @Override
  public void deleteAllBelongRecordsByClass(UUID classId) {
    String sql = String.format(
        "delete from \"%1$s\" where inner_class_id = '%2$s' or parent_class_id = '%2$s'",
        DBConstants.TBL_BELONG_RECORDS, classId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void clearAllInnerRecordsByImplementedId(UUID classId, UUID implementedId) {
    String sql = String.format("delete from \"%1$s\" where implemented_id = '%2$s'",
        getImplementedRecordsFromToken(classId), implementedId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void clearAllInnerRecordsWhileDeleteFieldId(UUID belongClassId, UUID innerClassId,
      UUID fieldId) {
    String sql = String.format(
        "DELETE FROM \"%1$s\" where \"implemented_id\" in (SELECT \"implemented_id\" FROM \"%2$s\" WHERE \"parent_class_id\" = '%3$s' AND \"field_id\" = '%4$s')",
        getImplementedRecordsFromToken(innerClassId), DBConstants.TBL_BELONG_RECORDS, belongClassId,
        fieldId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void clearAllBelongRecordsWhileDeleteFieldId(UUID belongClassId, UUID innerClassId,
      UUID fieldId) {
    String sql = String.format(
        "DELETE FROM \"%1$s\" WHERE \"parent_class_id\" = '%2$s' AND \"field_id\" = '%3$s'",
        DBConstants.TBL_BELONG_RECORDS, belongClassId, fieldId);
    jdbcTemplate.execute(sql);
  }

}
