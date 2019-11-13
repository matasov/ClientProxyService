package com.matas.liteconstruct.db.models.streamfiltersrecord.repos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.streamfiltersrecord.abstractmodel.FiltersRecordModelAbstract;
import com.matas.liteconstruct.db.models.streamfiltersrecord.model.FiltersRecordModel;
import com.matas.liteconstruct.db.models.streamliterals.model.LiteralModel;
import com.matas.liteconstruct.db.CommonMethods;

public class FiltersRecordRepositoryImplemented implements FiltersRecordRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addFiltersRecord(FiltersRecordModelAbstract filterRecord) {
    String sql = String.format(
        "insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s', '%7$s', '%8$s')",
        DBConstants.TBL_FILTER_RECORD, filterRecord.getId(), filterRecord.getName(),
        filterRecord.getCompanyId(), filterRecord.getStructureLiteralId(),
        filterRecord.getOperator(), filterRecord.getComplexDataValue(),
        filterRecord.getEditAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeFiltersRecord(FiltersRecordModelAbstract filterRecord) {
    String sql = String.format("delete from %1$s where id = '%2$s'", DBConstants.TBL_FILTER_RECORD,
        filterRecord.getId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateFiltersRecord(FiltersRecordModelAbstract filterRecord) {
    String sql = String.format(
        "update %1$s set name = '%3$s', company_id = '%4$s', strucutre_literal = '%5$s', compareoperation = '%6$s', "
            + "value_literal = '%7$s', edit_access = '%8$s' where id = '%2$s'",
        DBConstants.TBL_FILTER_RECORD, filterRecord.getId(), filterRecord.getName(),
        filterRecord.getCompanyId(), filterRecord.getStructureLiteralId(),
        filterRecord.getOperator(), filterRecord.getComplexDataValue(),
        filterRecord.getEditAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public FiltersRecordModelAbstract getFiltersRecordById(UUID filterRecordId) {
    StringBuilder sql = new StringBuilder(1000);
    sql.append("SELECT \"").append(DBConstants.TBL_FILTER_RECORD).append("\".*, \"")
        .append(DBConstants.TBL_LITERAL).append("\".class_id as val_class_id, \"")
        .append(DBConstants.TBL_LITERAL).append("\".field_id as val_field_id, \"")
        .append(DBConstants.TBL_LITERAL).append("\".record_field_id as val_record_field_id, \"")
        .append(DBConstants.TBL_LITERAL)
        .append("\".record_field_value as val_record_field_value, \"")
        .append(DBConstants.TBL_LITERAL).append("\".type_use as val_type_use, \"")
        .append(DBConstants.TBL_LITERAL).append("\".type_data as val_type_data, \"")
        .append(DBConstants.TBL_LITERAL).append("\".parent_class_id as val_parent_class_id, \"")
        .append(DBConstants.TBL_LITERAL)
        .append("\".parent_field_id as val_parent_field_id FROM (SELECT \"")
        .append(DBConstants.TBL_FILTER_RECORD).append("\".*, \"").append(DBConstants.TBL_LITERAL)
        .append("\".class_id as str_class_id, \"").append(DBConstants.TBL_LITERAL)
        .append("\".field_id as str_field_id, \"").append(DBConstants.TBL_LITERAL)
        .append("\".record_field_id as str_record_field_id, \"").append(DBConstants.TBL_LITERAL)
        .append("\".record_field_value as str_record_field_value, \"")
        .append(DBConstants.TBL_LITERAL).append("\".type_use as str_type_use, \"")
        .append(DBConstants.TBL_LITERAL).append("\".type_data as str_type_data, \"")
        .append(DBConstants.TBL_LITERAL).append("\".parent_class_id as str_parent_class_id, \"")
        .append(DBConstants.TBL_LITERAL).append("\".parent_field_id as str_parent_field_id FROM \"")
        .append(DBConstants.TBL_FILTER_RECORD).append("\" join \"").append(DBConstants.TBL_LITERAL)
        .append("\" on \"").append(DBConstants.TBL_FILTER_RECORD)
        .append("\".\"strucutre_literal\" = \"").append(DBConstants.TBL_LITERAL)
        .append("\".\"id\" where \"").append(DBConstants.TBL_FILTER_RECORD).append("\".id = '")
        .append(filterRecordId).append("') as \"").append(DBConstants.TBL_FILTER_RECORD)
        .append("\" left join \"").append(DBConstants.TBL_LITERAL).append("\" on case when \"")
        .append(DBConstants.TBL_FILTER_RECORD)
        .append(
            "\".value_literal ~ E'^[[:xdigit:]]{8}-([[:xdigit:]]{4}-){3}[[:xdigit:]]{12}$' and \"")
        .append(DBConstants.TBL_FILTER_RECORD).append("\".\"value_literal\"::uuid = \"")
        .append(DBConstants.TBL_LITERAL).append("\".\"id\" then TRUE else FALSE end");
    FiltersRecordModelAbstract[] result = new FiltersRecordModelAbstract[1];
    try {
      jdbcTemplate
          .query(sql.toString(),
              (rs, rowNum) -> new FiltersRecordModel(CommonMethods.getUUID(rs.getString("id")),
                  rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
                  CommonMethods.getUUID(rs.getString("strucutre_literal")),
                  rs.getShort("compareoperation"), rs.getString("value_literal"),
                  rs.getShort("edit_access"),

                  new LiteralModel(CommonMethods.getUUID(rs.getString("strucutre_literal")),
                      "noname", CommonMethods.getUUID(rs.getString("company_id")),
                      CommonMethods.getUUID(rs.getString("str_class_id")),
                      CommonMethods.getUUID(rs.getString("str_field_id")),
                      CommonMethods.getUUID(rs.getString("str_record_field_id")),
                      rs.getString("str_record_field_value"), rs.getShort("str_type_use"),
                      rs.getShort("str_type_data"),
                      CommonMethods.getUUID(rs.getString("str_parent_class_id")),
                      CommonMethods.getUUID(rs.getString("str_parent_field_id")), (short) 0),

                  (rs.getString("val_class_id") == null ? null
                      : new LiteralModel(CommonMethods.getUUID(rs.getString("value_literal")),
                          "noname", CommonMethods.getUUID(rs.getString("company_id")),
                          CommonMethods.getUUID(rs.getString("val_class_id")),
                          CommonMethods.getUUID(rs.getString("val_field_id")),
                          CommonMethods.getUUID(rs.getString("val_record_field_id")),
                          rs.getString("val_record_field_value"), rs.getShort("val_type_use"),
                          rs.getShort("val_type_data"),
                          CommonMethods.getUUID(rs.getString("val_parent_class_id")),
                          CommonMethods.getUUID(rs.getString("val_parent_field_id")), (short) 0))))
          .forEach(structure -> {
            result[0] = structure;
          });
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
    } finally {
      return result[0];
    }
  }

  @Override
  public List<FiltersRecordModelAbstract> getFiltersRecordsByCompanyId(UUID companyId, short editAccess) {
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT \"").append(DBConstants.TBL_FILTER_RECORD).append("\".*, \"")
        .append(DBConstants.TBL_LITERAL).append("\".class_id as val_class_id, \"")
        .append(DBConstants.TBL_LITERAL).append("\".field_id as val_field_id, \"")
        .append(DBConstants.TBL_LITERAL).append("\".record_field_id as val_record_field_id, \"")
        .append(DBConstants.TBL_LITERAL)
        .append("\".record_field_value as val_record_field_value, \"")
        .append(DBConstants.TBL_LITERAL).append("\".type_use as val_type_use, \"")
        .append(DBConstants.TBL_LITERAL).append("\".type_data as val_type_data, \"")
        .append(DBConstants.TBL_LITERAL).append("\".parent_class_id as val_parent_class_id, \"")
        .append(DBConstants.TBL_LITERAL)
        .append("\".parent_field_id as val_parent_field_id FROM (SELECT \"")
        .append(DBConstants.TBL_FILTER_RECORD).append("\".*, \"").append(DBConstants.TBL_LITERAL)
        .append("\".class_id as str_class_id, \"").append(DBConstants.TBL_LITERAL)
        .append("\".field_id as str_field_id, \"").append(DBConstants.TBL_LITERAL)
        .append("\".record_field_id as str_record_field_id, \"").append(DBConstants.TBL_LITERAL)
        .append("\".record_field_value as str_record_field_value, \"")
        .append(DBConstants.TBL_LITERAL).append("\".type_use as str_type_use, \"")
        .append(DBConstants.TBL_LITERAL).append("\".type_data as str_type_data, \"")
        .append(DBConstants.TBL_LITERAL).append("\".parent_class_id as str_parent_class_id, \"")
        .append(DBConstants.TBL_LITERAL).append("\".parent_field_id as str_parent_field_id FROM \"")
        .append(DBConstants.TBL_FILTER_RECORD).append("\" join \"").append(DBConstants.TBL_LITERAL)
        .append("\" on \"").append(DBConstants.TBL_FILTER_RECORD)
        .append("\".\"strucutre_literal\" = \"").append(DBConstants.TBL_LITERAL)
        .append("\".\"id\" where \"").append(DBConstants.TBL_FILTER_RECORD)
        .append("\".company_id = '").append(companyId).append("' and \"").append(DBConstants.TBL_FILTER_RECORD)
        .append(DBConstants.TBL_FILTER_RECORD)
        .append("\".edit_access >= '").append(editAccess).append("') as \"")
        .append(DBConstants.TBL_FILTER_RECORD).append("\" left join \"")
        .append(DBConstants.TBL_LITERAL).append("\" on case when \"")
        .append(DBConstants.TBL_FILTER_RECORD)
        .append(
            "\".value_literal ~ E'^[[:xdigit:]]{8}-([[:xdigit:]]{4}-){3}[[:xdigit:]]{12}$' and \"")
        .append(DBConstants.TBL_FILTER_RECORD).append("\".\"value_literal\"::uuid = \"")
        .append(DBConstants.TBL_LITERAL).append("\".\"id\" then TRUE else FALSE end");

    List<FiltersRecordModelAbstract> result = new ArrayList<>(5);
    try {
      jdbcTemplate.query(sql.toString(),
          (rs, rowNum) -> new FiltersRecordModel(CommonMethods.getUUID(rs.getString("id")),
              rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")), 
              CommonMethods.getUUID(rs.getString("strucutre_literal")),
              rs.getShort("compareoperation"), rs.getString("value_literal"),
              rs.getShort("edit_access"),

              new LiteralModel(CommonMethods.getUUID(rs.getString("strucutre_literal")), "noname",
                  CommonMethods.getUUID(rs.getString("company_id")),
                  CommonMethods.getUUID(rs.getString("str_class_id")),
                  CommonMethods.getUUID(rs.getString("str_field_id")),
                  CommonMethods.getUUID(rs.getString("str_record_field_id")),
                  rs.getString("str_record_field_value"), rs.getShort("str_type_use"),
                  rs.getShort("str_type_data"),
                  CommonMethods.getUUID(rs.getString("str_parent_class_id")),
                  CommonMethods.getUUID(rs.getString("str_parent_field_id")),
                  rs.getShort("str_edit_access")),

              (rs.getString("val_class_id") == null ? null
                  : new LiteralModel(CommonMethods.getUUID(rs.getString("value_literal")), "noname",
                      CommonMethods.getUUID(rs.getString("company_id")),
                      CommonMethods.getUUID(rs.getString("val_class_id")),
                      CommonMethods.getUUID(rs.getString("val_field_id")),
                      CommonMethods.getUUID(rs.getString("val_record_field_id")),
                      rs.getString("val_record_field_value"), rs.getShort("val_type_use"),
                      rs.getShort("val_type_data"),
                      CommonMethods.getUUID(rs.getString("val_parent_class_id")),
                      CommonMethods.getUUID(rs.getString("val_parent_field_id")),
                      rs.getShort("val_edit_access")))))
          .forEach(field -> {
            result.add(field);
          });
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
    } finally {
      return result;
    }
  }

  @Override
  public Map<UUID, FiltersRecordModelAbstract> getMapFiltersRecordsByListId(List<Object> listUUID) {
    if (listUUID == null || listUUID.isEmpty()) {
      return null;
    }
    // try {
    // listUUID.add(UUID.fromString("4bfce22f-2d62-4638-9b42-3e098973a2e5"));
    // System.out.println(listUUID.get(0));
    //
    // }catch(Exception ex) {
    // ex.printStackTrace();
    // }
    String subquery = " where " + DBConstants.TBL_FILTER_RECORD + ".id = '"
        + listUUID.stream().map(x -> x.toString())
            .collect(Collectors.joining("' or " + DBConstants.TBL_FILTER_RECORD + ".id = '"));
    // System.out.println(subquery);
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT \"").append(DBConstants.TBL_FILTER_RECORD).append("\".*, \"")
        .append(DBConstants.TBL_LITERAL).append("\".class_id as val_class_id, \"")
        .append(DBConstants.TBL_LITERAL).append("\".field_id as val_field_id, \"")
        .append(DBConstants.TBL_LITERAL).append("\".record_field_id as val_record_field_id, \"")
        .append(DBConstants.TBL_LITERAL)
        .append("\".record_field_value as val_record_field_value, \"")
        .append(DBConstants.TBL_LITERAL).append("\".type_use as val_type_use, \"")
        .append(DBConstants.TBL_LITERAL).append("\".type_data as val_type_data, \"")
        .append(DBConstants.TBL_LITERAL).append("\".parent_class_id as val_parent_class_id, \"")
        .append(DBConstants.TBL_LITERAL)
        .append("\".parent_field_id as val_parent_field_id FROM (SELECT \"")
        .append(DBConstants.TBL_FILTER_RECORD).append("\".*, \"").append(DBConstants.TBL_LITERAL)
        .append("\".class_id as str_class_id, \"").append(DBConstants.TBL_LITERAL)
        .append("\".field_id as str_field_id, \"").append(DBConstants.TBL_LITERAL)
        .append("\".record_field_id as str_record_field_id, \"").append(DBConstants.TBL_LITERAL)
        .append("\".record_field_value as str_record_field_value, \"")
        .append(DBConstants.TBL_LITERAL).append("\".type_use as str_type_use, \"")
        .append(DBConstants.TBL_LITERAL).append("\".type_data as str_type_data, \"")
        .append(DBConstants.TBL_LITERAL).append("\".parent_class_id as str_parent_class_id, \"")
        .append(DBConstants.TBL_LITERAL).append("\".parent_field_id as str_parent_field_id FROM \"")
        .append(DBConstants.TBL_FILTER_RECORD).append("\" join \"").append(DBConstants.TBL_LITERAL)
        .append("\" on \"").append(DBConstants.TBL_FILTER_RECORD)
        .append("\".\"strucutre_literal\" = \"").append(DBConstants.TBL_LITERAL)
        .append("\".\"id\" ").append(subquery).append("') as \"")
        .append(DBConstants.TBL_FILTER_RECORD).append("\" left join \"")
        .append(DBConstants.TBL_LITERAL).append("\" on case when \"")
        .append(DBConstants.TBL_FILTER_RECORD)
        .append(
            "\".value_literal ~ E'^[[:xdigit:]]{8}-([[:xdigit:]]{4}-){3}[[:xdigit:]]{12}$' and \"")
        .append(DBConstants.TBL_FILTER_RECORD).append("\".\"value_literal\"::uuid = \"")
        .append(DBConstants.TBL_LITERAL).append("\".\"id\" then TRUE else FALSE end");
    System.out.println(sql.toString());
    Map<UUID, FiltersRecordModelAbstract> result = new HashMap<>(5);
    try {
      jdbcTemplate
          .query(sql.toString(),
              (rs, rowNum) -> new FiltersRecordModel(CommonMethods.getUUID(rs.getString("id")),
                  rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
                  CommonMethods.getUUID(rs.getString("strucutre_literal")),
                  rs.getShort("compareoperation"), rs.getString("value_literal"),
                  rs.getShort("edit_access"),

                  new LiteralModel(CommonMethods.getUUID(rs.getString("strucutre_literal")),
                      "noname", CommonMethods.getUUID(rs.getString("company_id")),
                      CommonMethods.getUUID(rs.getString("str_class_id")),
                      CommonMethods.getUUID(rs.getString("str_field_id")),
                      CommonMethods.getUUID(rs.getString("str_record_field_id")),
                      rs.getString("str_record_field_value"), rs.getShort("str_type_use"),
                      rs.getShort("str_type_data"),
                      CommonMethods.getUUID(rs.getString("str_parent_class_id")),
                      CommonMethods.getUUID(rs.getString("str_parent_field_id")), (short) 0),

                  (rs.getString("val_class_id") == null ? null
                      : new LiteralModel(CommonMethods.getUUID(rs.getString("value_literal")),
                          "noname", CommonMethods.getUUID(rs.getString("company_id")),
                          CommonMethods.getUUID(rs.getString("val_class_id")),
                          CommonMethods.getUUID(rs.getString("val_field_id")),
                          CommonMethods.getUUID(rs.getString("val_record_field_id")),
                          rs.getString("val_record_field_value"), rs.getShort("val_type_use"),
                          rs.getShort("val_type_data"),
                          CommonMethods.getUUID(rs.getString("val_parent_class_id")),
                          CommonMethods.getUUID(rs.getString("val_parent_field_id")), (short) 0))))
          .forEach(field -> {
            result.put(field.getId(), field);
          });
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
    } finally {
      return result;
    }
  }

}
