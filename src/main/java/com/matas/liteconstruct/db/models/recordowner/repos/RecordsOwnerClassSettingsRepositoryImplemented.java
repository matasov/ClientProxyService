package com.matas.liteconstruct.db.models.recordowner.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.recordowner.abstractmodel.RecordsOwnerClassSettingsAbstract;
import com.matas.liteconstruct.db.models.recordowner.model.RecordsOwnerClassSettings;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecordsOwnerClassSettingsRepositoryImplemented
    implements RecordsOwnerClassSettingsRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addRecordsOwnerClassSettings(RecordsOwnerClassSettingsAbstract recordsOwner) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s')",
        DBConstants.TBL_OWN_RECORD_DYNAMIC, recordsOwner.getId(), recordsOwner.getClassId(),
        recordsOwner.getEditAccess(), recordsOwner.getPriority(),
        recordsOwner.getTypeRecordAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeRecordsOwnerClassSettings(RecordsOwnerClassSettingsAbstract recordsOwner) {
    String sql = String.format("delete from %1$s where id = '%2$s'",
        DBConstants.TBL_OWN_RECORD_DYNAMIC, recordsOwner.getId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateRecordsOwnerClassSettings(RecordsOwnerClassSettingsAbstract recordsOwner) {
    String sql = String.format(
        "update %1$s set class_id = '%3$s', edit_access = '%4$s', priority = '%5$s', type_record_access = '%6$s' where id = '%2$s'",
        DBConstants.TBL_OWN_RECORD_DYNAMIC, recordsOwner.getId(), recordsOwner.getClassId(),
        recordsOwner.getEditAccess(), recordsOwner.getPriority(),
        recordsOwner.getTypeRecordAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public RecordsOwnerClassSettingsAbstract getRecordsOwnerClassSettingsById(UUID recordId) {
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(
        "SELECT " + DBConstants.TBL_OWN_RECORD_DYNAMIC + ".*, " + DBConstants.TBL_STRUCTURE_FIELDS
            + ".id as field_id from " + DBConstants.TBL_OWN_RECORD_DYNAMIC + " join "
            + DBConstants.TBL_STRUCTURE_FIELDS + " on " + DBConstants.TBL_OWN_RECORD_DYNAMIC
            + ".class_id = " + DBConstants.TBL_STRUCTURE_FIELDS + ".class_id where "
            + DBConstants.TBL_OWN_RECORD_DYNAMIC + ".id = '" + recordId + "' and "
            + DBConstants.TBL_STRUCTURE_FIELDS + ".field_class = '" + DBConstants.SYSTEM_UUID
            + "' order by priority limit 1");
    if (rows != null && rows.size() > 0)
      return new RecordsOwnerClassSettings((UUID) rows.get(0).get("id"),
          (UUID) rows.get(0).get("class_id"), (short) (int) rows.get(0).get("edit_access"),
          (int) rows.get(0).get("priority"), (short) (int) rows.get(0).get("type_record_access"),
          (UUID) rows.get(0).get("field_id"));
    else
      return null;
  }

  @Override
  public RecordsOwnerClassSettingsAbstract getRecordsOwnerClassSettingsByclassId(UUID classId,
      UUID ownerFieldId) {
    String sql = "SELECT " + DBConstants.TBL_OWN_RECORD_DYNAMIC + ".*, "
        + DBConstants.TBL_STRUCTURE_FIELDS + ".id as field_id from "
        + DBConstants.TBL_OWN_RECORD_DYNAMIC + " join " + DBConstants.TBL_STRUCTURE_FIELDS + " on "
        + DBConstants.TBL_OWN_RECORD_DYNAMIC + ".class_id = " + DBConstants.TBL_STRUCTURE_FIELDS
        + ".class_id where " + DBConstants.TBL_OWN_RECORD_DYNAMIC + ".class_id = '" + classId
        + "' and " + DBConstants.TBL_STRUCTURE_FIELDS + ".id = '" + ownerFieldId
        + "' order by priority limit 1";
    log.info("getRecordsOwnerClassSettingsByclassId: {}", sql);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new RecordsOwnerClassSettings((UUID) rows.get(0).get("id"),
          (UUID) rows.get(0).get("class_id"), (short) (int) rows.get(0).get("edit_access"),
          (int) rows.get(0).get("priority"), (short) (int) rows.get(0).get("type_record_access"),
          (UUID) rows.get(0).get("field_id"));
    else
      return null;
  }

  @Override
  public UUID getFieldIdOwnerInDataStructure(UUID classId) {
    List<Map<String, Object>> rows =
        jdbcTemplate.queryForList("SELECT id FROM \"class_structure_fields\" WHERE \"class_id\" = '"
            + classId + "' AND \"field_class\" = '" + DBConstants.SYSTEM_UUID + "'");
    if (rows != null && rows.size() > 0)
      return (UUID) rows.get(0).get("id");
    else
      return null;
  }

}
