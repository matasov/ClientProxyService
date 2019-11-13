package com.matas.liteconstruct.db.models.dynamicclass.abstractmodel;

import java.util.Map;
import java.util.UUID;

public interface DynamicClassRecordLineAbstract {

  UUID getRootclassId();
  
  UUID getRecordId();
  
  UUID getOwnerFieldId();
  
  UUID getOwnerRecordId();
  
  UUID getDateCreateFieldId();
  
  UUID getDateChangeFieldId();
  
  long getTimeStamp();
  
  String getTblClassDataUse();

  UUID getIdentificatorFieldId();

  Map<String, Object> getFieldValues();
  
}