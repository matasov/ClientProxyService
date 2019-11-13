package com.matas.liteconstruct.db.models.structure.repos;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.controller.management.StructureClassController;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.classes.model.CustomerClassModel;
import com.matas.liteconstruct.db.models.structure.abstractmodel.StructureFieldAbstract;
import com.matas.liteconstruct.db.models.structure.model.StructureFieldImplemented;
import com.matas.liteconstruct.db.tools.sqlselectfactory.FieldsFactoryInterface;
import com.matas.liteconstruct.service.management.structure.PrimitiveCustomClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StructureFieldsRepositoryImplemented implements StructureFieldsRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addStructureFields(StructureFieldAbstract field) {
    String sql = String.format(
        "insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s', '%7$s')",
        DBConstants.TBL_STRUCTURE_FIELDS, field.getId(), field.getClassId(), field.getFieldName(),
        field.getDataClass(), field.getInnerType(), field.getFieldShowName());
    log.info("add new field with query: {}", sql);
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeStructureField(UUID fieldId) {
    String sql = String.format("delete from %1$s where id = '%2$s'",
        DBConstants.TBL_STRUCTURE_FIELDS, fieldId);
    jdbcTemplate.execute(sql);
  }
  
  @Override
  public void removeStructureFieldsByclassId(UUID classId){
    String sql = String.format("delete from %1$s where class_id = '%2$s'",
        DBConstants.TBL_STRUCTURE_FIELDS, classId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void updateStructureFields(StructureFieldAbstract field) {
    String sql = String.format(
        "update %1$s set class_id = '%3$s', name = '%4$s', field_class = '%5$s', \"inner\" = '%6$s', show_name = '%7$s' where id = '%2$s'",
        DBConstants.TBL_STRUCTURE_FIELDS, field.getId(), field.getClassId(), field.getFieldName(),
        field.getDataClass(), field.getInnerType(), field.getFieldShowName());
    jdbcTemplate.update(sql);
  }

  @Override
  public StructureFieldAbstract getStructureFieldsById(UUID fieldId) {
    String sql = String.format("SELECT * FROM %1$s WHERE id = '%2$s' limit 1",
        DBConstants.TBL_STRUCTURE_FIELDS, fieldId);
	log.info("getStructureFieldsById({}) sql: {}", fieldId, sql);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new StructureFieldImplemented((UUID) rows.get(0).get("id"),
          (UUID) rows.get(0).get("class_id"), (String) rows.get(0).get("name"),
          ((UUID) rows.get(0).get("field_class")),
          (byte) (int) rows.get(0).get("inner"), (String) rows.get(0).get("show_name"));
    else
      return null;
  }

  @Override
  public StructureFieldAbstract getStructureFieldsByName(UUID classId, String name) {
    String sql =
        String.format("SELECT * FROM %1$s WHERE class_id = '%2$s' and name = '%3$s' limit 1",
            DBConstants.TBL_STRUCTURE_FIELDS, classId, name);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new StructureFieldImplemented((UUID) rows.get(0).get("id"),
          (UUID) rows.get(0).get("class_id"), (String) rows.get(0).get("name"),
          ((UUID) rows.get(0).get("field_class")),
          (byte) (int) rows.get(0).get("inner"), (String) rows.get(0).get("show_name"));
    else
      return null;
  }

  @SuppressWarnings("finally")
  @Override
  public Map<UUID, StructureFieldAbstract> queryByStructure(
      FieldsFactoryInterface fieldsInterface) {
    // TreeMap<UUID, StructureFieldAbstract> result = new TreeMap<UUID, StructureFieldAbstract>();
    try {
      return jdbcTemplate
          .query(fieldsInterface.getQuery(),
              (rs, rowNum) -> new StructureFieldImplemented(UUID.fromString(rs.getString("id")),
                  UUID.fromString(rs.getString("class_id")), rs.getString("name"),
                  UUID.fromString(rs.getString("field_class")), rs.getByte("inner"),
                  rs.getString("show_name")))
          .stream().collect(Collectors.toMap(value -> ((StructureFieldImplemented) value).getId(),
              value -> (StructureFieldImplemented) value));
    } catch (SQLException sqlex) {
      sqlex.printStackTrace();
      return null;
    }
  }

}