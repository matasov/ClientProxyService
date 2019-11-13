package com.matas.liteconstruct.db.models.stafflog.repos;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.stafflog.abstractmodel.ClassLogAbstract;
import com.matas.liteconstruct.db.models.stafflog.model.ClassLog;

public class ClassLogRepositoryImplemented implements ClassLogRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addClassLog(ClassLogAbstract logObject) {
    String sql = String.format("INSERT INTO \"%1$s\" (\"id\", \"class_id\", \"table_name\", "
        + "\"record_id\", \"field_id\", \"value_old\", \"value_new\", \"date_change\", "
        + "\"dynamic_role_id\", \"dispatcher_id\", \"remote_address\") VALUES "
        + "('%2$s', '%3$s', '%4$s', "
        + "'%5$s', '%6$s', (SELECT value_new from %1$s where \"table_name\" = '%4$s' and \"record_id\" = "
        + "'%5$s' and \"field_id\" = '%6$s' order by \"date_change\" desc limit 1), "
        + "'%7$s', '%8$s', '%9$s', '%10$s', '%11$s')", DBConstants.TBL_CLASS_LOG, logObject.getId(),
        logObject.getClassId(), logObject.getTableName(), logObject.getRecordId(),
        logObject.getFieldId(), logObject.getValueNew(), System.currentTimeMillis(),
        logObject.getDynamicRoleId(), logObject.getDispatcherId(), logObject.getRemoteAddress());
    jdbcTemplate.update(sql);
  }

  @Override
  public void addClassLogWithOldValue(ClassLogAbstract logObject) {
    String sql = String.format(
        "INSERT INTO \"%1$s\" (\"id\", \"class_id\", \"table_name\", "
            + "\"record_id\", \"field_id\", \"value_old\", \"value_new\", \"date_change\", "
            + "\"dynamic_role_id\", \"dispatcher_id\", \"remote_address\") VALUES "
            + "('%2$s', '%3$s', '%4$s', "
            + "'%5$s', '%6$s', '%7$s', '%8$s', '%9$s', '%10$s', '%11$s', '%12$s')",
        DBConstants.TBL_CLASS_LOG, UUID.randomUUID(), logObject.getClassId(),
        logObject.getTableName(), logObject.getRecordId(), logObject.getFieldId(),
        logObject.getValueOld(), logObject.getValueNew(), System.currentTimeMillis(),
        logObject.getDynamicRoleId(), logObject.getDispatcherId(), logObject.getRemoteAddress());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeClassLog(ClassLogAbstract filterGroup) {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateClassLog(ClassLogAbstract filterGroup) {
    // TODO Auto-generated method stub

  }

  @Override
  public ClassLogAbstract getClassLogById(UUID filterGroupId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ClassLogAbstract getClassLogByclassIdAndFieldId(UUID classId, UUID recordId, UUID fieldId) {
    String sql = String.format(
        "SELECT * FROM \"%1$s\" where class_id = '%2$s' and record_id = '%3$s' and field_id = '%4$s' order by date_change desc limit 1",
        DBConstants.TBL_CLASS_LOG, classId, recordId, fieldId);
    return Optional
        .ofNullable(jdbcTemplate.query(sql,
            (rs, rowNum) -> new ClassLog(CommonMethods.getUUID(rs.getString("id")),
                CommonMethods.getUUID(rs.getString("class_id")), rs.getString("table_name"),
                CommonMethods.getUUID(rs.getString("record_id")),
                CommonMethods.getUUID(rs.getString("field_id")), rs.getString("value_old"),
                rs.getString("value_new"), rs.getLong("date_change"),
                CommonMethods.getUUID(rs.getString("dynamic_role_id")),
                CommonMethods.getUUID(rs.getString("dispatcher_id")),
                rs.getString("remote_address"))))
        .map(Collection::stream).orElseGet(Stream::empty).findAny().orElse(null);
  }

  @Override
  public List<ClassLogAbstract> getClassLogForclassIdAndContactId(UUID classId, UUID contactId) {
    String sql = String.format(
        "SELECT * FROM \"%1$s\" where class_id = '%2$s' and dispatcher_id = '%3$s' order by date_change desc",
        DBConstants.TBL_CLASS_LOG, classId, contactId);
    return jdbcTemplate
        .query(sql, (rs, rowNum) -> new ClassLog(CommonMethods.getUUID(rs.getString("id")),
            CommonMethods.getUUID(rs.getString("class_id")), rs.getString("table_name"),
            CommonMethods.getUUID(rs.getString("record_id")),
            CommonMethods.getUUID(rs.getString("field_id")), rs.getString("value_old"),
            rs.getString("value_new"), rs.getLong("date_change"),
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("dispatcher_id")), rs.getString("remote_address")))
        .stream().collect(Collectors.toList());
  }

  @Override
  public List<ClassLogAbstract> getClassLogByCompanyIdAndContactId(UUID classId, UUID companyId) {
    String sql = String.format(
        "SELECT * FROM \"%1$s\" where class_id = '%2$s' and company_id = '%3$s' order by date_change desc",
        DBConstants.TBL_CLASS_LOG, classId, companyId);
    return jdbcTemplate
        .query(sql, (rs, rowNum) -> new ClassLog(CommonMethods.getUUID(rs.getString("id")),
            CommonMethods.getUUID(rs.getString("class_id")), rs.getString("table_name"),
            CommonMethods.getUUID(rs.getString("record_id")),
            CommonMethods.getUUID(rs.getString("field_id")), rs.getString("value_old"),
            rs.getString("value_new"), rs.getLong("date_change"),
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("dispatcher_id")), rs.getString("remote_address")))
        .stream().collect(Collectors.toList());
  }

  @Override
  public List<ClassLogAbstract> getClassLogForclassIdFieldIdAndContactId(UUID classId,
      UUID contactId, UUID fieldId) {
    String sql = String.format(
        "SELECT * FROM \"%1$s\" where class_id = '%2$s' and dispatcher_id = '%3$s' and field_id = '%4$s' order by date_change desc",
        DBConstants.TBL_CLASS_LOG, classId, contactId, fieldId);
    return jdbcTemplate
        .query(sql, (rs, rowNum) -> new ClassLog(CommonMethods.getUUID(rs.getString("id")),
            CommonMethods.getUUID(rs.getString("class_id")), rs.getString("table_name"),
            CommonMethods.getUUID(rs.getString("record_id")),
            CommonMethods.getUUID(rs.getString("field_id")), rs.getString("value_old"),
            rs.getString("value_new"), rs.getLong("date_change"),
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("dispatcher_id")), rs.getString("remote_address")))
        .stream().collect(Collectors.toList());
  }

  @Override
  public List<ClassLogAbstract> getClassLogByCompanyIdFieldIdAndContactId(UUID classId,
      UUID companyId, UUID fieldId) {
    String sql = String.format(
        "SELECT * FROM \"%1$s\" where class_id = '%2$s' and company_id = '%3$s' and field_id = '%4$s' order by date_change desc",
        DBConstants.TBL_CLASS_LOG, classId, companyId, fieldId);
    return jdbcTemplate
        .query(sql, (rs, rowNum) -> new ClassLog(CommonMethods.getUUID(rs.getString("id")),
            CommonMethods.getUUID(rs.getString("class_id")), rs.getString("table_name"),
            CommonMethods.getUUID(rs.getString("record_id")),
            CommonMethods.getUUID(rs.getString("field_id")), rs.getString("value_old"),
            rs.getString("value_new"), rs.getLong("date_change"),
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("dispatcher_id")), rs.getString("remote_address")))
        .stream().collect(Collectors.toList());
  }

}
