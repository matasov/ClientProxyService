package com.matas.liteconstruct.db.models.recordowner.abstractmodel;

import java.util.UUID;

public interface RecordsOwnerClassSettingsAbstract {
  
  UUID getId();

  UUID getClassId();

  short getEditAccess();

  int getPriority();

  short getTypeRecordAccess();

  UUID getOwnerFieldId();
}
