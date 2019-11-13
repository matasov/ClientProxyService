package com.matas.liteconstruct.db.models.classmanagement.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.accessliteral.model.AccessLiteralModel;
import com.matas.liteconstruct.db.models.classmanagement.abstractmodel.DynamicClassManagementSettingsAbstract;
import com.matas.liteconstruct.db.models.classmanagement.model.DynamicClassManagementSettings;

public class DCManagementSettingsImplemented implements DCManagementSettings {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addDCManagementSettings(DynamicClassManagementSettingsAbstract dcManagement) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s')",
        DBConstants.TBL_MANAGEMENT_RULES, dcManagement.getManagementId(), dcManagement.getName(),
        dcManagement.getClassId(), dcManagement.getDynamicRoleId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeDCManagementSettings(DynamicClassManagementSettingsAbstract dcManagement) {
    String sql = String.format("delete from %1$s where id = '%2$s'",
        DBConstants.TBL_MANAGEMENT_RULES, dcManagement.getManagementId());
    jdbcTemplate.update(sql);
    sql = String.format("delete from %1$s where management_id = '%2$s'",
        DBConstants.TBL_MANAGEMENT_SETTINGS, dcManagement.getManagementId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateDCManagementSettings(DynamicClassManagementSettingsAbstract dcManagement) {
    String sql = String.format(
        "update %1$s set name = '%3$s', class_id = '%4$s', dynamic_role_id = '%5$s' where id = '%2$s'",
        DBConstants.TBL_MANAGEMENT_RULES, dcManagement.getManagementId(), dcManagement.getName(),
        dcManagement.getClassId(), dcManagement.getDynamicRoleId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void addMetaValueDCManagementSettings(DynamicClassManagementSettingsAbstract dcManagement,
      String metaKey, String metaValue) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s')",
        DBConstants.TBL_MANAGEMENT_SETTINGS, dcManagement.getManagementId(), metaKey, metaValue);
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateMetaValueDCManagementSettings(
      DynamicClassManagementSettingsAbstract dcManagement, String metaKey, String metaValue) {
    String sql =
        String.format("update %1$s set meta_key = '%3$s', meta_value = '%4$s' where id = '%2$s'",
            DBConstants.TBL_MANAGEMENT_RULES, dcManagement.getManagementId(), metaKey, metaValue);
    jdbcTemplate.update(sql);
  }

  @Override
  public DynamicClassManagementSettingsAbstract getDCManagementSettingsById(UUID dcManagementId) {
    String sql = String.format("SELECT * FROM %1$s WHERE id = '%2$s' limit 1",
        DBConstants.TBL_MANAGEMENT_RULES, dcManagementId);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0) {
      DynamicClassManagementSettings result = new DynamicClassManagementSettings();
      result.setManagementId((UUID) rows.get(0).get("id"));
      result.setName((String) rows.get(0).get("name"));
      result.setClassId((UUID) rows.get(0).get("class_id"));
      result.setDynamicRoleId((UUID) rows.get(0).get("dynamic_role_id"));
      result.setValues(getAllMetaValuesForDCManagementSettings((UUID) rows.get(0).get("id")));
      return result;
    } else
      return null;
  }

  @Override
  public Map<String, String> getAllMetaValuesForDCManagementSettings(UUID dcManagementId) {
    String sql = String.format("SELECT * FROM %1$s WHERE management_id = '%2$s'",
        DBConstants.TBL_MANAGEMENT_SETTINGS, dcManagementId);
    return jdbcTemplate.queryForList(sql).parallelStream().collect(Collectors
        .toMap(row -> (String) row.get("meta_key"), row -> (String) row.get("meta_value")));
  }

  @Override
  public DynamicClassManagementSettingsAbstract getDCManagementSettingsByDynamicRoleId(
      UUID dynamicRoleId, UUID classId) {
    String sql =
        String.format("SELECT * FROM %1$s WHERE class_id = '%2$s' and dynamic_role_id = '%3$s'",
            DBConstants.TBL_MANAGEMENT_RULES, dynamicRoleId, classId);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0) {
      DynamicClassManagementSettings result = new DynamicClassManagementSettings();
      result.setManagementId((UUID) rows.get(0).get("id"));
      result.setName((String) rows.get(0).get("name"));
      result.setClassId((UUID) rows.get(0).get("class_id"));
      result.setDynamicRoleId((UUID) rows.get(0).get("dynamic_role_id"));
      result.setValues(getAllMetaValuesForDCManagementSettings((UUID) rows.get(0).get("id")));
      return result;
    } else
      return null;
  }

  @Override
  public String getMetaValueForDCManagementSettings(
      DynamicClassManagementSettingsAbstract dcManagement, String metaKey) {
    String sql =
        String.format("SELECT * FROM %1$s WHERE management_id = '%2$s' and meta_key = '%3$s'",
            DBConstants.TBL_MANAGEMENT_SETTINGS, dcManagement.getManagementId(), metaKey);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0) {
      return (String) rows.get(0).get("meta_value");
    } else
      return null;
  }

  @Override
  public List<DynamicClassManagementSettingsAbstract> getAllDCManagementSettings() {
    String sql = String.format("SELECT * FROM %1$s", DBConstants.TBL_MANAGEMENT_RULES);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new DynamicClassManagementSettings(UUID.fromString(rs.getString("id")),
            rs.getString("name"), UUID.fromString(rs.getString("class_id")),
            UUID.fromString(rs.getString("dynamic_role_id")),
            getAllMetaValuesForDCManagementSettings(UUID.fromString(rs.getString("id")))));
  }

  @Override
  public List<DynamicClassManagementSettingsAbstract> getAllDCManagementSettingsByCompanyId(
      UUID companyId) {
    String sql = String.format(
        "SELECT class_management_rules.* " + "FROM %1$s join %2$s on %1$s.dynamic_role_id = "
            + "%2$s.id where company_id = '%3$s'",
        DBConstants.TBL_MANAGEMENT_RULES, DBConstants.TBL_REGISTRY_DYNAMIC_ROLE, companyId);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new DynamicClassManagementSettings(UUID.fromString(rs.getString("id")),
            rs.getString("name"), UUID.fromString(rs.getString("class_id")),
            UUID.fromString(rs.getString("dynamic_role_id")),
            getAllMetaValuesForDCManagementSettings(UUID.fromString(rs.getString("id")))));
  }

}
