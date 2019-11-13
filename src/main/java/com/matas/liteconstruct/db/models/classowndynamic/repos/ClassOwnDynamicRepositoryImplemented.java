package com.matas.liteconstruct.db.models.classowndynamic.repos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.classowndynamic.abstractmodel.ClassOwnDynamicAbstract;
import com.matas.liteconstruct.db.models.classowndynamic.model.ClassOwnDynamic;

public class ClassOwnDynamicRepositoryImplemented implements ClassOwnDynamicRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addClassOwnDynamic(ClassOwnDynamicAbstract classownrecord) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s')",
        DBConstants.TBL_OWN_RECORD_DYNAMIC, classownrecord.getId(), classownrecord.getClassId(),
        classownrecord.getEditAccess(), classownrecord.getPriority(),
        classownrecord.getRecordAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeClassOwnDynamic(ClassOwnDynamicAbstract classownrecord) {
    String sql = String.format("delete from %1$s where id = '%2$s'",
        DBConstants.TBL_OWN_RECORD_DYNAMIC, classownrecord.getId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateClassOwnDynamic(ClassOwnDynamicAbstract classownrecord) {
    String sql = String.format(
        "update %1$s set name = '%3$s', class_id = '%4$s', edit_access = '%5$s', priority = '%6$s', record_access = '%7$s' where id = '%2$s'",
        DBConstants.TBL_OWN_RECORD_DYNAMIC, classownrecord.getId(), classownrecord.getClassId(),
        classownrecord.getEditAccess(), classownrecord.getPriority(),
        classownrecord.getRecordAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public ClassOwnDynamicAbstract getClassOwnDynamicById(UUID classownrecordId) {
    List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM "
        + DBConstants.TBL_OWN_RECORD_DYNAMIC + " WHERE id = '" + classownrecordId + "' limit 1");
    if (rows != null && rows.size() > 0)
      return new ClassOwnDynamic((UUID) rows.get(0).get("id"), (UUID) rows.get(0).get("class_id"),
          (Short) rows.get(0).get("edit_access"), (Integer) rows.get(0).get("priority"),
          (Short) rows.get(0).get("record_access"));
    else
      return null;
  }

  @SuppressWarnings("finally")
  @Override
  public List<ClassOwnDynamicAbstract> getClassOwnDynamicForCompanyClassByPermission(UUID classId,
      int editAccess) {
    ArrayList<ClassOwnDynamicAbstract> result = new ArrayList<>(20);
    try {
      String sql = String.format(
          "select * from %1$s where class_id = '%2$s' and edit_access > '%3$s' order by edit_access, priority",
          DBConstants.TBL_OWN_RECORD_DYNAMIC, classId, editAccess);
      jdbcTemplate.query(sql,
          (rs, rowNum) -> new ClassOwnDynamic(CommonMethods.getUUID(rs.getString("id")),
              CommonMethods.getUUID(rs.getString("class_id")), rs.getShort("edit_access"),
              rs.getInt("priority"), rs.getShort("record_access")))
          .forEach(structure -> {
            result.add(structure);
          });
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
    } finally {
      return result;
    }
  }

}
