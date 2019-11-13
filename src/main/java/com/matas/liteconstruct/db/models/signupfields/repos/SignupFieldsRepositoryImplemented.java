package com.matas.liteconstruct.db.models.signupfields.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.signupfields.abstractmodel.SignupFieldsAbstract;
import com.matas.liteconstruct.db.models.signupfields.model.SignupFields;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SignupFieldsRepositoryImplemented implements SignupFieldsRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addSignupFields(SignupFieldsAbstract signupFields) {
    Map<String, UUID> pairs = signupFields.getFieldsRelations();
    if (pairs != null) {
      pairs.entrySet().forEach(
          record -> addSignupPair(signupFields.getClassId(), record.getKey(), record.getValue()));
    }
  }

  @Override
  public void addSignupPair(UUID classId, String key, UUID fieldId) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s')",
        DBConstants.TBL_SIGNUP_FIELDS, classId, key, fieldId);

    jdbcTemplate.update(sql);
  }

  @Override
  public void updateSignupPair(UUID classId, String key, UUID fieldId) {
    String sql = String.format(
        "update %1$s set field_id = '%4$s' where class_id = '%2$s' and title_key = '%3$s'",
        DBConstants.TBL_SIGNUP_FIELDS, classId, key, fieldId);
    jdbcTemplate.update(sql);
  }

  @Override
  public UUID getFieldIdByParams(UUID classId, String key) {
    String sql = String.format(
        "SELECT field_id FROM \"%1$s\" WHERE class_id = '%2$s' and title_key = '%3$s'",
        DBConstants.TBL_SIGNUP_FIELDS, classId, key);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return (UUID) rows.get(0).get("contact_id");
    else
      return null;
  }

  @Override
  public void removeFieldByParams(UUID classId, String key) {
    String sql =
        String.format("DELETE FROM \"%1$s\" WHERE class_id = '%2$s' and title_key = '%3$s'",
            DBConstants.TBL_SIGNUP_FIELDS, classId, key);
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeFieldByclassId(UUID classId) {
    String sql = String.format("DELETE FROM \"%1$s\" WHERE class_id = '%2$s'",
        DBConstants.TBL_SIGNUP_FIELDS, classId);
    jdbcTemplate.update(sql);
  }

  @Override
  public SignupFieldsAbstract getSignupFieldsByClass(UUID classId) {
    String sql = String.format("select title_key, field_id from %1$s where class_id = '%2$s'",
        DBConstants.TBL_SIGNUP_FIELDS, classId);
    List<Map<String, Object>> fieldsMap = jdbcTemplate.queryForList(sql);

    if (fieldsMap == null || fieldsMap.isEmpty())
      return null;
    return new SignupFields(classId, fieldsMap.parallelStream()// .flatMap(record ->
                                                               // record.entrySet().parallelStream())
        .peek(record -> System.out.println(record.get("title_key") + ": " + record.get("field_id")))
        .collect(Collectors.toMap(record -> record.get("title_key").toString(),
            record -> (UUID) record.get("field_id"))));
  }

}
