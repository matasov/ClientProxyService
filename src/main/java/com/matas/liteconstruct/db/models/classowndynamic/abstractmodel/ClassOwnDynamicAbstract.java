package com.matas.liteconstruct.db.models.classowndynamic.abstractmodel;

import java.util.UUID;

public interface ClassOwnDynamicAbstract {
  UUID getId();

  UUID getClassId();

  short getEditAccess();

  int getPriority();

  short getRecordAccess();
}
