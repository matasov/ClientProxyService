package com.matas.liteconstruct.db.models.faststructure.abstractmodel;

import java.util.UUID;
import com.matas.liteconstruct.db.models.collectioncase.abstractmodel.CollectionCaseAbstract;

public interface FastStructureModelAbstract {

  UUID getDynamicRoleId();

  UUID getCollectionId();

  UUID getClassId();

  String getFastStructureJSON();

  boolean isActive();

  CollectionCaseAbstract getCollectionCase();
  
}
