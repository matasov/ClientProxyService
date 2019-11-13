package com.matas.liteconstruct.db.models.collections.repos;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.collections.abstractmodel.StructureCollectionAbstract;
import com.matas.liteconstruct.db.models.collections.model.StructureCollectionImplemented;
import com.matas.liteconstruct.db.models.structure.model.StructureFieldImplemented;
import com.matas.liteconstruct.db.tools.StructrueCollectionEnum;
import com.matas.liteconstruct.db.tools.sqlselectfactory.FieldsFactoryInterface;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StructureCollectionsFieldsRepositoryImplemented
    implements StructureCollectionsFieldsRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addStructureFields(StructureCollectionAbstract field) {
    String sql = String.format(
        "insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s', '%7$s', '%8$s', '%9$s', '%10$s')",
        DBConstants.TBL_COLLECTIONS_STRUCTURE, field.getId(), field.getClassId(),
        field.getFieldId(), field.getTurn(), field.isUseful(), field.isVisible(), field.isEdit(),
        field.isDelete(), field.isInsert());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeStructureField(UUID id, UUID classId, UUID fieldId) {
    String sql =
        String.format("delete from %1$s where id = '%2$s' and class_id = '%3$s' and field = '%4$s'",
            DBConstants.TBL_COLLECTIONS_STRUCTURE, id, classId, fieldId);
    log.info("removeStructureField: {}", sql);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void removeStructureFieldsByclassId(UUID classId) {
    String sql = String.format("delete from %1$s where class_id = '%2$s'",
        DBConstants.TBL_COLLECTIONS_STRUCTURE, classId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void updateStructureFields(StructureCollectionAbstract field) {
    String sql = String.format(
        "update %1$s set turn = '%5$s', usef = '%6$s', visible = '%7$s', edit = '%8$s', delete = '%9$s', insert = '%10$s' where id = '%2$s' and class_id = '%3$s' and field = '%4$s'",
        DBConstants.TBL_COLLECTIONS_STRUCTURE, field.getId(), field.getClassId(),
        field.getFieldId(), field.getTurn(), field.isUseful(), field.isVisible(), field.isEdit(),
        field.isDelete(), field.isInsert());
    log.info("updateStructureFields: {}", sql);
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateDependsStructureFields(UUID classId, UUID fieldId, UUID companyId,
      String fieldPermissionsName) {
    // String sql = String.format(
    // "UPDATE %1$s SET \"%5$s\" = false WHERE %1$s.field = '%4$s' AND %1$s.id IN (SELECT
    // collection_id FROM %2$s WHERE %2$s.class_id = '%3$s' AND %2$s.dynamic_role_id != '%6$s')",
    // DBConstants.TBL_COLLECTIONS_STRUCTURE, DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION,
    // classId, fieldId, companyId, fieldPermissionsName, "62900a19-88a9-4655-a7ac-71488070b659");
    String sql = String.format(
        "UPDATE %1$s SET \"%7$s\" = false WHERE %1$s.field = '%5$s' AND %1$s.id IN (SELECT %2$s.collection_id FROM %2$s JOIN %2$s ON %3$s.dynamic_role_id = %3$s.id WHERE  %2$s.class_id = '%4$s' AND %3$s.company_id = '%6$s' AND %3$s.role_id != '%8$s')",
        DBConstants.TBL_COLLECTIONS_STRUCTURE, DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION,
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE, classId, fieldId, companyId, fieldPermissionsName,
        "1d021b86-41c6-47c1-a38e-0aa89b98dc28");
    // if (!withSuperadmin) {
    // sql += String.format("AND %1$s.dynamic_role_id != '%2$s'",
    // DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, "62900a19-88a9-4655-a7ac-71488070b659");
    // }
    log.info("updateStructureFields: {}", sql);
    jdbcTemplate.update(sql);
  }

  @Override
  public StructureCollectionAbstract getStructureCollectionFieldByKey(UUID id, UUID classId,
      UUID fieldId) {
    String sql = String.format(
        "SELECT \"%2$s\".\"id\" as \"collection_id\", \"%2$s\".\"class_id\", \"%2$s\".\"field\" as \"field_id\", "
            + "\"%2$s\".\"turn\", \"%2$s\".\"usef\", \"%2$s\".\"visible\", \"%2$s\".\"edit\" "
            + ", \"%2$s\".\"delete\", \"%2$s\".\"insert\", \"%1$s\".\"name\", \"%1$s\".\"field_class\", "
            + "\"%1$s\".\"inner\", \"%1$s\".\"show_name\" FROM \"%2$s\" join \"%1$s\" on "
            + "\"%2$s\".\"field\" = \"%1$s\".\"id\" and \"%2$s\".\"class_id\" = \"%1$s\".\"class_id\" "
            + "WHERE \"%2$s\".\"id\" = '%3$s' and "
            + "\"%2$s\".\"class_id\" = '%4$s' and \"%2$s\".\"field\" = '%5$s'",
        DBConstants.TBL_STRUCTURE_FIELDS, DBConstants.TBL_COLLECTIONS_STRUCTURE, id, classId,
        fieldId);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new StructureCollectionImplemented((UUID) rows.get(0).get("id"),
          (UUID) rows.get(0).get("class_id"), (UUID) rows.get(0).get("field"),
          (int) rows.get(0).get("turn"), (boolean) rows.get(0).get("usef"),
          (boolean) rows.get(0).get("visible"), (boolean) rows.get(0).get("edit"),
          (boolean) rows.get(0).get("delete"), (boolean) rows.get(0).get("insert"),
          new StructureFieldImplemented(fieldId, classId, (String) rows.get(0).get("name"),
              (UUID) rows.get(0).get("field_class"), (byte) (int) rows.get(0).get("turn"),
              (String) rows.get(0).get("show_name")));
    else
      return null;
  }

  @Override
  public List<StructureCollectionAbstract> selectOrderedFields(UUID id, UUID classId) {
    String sql =
        String.format("SELECT * FROM %1$s WHERE id = '%2$s' and class_id = '%3$s' order by turn",
            DBConstants.TBL_COLLECTIONS_STRUCTURE, id, classId);
    log.info("selectOrderedFields: {}", sql);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new StructureCollectionImplemented(UUID.fromString(rs.getString("id")),
            UUID.fromString(rs.getString("class_id")), UUID.fromString(rs.getString("field")),
            rs.getInt("turn"), rs.getBoolean("usef"), rs.getBoolean("visible"),
            rs.getBoolean("edit"), rs.getBoolean("delete"), rs.getBoolean("insert"), null));
  }

  @SuppressWarnings("finally")
  @Override
  public List<StructureCollectionAbstract> queryByStructure(
      FieldsFactoryInterface fieldsInterface) {
    try {
      return jdbcTemplate.query(fieldsInterface.getQuery(),
          (rs, rowNum) -> new StructureCollectionImplemented(UUID.fromString(rs.getString("id")),
              UUID.fromString(rs.getString("class_id")), UUID.fromString(rs.getString("field")),
              rs.getInt("turn"), rs.getBoolean("usef"), rs.getBoolean("visible"),
              rs.getBoolean("edit"), rs.getBoolean("delete"), rs.getBoolean("insert"),
              new StructureFieldImplemented(UUID.fromString(rs.getString("field")),
                  UUID.fromString(rs.getString("class_id")), rs.getString("name"),
                  UUID.fromString(rs.getString("field_class")), rs.getByte(StructrueCollectionEnum.INNER.toString()),
                  rs.getString("show_name"))));
    } catch (DataAccessException | SQLException e) {
      log.error("error in queryByStructure: {}", e.getMessage());
      return null;
    }
  }

}
