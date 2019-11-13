package com.matas.liteconstruct.db.models.classmanagement.abstractmodel;

import java.util.Map;
import java.util.UUID;

public interface DynamicClassManagementSettingsAbstract {
  
  UUID getManagementId();
  
  String getName();
  
  UUID getClassId();
  
  UUID getDynamicRoleId();
  
  Map<String, String> getValues();
  
  String getValueByMetaKey(String key);
}
