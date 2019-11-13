package com.matas.liteconstruct.db.models.accessrules.repos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.accessrules.abstractmodel.AccessRuleAbstract;
import com.matas.liteconstruct.db.models.accessrules.model.AccessRule;
import com.matas.liteconstruct.service.dynamic.DynamicClassPutData;

public class AccessRuleRepositoryImplemented implements AccessRuleRepository {

  private static final org.slf4j.Logger LOGGER =
      org.slf4j.LoggerFactory.getLogger(AccessRuleRepository.class);

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addAccessRule(AccessRuleAbstract accessRule) {
    String sql = String.format(
        "insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s', '%7$s', '%8$s', '%9$s', '%10$s')",
        DBConstants.TBL_ACCESS_RULE, accessRule.getId(), accessRule.getName(),
        accessRule.getCompanyId(), accessRule.getClassId(), accessRule.getAccessFilterGroupId(),
        accessRule.getEditAccess(), accessRule.getPriority(), accessRule.getLevelAccess(),
        accessRule.getLevelValue());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeAccessRule(AccessRuleAbstract accessRule) {
    String sql = String.format("delete from %1$s where id = '%2$s'", DBConstants.TBL_ACCESS_RULE,
        accessRule.getId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateAccessRule(AccessRuleAbstract accessRule) {
    String sql = String.format(
        "update %1$s set name = '%3$s', company_id = '%4$s', class_id = '%5$s', class_id = '%6$s', edit_access = '%7$s', priority = '%8$s', level_access = '%9$s', level_value = '%10$s' where id = '%2$s'",
        DBConstants.TBL_ACCESS_RULE, accessRule.getId(), accessRule.getName(),
        accessRule.getCompanyId(), accessRule.getClassId(), accessRule.getAccessFilterGroupId(),
        accessRule.getEditAccess(), accessRule.getPriority(), accessRule.getLevelAccess(),
        accessRule.getLevelValue());
    jdbcTemplate.update(sql);
  }

  @Override
  public AccessRuleAbstract getAccessRuleById(UUID accessRuleID) {
    String sql = String.format("SELECT * FROM %1$s WHERE id = '%2$s' limit 1",
        DBConstants.TBL_ACCESS_RULE, accessRuleID);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new AccessRule((UUID) rows.get(0).get("id"), (String) rows.get(0).get("name"),
          (UUID) rows.get(0).get("company_id"), (UUID) rows.get(0).get("class_id"),
          (UUID) rows.get(0).get("access_filter_group_id"), (Short) rows.get(0).get("edit_access"),
          (Integer) rows.get(0).get("priority"), (Short) rows.get(0).get("level_access"),
          (UUID) rows.get(0).get("level_value"));
    else
      return null;
  }

  @Override
  public AccessRuleAbstract getAccessRuleByIdWithPermission(UUID accessRuleID, String subquery) {
    String sql = String.format("SELECT * FROM %1$s where id = '%2$s' %3$s limit 1",
        DBConstants.TBL_ACCESS_RULE, accessRuleID, subquery.equals("") ? "" : (" and " + subquery));
    LOGGER.info("getAccessRuleByIdWithPermission sql: {}", sql);
    return jdbcTemplate
        .query(sql,
            (rs, rowNum) -> new AccessRule(CommonMethods.getUUID(rs.getString("id")),
                rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
                CommonMethods.getUUID(rs.getString("class_id")),
                CommonMethods.getUUID(rs.getString("access_filter_group_id")),
                rs.getShort("edit_access"), rs.getInt("priority"), rs.getShort("level_access"),
                CommonMethods.getUUID(rs.getString("level_value"))))
        .parallelStream().findFirst().orElse(null);
  }

  @SuppressWarnings("finally")
  @Override
  public List<AccessRuleAbstract> getForEditAccessRuleForCompanyClassByPermission(UUID companyId,
      UUID classId, int editAccess) {
    ArrayList<AccessRuleAbstract> result = new ArrayList<>(3);
    try {
      String sql = String.format(
          "select * from %1$s where company_id = '%2$s' and class_id = '%3$s' and edit_access >= '%4$s' order by edit_access, priority",
          DBConstants.TBL_ACCESS_RULE, companyId, classId, editAccess);
      jdbcTemplate.query(sql,
          (rs, rowNum) -> new AccessRule(CommonMethods.getUUID(rs.getString("id")),
              rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
              CommonMethods.getUUID(rs.getString("class_id")),
              CommonMethods.getUUID(rs.getString("access_filter_group_id")),
              rs.getShort("edit_access"), rs.getInt("priority"), rs.getShort("level_access"),
              CommonMethods.getUUID(rs.getString("level_value"))));
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
    } finally {
      return result;
    }
  }

  @Override
  public List<AccessRuleAbstract> getForUseAccessRuleForCompanyClassByPermission(UUID companyId,
      UUID classId, short levelAccess) {
    // ArrayList<AccessRuleAbstract> result = new ArrayList<>(3);
    try {
      String sql = String.format(
          "select * from %1$s where company_id = '%2$s' and class_id = '%3$s' and level_access >= '%4$s' order by edit_access, priority",
          DBConstants.TBL_ACCESS_RULE, companyId, classId, levelAccess);
      return jdbcTemplate.query(sql,
          (rs, rowNum) -> new AccessRule(CommonMethods.getUUID(rs.getString("id")),
              rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
              CommonMethods.getUUID(rs.getString("class_id")),
              CommonMethods.getUUID(rs.getString("access_filter_group_id")),
              rs.getShort("edit_access"), rs.getInt("priority"), rs.getShort("level_access"),
              CommonMethods.getUUID(rs.getString("level_value"))));
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
      return null;
    }
  }
}
