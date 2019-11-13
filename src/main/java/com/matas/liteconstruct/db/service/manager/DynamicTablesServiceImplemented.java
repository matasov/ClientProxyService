package com.matas.liteconstruct.db.service.manager;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.controller.management.StructureClassController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicTablesServiceImplemented implements DynamicTablesService {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addTables(UUID classId, UUID idFieldId, UUID ownerFieldId, UUID createFieldId,
      UUID changeFieldId) {
    String sql = String.format(
        "CREATE TABLE \"public\".\"cc_%1$s_data_use\" (\"%2$s\" uuid NOT NULL,"
            + "    \"%3$s\" uuid NOT NULL,"
            + "    \"%4$s\" bigint DEFAULT date_part('epoch', now()) * 1000 NOT NULL,"
            + "    \"%5$s\" bigint DEFAULT date_part('epoch', now()) * 1000 NOT NULL, "
            + "    CONSTRAINT \"cc_%1$s_data_use_id\" PRIMARY KEY (\"%2$s\")"
            + ") WITH (oids = false);",
        classId, idFieldId, ownerFieldId, createFieldId, changeFieldId);
    jdbcTemplate.execute(sql);
    sql = String.format("CREATE TABLE \"public\".\"cc_%1$s_implemented_records\" ("
        + "    \"implemented_id\" uuid NOT NULL, \"record_id\" uuid NOT NULL, "
        + "    \"turn\" smallint DEFAULT '0' NOT NULL, "
        + "    CONSTRAINT \"сс_%1$s_implemented_records_id\" PRIMARY KEY (\"implemented_id\", \"record_id\")"
        + ") WITH (oids = false);", classId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void removeTables(UUID classId) {
    String sql = String.format(
        "DROP TABLE \"cc_%1$s_data_use\"; DROP TABLE \"cc_%1$s_implemented_records\"; ", classId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void addNewField(UUID classId, UUID fieldId, String typeField, String defaultValue,
      boolean uniqueToken) {
    String defaultValueConstraint = "";
    if (defaultValue != null && !defaultValue.equals(""))
      defaultValueConstraint = " NOT NULL DEFAULT '" + defaultValue + "'";
    String sql = String.format("ALTER TABLE \"cc_%1$s_data_use\" ADD \"%2$s\" %3$s %4$s;", classId,
        fieldId, typeField, defaultValueConstraint);
    if (uniqueToken) {
      sql += String.format("ADD CONSTRAINT \"%1$s_%2$s\" UNIQUE (\"%2$s\");", classId, fieldId);
    }
    log.info("addNewField to table: {}", sql);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void updateFieldType(UUID classId, UUID fieldId, String newTypeField, String defaultValue,
      boolean uniqueToken) {
    String defaultValueConstraint = "";
    if (defaultValue != null && !defaultValue.equals(""))
      defaultValueConstraint = " NOT NULL DEFAULT '" + defaultValue + "'";
    String sql = String.format(
        "ALTER TABLE \"cc_%1$s_data_use\" ALTER COLUMN \"%2$s\" TYPE %3$s USING \"%2$s\"::%3$s %4$s;",
        classId, fieldId, newTypeField, defaultValueConstraint);
    if (uniqueToken) {
      sql += String.format(
          "ALTER TABLE \"cc_%1$s_data_use\" ADD CONSTRAINT \"%1$s_%2$s\" UNIQUE (\"%2$s\");",
          classId, fieldId);
    } else {
      sql +=
          String.format("ALTER TABLE \"cc_%1$s_data_use\" DROP CONSTRAINT IF EXISTS \"%1$s_%2$s\";",
              classId, fieldId);
    }
    jdbcTemplate.execute(sql);
  }

  @Override
  public void removeFieldType(UUID classId, UUID fieldId) {
    String sql = String.format(
        "ALTER TABLE \"cc_%1$s_data_use\" DROP \"%2$s\"; ALTER TABLE \"cc_%1$s_data_use\" DROP CONSTRAINT IF EXISTS \"%1$s_%2$s\";",
        classId, fieldId);
    jdbcTemplate.execute(sql);
  }

}
