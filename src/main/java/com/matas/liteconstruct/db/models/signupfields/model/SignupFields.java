package com.matas.liteconstruct.db.models.signupfields.model;

import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.signupfields.abstractmodel.SignupFieldsAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupFields implements SignupFieldsAbstract {

  UUID classId;

  Map<String, UUID> fieldsRelations;

  @Override
  public UUID getFieldByKey(String key) {
    return fieldsRelations.get(key);
  }

  @Override
  public void setFieldPair(String key, UUID field) {
    fieldsRelations.put(key, field);
  }

}
