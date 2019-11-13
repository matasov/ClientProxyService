package com.matas.liteconstruct.db.models.collectioncase.abstractmodel;

import java.util.UUID;

public interface CollectionCaseAbstract {

  String getMetaKey();
  
  UUID getClassId();
  
  UUID getDynamicRoleId();
  
  UUID getCollectionId();
  
}
