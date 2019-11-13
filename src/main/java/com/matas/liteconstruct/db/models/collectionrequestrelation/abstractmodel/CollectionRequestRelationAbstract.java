package com.matas.liteconstruct.db.models.collectionrequestrelation.abstractmodel;

import java.util.UUID;

public interface CollectionRequestRelationAbstract {

  String getUrl();

  UUID getClassId();

  UUID getDynamicRoleId();

  UUID getCollectionId();
  
  int getEditAccess();
}
