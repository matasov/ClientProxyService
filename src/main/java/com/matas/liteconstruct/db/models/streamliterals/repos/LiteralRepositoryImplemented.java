package com.matas.liteconstruct.db.models.streamliterals.repos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.streamliterals.abstractmodel.LiteralModelAbstract;
import com.matas.liteconstruct.db.models.streamliterals.model.LiteralModel;

public class LiteralRepositoryImplemented implements LiteralRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addLiteral(LiteralModelAbstract literal) {
    String sql = String.format(
        "insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s', '%7$s', '%8$s', '%9$s', '%10$s', '%11$s', '%12$s', '%13$s')",
        DBConstants.TBL_LITERAL, literal.getId(), literal.getName(), literal.getCompanyId(),
        literal.getClassId(), literal.getFieldId(), literal.getRecordFieldId(),
        literal.getRecordFieldValue(), literal.getTypeUse(), literal.getTypeData(),
        literal.getParentclassId(), literal.getParentFieldId(), literal.getEditAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeLiteral(LiteralModelAbstract literal) {
    String sql = String.format("delete from %1$s where id = '%2$s'", DBConstants.TBL_LITERAL,
        literal.getId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateLiteral(LiteralModelAbstract literal) {
    String sql = String.format(
        "update %1$s set name = '%3$s', company_id = '%4$s', class_id = '%5$s', field_id = '%6$s', "
            + "record_field_id = '%7$s', record_field_value = '%8$s', type_use = '%9$s', type_data = '%10$s', parent_class_id = '%11$s', parent_field_id = '%12$s', "
            + "edit_access = '%13$s' where id = '%2$s'",
            DBConstants.TBL_LITERAL, literal.getId(), literal.getName(), literal.getCompanyId(),
            literal.getClassId(), literal.getFieldId(), literal.getRecordFieldId(),
            literal.getRecordFieldValue(), literal.getTypeUse(), literal.getTypeData(),
            literal.getParentclassId(), literal.getParentFieldId(), literal.getEditAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public LiteralModelAbstract getLiteralById(UUID literalId) {
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(
        "SELECT * FROM " + DBConstants.TBL_LITERAL + " WHERE id = '" + literalId + "' limit 1");
    if (rows != null && rows.size() > 0)
      return new LiteralModel((UUID) rows.get(0).get("id"), (String) rows.get(0).get("name"),
          (UUID) rows.get(0).get("company_id"), (UUID) rows.get(0).get("class_id"),
          (UUID) rows.get(0).get("field_id"), (UUID) rows.get(0).get("record_field_id"),
          (String) rows.get(0).get("record_field_value"), (Short) rows.get(0).get("type_use"),
          (Short) rows.get(0).get("type_data"), (UUID) rows.get(0).get("parent_class_id"),
          (UUID) rows.get(0).get("parent_field_id"), (Short) rows.get(0).get("edit_access"));
    else
      return null;
  }

  @SuppressWarnings("finally")
  @Override
  public List<LiteralModelAbstract> getLiteralsByCompanyId(UUID companyId) {
    ArrayList<LiteralModelAbstract> result = new ArrayList<>(20);
    try {
      String sql = String.format("select * from %1$s where company_id = '%2$s'",
          DBConstants.TBL_LITERAL, companyId);
      jdbcTemplate
          .query(sql, (rs, rowNum) -> new LiteralModel(CommonMethods.getUUID(rs.getString("id")),
              rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
              CommonMethods.getUUID(rs.getString("class_id")),
              CommonMethods.getUUID(rs.getString("field_id")),
              CommonMethods.getUUID(rs.getString("record_field_id")),
              rs.getString("record_field_value"), rs.getShort("type_use"), rs.getShort("type_data"),
              CommonMethods.getUUID(rs.getString("parent_class_id")),
              CommonMethods.getUUID(rs.getString("parent_field_id")), rs.getShort("edit_access")))
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
  public List<LiteralModelAbstract> getLiteralsByCompanyIdclassId(UUID companyId, UUID classId) {
    ArrayList<LiteralModelAbstract> result = new ArrayList<>(20);
    try {
      String sql =
          String.format("select * from %1$s where company_id = '%2$s' and class_id = '%3$s'",
              DBConstants.TBL_LITERAL, companyId, classId);
      jdbcTemplate
          .query(sql, (rs, rowNum) -> new LiteralModel(CommonMethods.getUUID(rs.getString("id")),
              rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
              CommonMethods.getUUID(rs.getString("class_id")),
              CommonMethods.getUUID(rs.getString("field_id")),
              CommonMethods.getUUID(rs.getString("record_field_id")),
              rs.getString("record_field_value"), rs.getShort("type_use"), rs.getShort("type_data"),
              CommonMethods.getUUID(rs.getString("parent_class_id")),
              CommonMethods.getUUID(rs.getString("parent_field_id")), rs.getShort("edit_access")))
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
  public String getValueFromDataByLiteral(LiteralModelAbstract literal) {
    if (literal == null || literal.getClassId() == null) {
      return null;
    }
    String[] result = new String[1];
    // String resultField;
    String sql = String.format(
        "select \"cc_%1$s_data_use\".\"%2$s\" from \"cc_%1$s_data_use\" where \"%3$s\" = '%4$s' limit 1",
        literal.getClassId(), literal.getFieldId(), literal.getRecordFieldId(),
        literal.getRecordFieldValue());
    // System.out.println(sql);
    // resultField = literal.getFieldId().toString();
    // if (literal.getTypeUse() == 1) {
    // // literal for incoming permission
    // sql = String.format(
    // "SELECT \"cc_%1$_data_use\".\"%2$\" FROM \"cc_%3$_implemented_records\" join
    // \"cc_%1$_data_use\" on \"cc_%1$_data_use\".\"e0cfbed0-5bfd-4484-9006-ba74f612c4d3\" =
    // \"cc_%3$_implemented_records\".\"implemented_id\" where record_id =
    // '8628879d-6df6-41ab-bd25-c2ba1783378f' order by
    // \"cc_fd27729c-0f30-444b-a124-e3e16069e7d0_data_use\".\"370a78d8-286b-4ccb-9bce-71a2524e84d3\"
    // desc limit 1",
    // literal.getClassId(), literal.getFieldId(), literal.getParentclassId(),
    // literal.getParentFieldId(),
    // incomingPermissions.get(literal.getParentclassId()));
    // //resultField = literal.getFieldId().toString();
    // }
    jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString(literal.getFieldId().toString()))
        .forEach(structure -> {
          result[0] = structure;
        });
    return result[0];
  }

}
