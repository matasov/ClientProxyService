package com.matas.liteconstruct.db.models.innerrecords.abstractmodel;

import java.util.UUID;

public interface BelongRecord {
  
  UUID getInnerClassId();
  
  UUID getImplementedId();
  
  UUID getParentClassId();
  
  UUID getFieldId();
}
