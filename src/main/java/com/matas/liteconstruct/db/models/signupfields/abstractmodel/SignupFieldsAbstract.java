package com.matas.liteconstruct.db.models.signupfields.abstractmodel;

import java.util.Map;
import java.util.UUID;

public interface SignupFieldsAbstract {

  UUID getClassId();
  
  UUID getFieldByKey(String key);
  
  void setFieldPair(String key, UUID field);
  
  Map<String, UUID> getFieldsRelations();
  
}
