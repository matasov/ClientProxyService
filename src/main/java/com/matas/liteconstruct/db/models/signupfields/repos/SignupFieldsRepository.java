package com.matas.liteconstruct.db.models.signupfields.repos;

import java.util.UUID;
import com.matas.liteconstruct.db.models.signupfields.abstractmodel.SignupFieldsAbstract;

public interface SignupFieldsRepository {
  
  void addSignupFields(SignupFieldsAbstract signupFields);

  void addSignupPair(UUID classId, String key, UUID fieldId);

  void updateSignupPair(UUID classId, String key, UUID fieldId);

  UUID getFieldIdByParams(UUID classId, String key);

  void removeFieldByParams(UUID classId, String key);

  void removeFieldByclassId(UUID classId);

  SignupFieldsAbstract getSignupFieldsByClass(UUID classId);
}
