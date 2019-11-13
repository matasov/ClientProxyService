package com.matas.liteconstruct.db.models.accessfiltersrecord.repos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.accessfiltersrecord.abstractmodel.AccessFiltersRecordModelAbstract;
import com.matas.liteconstruct.db.models.accessfiltersrecord.model.AccessFiltersRecordModel;
import com.matas.liteconstruct.db.models.accessliteral.model.AccessLiteralModel;

public class AccessFiltersRecordRepositoryImplemented implements AccessFiltersRecordRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addFiltersRecord(AccessFiltersRecordModelAbstract filterRecord) {
    String sql = String.format(
        "insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s', '%7$s', '%8$s')",
        DBConstants.TBL_ACCESS_FILTER_RECORD, filterRecord.getId(), filterRecord.getName(),
        filterRecord.getCompanyId(), filterRecord.getStructureLiteralId(),
        filterRecord.getOperator(), filterRecord.getComplexDataValue(),
        filterRecord.getEditAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeFiltersRecord(AccessFiltersRecordModelAbstract filterRecord) {
    String sql = String.format("delete from %1$s where id = '%2$s'",
        DBConstants.TBL_ACCESS_FILTER_RECORD, filterRecord.getId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateFiltersRecord(AccessFiltersRecordModelAbstract filterRecord) {
    String sql = String.format(
        "update %1$s set name = '%3$s', company_id = '%4$s', strucutre_literal = '%5$s', compareoperation = '%6$s', "
            + "value_literal = '%7$s', edit_access = '%8$s' where id = '%2$s'",
        DBConstants.TBL_ACCESS_FILTER_RECORD, filterRecord.getId(), filterRecord.getName(),
        filterRecord.getCompanyId(), filterRecord.getStructureLiteralId(),
        filterRecord.getOperator(), filterRecord.getComplexDataValue(),
        filterRecord.getEditAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public AccessFiltersRecordModelAbstract getFiltersRecordById(UUID filterRecordId) {
    StringBuilder sql = new StringBuilder(1000);
    sql.append("SELECT \"").append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".*, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".permission_class_id as val_permission_class_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".class_id as val_class_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".field_id as val_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".relation_field_id as val_relation_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".record_field_id as val_record_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".record_field_value as val_record_field_value, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".type_use as val_type_use, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".edit_access as val_edit_access ")
        .append(" FROM (SELECT \"").append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".*, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".permission_class_id as str_permission_class_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".class_id as str_class_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".field_id as str_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".relation_field_id as str_relation_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".record_field_id as str_record_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".record_field_value as str_record_field_value, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".type_use as str_type_use, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".edit_access as str_edit_access FROM \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\" join \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\" on \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".\"strucutre_literal\" = \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".\"id\" ").append(" where \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".id = '").append(filterRecordId)
        .append("') as \"").append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\" left join \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\" on case when \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD)
        .append(
            "\".value_literal ~ E'^[[:xdigit:]]{8}-([[:xdigit:]]{4}-){3}[[:xdigit:]]{12}$' and \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".\"value_literal\"::uuid = \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".\"id\" then TRUE else FALSE end");
    try {
      return Optional
          .ofNullable(jdbcTemplate.query(sql.toString(),
              (rs, rowNum) -> new AccessFiltersRecordModel(
                  CommonMethods.getUUID(rs.getString("id")), rs.getString("name"),
                  CommonMethods.getUUID(rs.getString("company_id")),
                  CommonMethods.getUUID(rs.getString("strucutre_literal")),
                  rs.getShort("compareoperation"), rs.getString("value_literal"),
                  rs.getShort("edit_access"),

                  new AccessLiteralModel(CommonMethods.getUUID(rs.getString("strucutre_literal")),
                      "noname", CommonMethods.getUUID(rs.getString("company_id")),
                      CommonMethods.getUUID(rs.getString("str_permission_class_id")),
                      CommonMethods.getUUID(rs.getString("str_class_id")),
                      CommonMethods.getUUID(rs.getString("str_field_id")),
                      CommonMethods.getUUID(rs.getString("str_relation_field_id")),
                      CommonMethods.getUUID(rs.getString("str_record_field_id")),
                      CommonMethods.getUUID(rs.getString("str_record_field_value")),
                      rs.getShort("str_type_use"), (short) 0),

                  (rs.getString("val_class_id") == null ? null
                      : new AccessLiteralModel(
                          CommonMethods.getUUID(rs.getString("strucutre_literal")), "noname",
                          CommonMethods.getUUID(rs.getString("company_id")),
                          CommonMethods.getUUID(rs.getString("val_permission_class_id")),
                          CommonMethods.getUUID(rs.getString("val_class_id")),
                          CommonMethods.getUUID(rs.getString("val_field_id")),
                          CommonMethods.getUUID(rs.getString("val_relation_field_id")),
                          CommonMethods.getUUID(rs.getString("val_record_field_id")),
                          CommonMethods.getUUID(rs.getString("val_record_field_value")),
                          rs.getShort("val_type_use"), (short) 0)))))
          .map(Collection::stream).orElseGet(Stream::empty).findAny().orElse(null);
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
      return null;
    }
  }

  @Override
  public List<AccessFiltersRecordModelAbstract> getFiltersRecordsByCompanyId(UUID companyId,
      short editAccess) {
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT \"").append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".*, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".permission_class_id as val_permission_class_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".class_id as val_class_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".field_id as val_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".relation_field_id as val_relation_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".record_field_id as val_record_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".record_field_value as val_record_field_value, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".type_use as val_type_use, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".edit_access as val_edit_access ")
        .append(" FROM (SELECT \"").append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".*, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".permission_class_id as str_permission_class_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".class_id as str_class_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".field_id as str_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".relation_field_id as str_relation_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".record_field_id as str_record_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".record_field_value as str_record_field_value, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".type_use as str_type_use, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".edit_access as str_edit_access FROM \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\" join \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\" on \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".\"strucutre_literal\" = \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".\"id\" ").append(" where \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".company_id = '").append(companyId)
        .append("' and \"").append(DBConstants.TBL_ACCESS_FILTER_RECORD)
        .append("\".edit_access >= '").append(editAccess).append("') as \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\" left join \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\" on case when \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD)
        .append(
            "\".value_literal ~ E'^[[:xdigit:]]{8}-([[:xdigit:]]{4}-){3}[[:xdigit:]]{12}$' and \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".\"value_literal\"::uuid = \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".\"id\" then TRUE else FALSE end");

    List<AccessFiltersRecordModelAbstract> result = new ArrayList<>(5);
    try {
      return jdbcTemplate.query(sql.toString(),
          (rs, rowNum) -> new AccessFiltersRecordModel(CommonMethods.getUUID(rs.getString("id")),
              rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
              CommonMethods.getUUID(rs.getString("strucutre_literal")),
              rs.getShort("compareoperation"), rs.getString("value_literal"),
              rs.getShort("edit_access"),

              new AccessLiteralModel(CommonMethods.getUUID(rs.getString("strucutre_literal")),
                  "noname", CommonMethods.getUUID(rs.getString("company_id")),
                  CommonMethods.getUUID(rs.getString("str_permission_class_id")),
                  CommonMethods.getUUID(rs.getString("str_class_id")),
                  CommonMethods.getUUID(rs.getString("str_field_id")),
                  CommonMethods.getUUID(rs.getString("str_relation_field_id")),
                  CommonMethods.getUUID(rs.getString("str_record_field_id")),
                  CommonMethods.getUUID(rs.getString("str_record_field_value")),
                  rs.getShort("str_type_use"), (short) 0),

              (rs.getString("val_class_id") == null ? null
                  : new AccessLiteralModel(CommonMethods.getUUID(rs.getString("strucutre_literal")),
                      "noname", CommonMethods.getUUID(rs.getString("company_id")),
                      CommonMethods.getUUID(rs.getString("val_permission_class_id")),
                      CommonMethods.getUUID(rs.getString("val_class_id")),
                      CommonMethods.getUUID(rs.getString("val_field_id")),
                      CommonMethods.getUUID(rs.getString("val_relation_field_id")),
                      CommonMethods.getUUID(rs.getString("val_record_field_id")),
                      CommonMethods.getUUID(rs.getString("val_record_field_value")),
                      rs.getShort("val_type_use"), (short) 0))));
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
      return null;
    }
  }

  @Override
  public Map<UUID, AccessFiltersRecordModelAbstract> getMapFiltersRecordsByListId(
      List<Object> listUUID) {
    if (listUUID == null || listUUID.isEmpty()) {
      return null;
    }
    String subquery = " where " + DBConstants.TBL_ACCESS_FILTER_RECORD + ".id = '"
        + listUUID.stream().map(x -> x.toString()).collect(
            Collectors.joining("' or " + DBConstants.TBL_ACCESS_FILTER_RECORD + ".id = '"));
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT \"").append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".*, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".permission_class_id as val_permission_class_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".class_id as val_class_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".field_id as val_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".relation_field_id as val_relation_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".record_field_id as val_record_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".record_field_value as val_record_field_value, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".type_use as val_type_use, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".edit_access as val_edit_access ")
        .append(" FROM (SELECT \"").append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".*, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".permission_class_id as str_permission_class_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".class_id as str_class_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".field_id as str_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".relation_field_id as str_relation_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".record_field_id as str_record_field_id, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL)
        .append("\".record_field_value as str_record_field_value, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".type_use as str_type_use, \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".edit_access as str_edit_access FROM \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\" left join \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\" on \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".\"strucutre_literal\" = \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".\"id\" ").append(subquery)
        .append("') as \"").append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\" left join \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\" on case when \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD)
        .append(
            "\".value_literal ~ E'^[[:xdigit:]]{8}-([[:xdigit:]]{4}-){3}[[:xdigit:]]{12}$' and \"")
        .append(DBConstants.TBL_ACCESS_FILTER_RECORD).append("\".\"value_literal\"::uuid = \"")
        .append(DBConstants.TBL_ACCESS_LITERAL).append("\".\"id\" then TRUE else FALSE end");
    System.out.println("getMapFiltersRecordsByListId sql: " + sql);
    try {
      return Optional
          .ofNullable(jdbcTemplate.query(sql.toString(),
              (rs, rowNum) -> new AccessFiltersRecordModel(
                  CommonMethods.getUUID(rs.getString("id")), rs.getString("name"),
                  CommonMethods.getUUID(rs.getString("company_id")),
                  CommonMethods.getUUID(rs.getString("strucutre_literal")),
                  rs.getShort("compareoperation"), rs.getString("value_literal"),
                  rs.getShort("edit_access"),

                  new AccessLiteralModel(CommonMethods.getUUID(rs.getString("strucutre_literal")),
                      "noname", CommonMethods.getUUID(rs.getString("company_id")),
                      CommonMethods.getUUID(rs.getString("str_permission_class_id")),
                      CommonMethods.getUUID(rs.getString("str_class_id")),
                      CommonMethods.getUUID(rs.getString("str_field_id")),
                      CommonMethods.getUUID(rs.getString("str_relation_field_id")),
                      CommonMethods.getUUID(rs.getString("str_record_field_id")),
                      CommonMethods.getUUID(rs.getString("str_record_field_value")),
                      rs.getShort("str_type_use"), (short) 0),

                  (rs.getString("val_class_id") == null ? null
                      : new AccessLiteralModel(
                          CommonMethods.getUUID(rs.getString("strucutre_literal")), "noname",
                          CommonMethods.getUUID(rs.getString("company_id")),
                          CommonMethods.getUUID(rs.getString("val_permission_class_id")),
                          CommonMethods.getUUID(rs.getString("val_class_id")),
                          CommonMethods.getUUID(rs.getString("val_field_id")),
                          CommonMethods.getUUID(rs.getString("val_relation_field_id")),
                          CommonMethods.getUUID(rs.getString("val_record_field_id")),
                          CommonMethods.getUUID(rs.getString("val_record_field_value")),
                          rs.getShort("val_type_use"), (short) 0)))))
          .map(Collection::stream).orElseGet(Stream::empty)
          .collect(Collectors.toMap(x -> x.getId(), x -> x));
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
      return null;
    }
  }

}
