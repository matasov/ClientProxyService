package com.matas.liteconstruct.db.models.systemdictionary.abstractmodel;

import java.util.UUID;

public interface SystemDictionaryItemAbstract {

  UUID getCompanyId();
  
  String getLang();

  String getMetaKey();

  String getValue();
}
