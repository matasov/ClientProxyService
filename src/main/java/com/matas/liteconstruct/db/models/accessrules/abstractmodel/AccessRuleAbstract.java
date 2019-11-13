package com.matas.liteconstruct.db.models.accessrules.abstractmodel;

import java.util.UUID;

public interface AccessRuleAbstract {
  UUID getId();

  String getName();

  UUID getCompanyId();

  UUID getClassId();
  
  UUID getAccessFilterGroupId();

  short getEditAccess();

  int getPriority();

  short getLevelAccess();

  UUID getLevelValue();

}
