package com.matas.liteconstruct.db.models.accessliteral.repos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.accessliteral.abstractmodel.AccessLiteralModelAbstract;
import com.matas.liteconstruct.db.models.accessliteral.model.AccessLiteralModel;

public class AccessLiteralRepositoryImplemented implements AccessLiteralRepository {
  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addLiteral(AccessLiteralModelAbstract literal) {
    String sql = String.format(
        "insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s', '%7$s', '%8$s', '%9$s', '%10$s', '%11$s', '%12$s')",
        DBConstants.TBL_LITERAL, literal.getId(), literal.getName(), literal.getCompanyId(),
        literal.getPermissionclassId(), literal.getClassId(), literal.getFieldId(),
        literal.getRelationFieldId(), literal.getRecordFieldId(), literal.getRecordFieldValue(),
        literal.getTypeUse(), literal.getEditAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeLiteral(AccessLiteralModelAbstract literal) {
    String sql = String.format("delete from %1$s where id = '%2$s'", DBConstants.TBL_LITERAL,
        literal.getId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateLiteral(AccessLiteralModelAbstract literal) {
    String sql = String.format(
        "update %1$s set name = '%3$s', company_id = '%4$s', permission_class_id = '%5$s', class_id = '%6$s', field_id = '%7$s', "
            + "relation_field_id = '%8$s', record_field_id = '%9$s', record_field_value = '%10$s', type_use = '%11$s', "
            + "edit_access = '%12$s' where id = '%2$s'",
        DBConstants.TBL_LITERAL, literal.getId(), literal.getName(), literal.getCompanyId(),
        literal.getPermissionclassId(), literal.getClassId(), literal.getFieldId(),
        literal.getRelationFieldId(), literal.getRecordFieldId(), literal.getRecordFieldValue(),
        literal.getTypeUse(), literal.getEditAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public AccessLiteralModelAbstract getLiteralById(UUID literalId) {
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(
        "SELECT * FROM " + DBConstants.TBL_LITERAL + " WHERE id = '" + literalId + "' limit 1");
    if (rows != null && rows.size() > 0)
      return new AccessLiteralModel((UUID) rows.get(0).get("id"), (String) rows.get(0).get("name"),
          (UUID) rows.get(0).get("company_id"), (UUID) rows.get(0).get("permission_class_id"),
          (UUID) rows.get(0).get("class_id"), (UUID) rows.get(0).get("field_id"),
          (UUID) rows.get(0).get("relation_field_id"), (UUID) rows.get(0).get("record_field_id"),
          (UUID) rows.get(0).get("record_field_value"), (Short) rows.get(0).get("edit_access"),
          (Short) rows.get(0).get("type_use"));

    else
      return null;
  }

  @SuppressWarnings("finally")
  @Override
  public List<AccessLiteralModelAbstract> getLiteralsByCompanyId(UUID companyId) {
    ArrayList<AccessLiteralModelAbstract> result = new ArrayList<>(20);
    try {
      String sql = String.format("select * from %1$s where company_id = '%2$s'",
          DBConstants.TBL_LITERAL, companyId);
      jdbcTemplate
          .query(sql,
              (rs, rowNum) -> new AccessLiteralModel(
                  CommonMethods.getUUID(rs.getString("strucutre_literal")), "noname",
                  CommonMethods.getUUID(rs.getString("company_id")),
                  CommonMethods.getUUID(rs.getString("str_permission_class_id")),
                  CommonMethods.getUUID(rs.getString("str_class_id")),
                  CommonMethods.getUUID(rs.getString("str_field_id")),
                  CommonMethods.getUUID(rs.getString("str_relation_field_id")),
                  CommonMethods.getUUID(rs.getString("str_record_field_id")),
                  CommonMethods.getUUID(rs.getString("str_record_field_value")),
                  rs.getShort("str_type_use"), rs.getShort("edit_access")))

          .forEach(structure -> {
            result.add(structure);
          });
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
    } finally {
      return result;
    }
  }

  @SuppressWarnings("finally")
  @Override
  public List<AccessLiteralModelAbstract> getLiteralsByCompanyIdclassId(UUID companyId,
      UUID classId) {
    ArrayList<AccessLiteralModelAbstract> result = new ArrayList<>(20);
    try {
      String sql =
          String.format("select * from %1$s where company_id = '%2$s' and class_id = '%3$s'",
              DBConstants.TBL_LITERAL, companyId, classId);
      jdbcTemplate.query(sql,
          (rs, rowNum) -> new AccessLiteralModel(
              CommonMethods.getUUID(rs.getString("strucutre_literal")), "noname",
              CommonMethods.getUUID(rs.getString("company_id")),
              CommonMethods.getUUID(rs.getString("permission_class_id")),
              CommonMethods.getUUID(rs.getString("class_id")),
              CommonMethods.getUUID(rs.getString("field_id")),
              CommonMethods.getUUID(rs.getString("relation_field_id")),
              CommonMethods.getUUID(rs.getString("record_field_id")),
              CommonMethods.getUUID(rs.getString("record_field_value")),
              rs.getShort("str_type_use"), rs.getShort("edit_access")))
          .forEach(structure -> {
            result.add(structure);
          });
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
    } finally {
      return result;
    }
  }

  @Override
  public String getValueFromDataByLiteral(AccessLiteralModelAbstract literal) {
    if (literal == null || literal.getClassId() == null) {
      return null;
    }
    String sql = "";
    if (literal == null)
      return null;
    if (literal.getTypeUse() == 1) {
      sql = String.format(
          "select \"cc_%1$s_data_use\".\"%2$s\" from \"cc_%1$s_data_use\" where \"%3$s\" = '%4$s' limit 1",
          literal.getClassId(), literal.getFieldId(), literal.getRecordFieldId(),
          literal.getRecordFieldValue());
    }
    String[] result = new String[1];
    jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString(literal.getFieldId().toString()))
        .forEach(structure -> {
          result[0] = structure;
        });
    return result[0];
  }

  @Override
  public String getValueFromStructureByLiteral(AccessLiteralModelAbstract literal) {
    if (literal == null || literal.getClassId() == null) {
      return null;
    }
    String sql = "";
    if (literal == null)
      return null;
    if (literal.getTypeUse() == 1) {
      sql = String.format(
          "select \"cc_%1$s_data_use\".\"%2$s\" from \"cc_%1$s_data_use\" where \"%3$s\" = '%4$s' limit 1",
          literal.getClassId(), literal.getFieldId(), literal.getRecordFieldId(),
          literal.getRecordFieldValue());
    }
    String[] result = new String[1];
    jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString(literal.getFieldId().toString()))
        .forEach(structure -> {
          result[0] = structure;
        });
    return result[0];
  }
}
