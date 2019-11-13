package com.matas.liteconstruct.db.models.collectioncase.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.collectioncase.abstractmodel.CollectionCaseAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollectionCase implements CollectionCaseAbstract {
  
  public CollectionCase() {
    metaKey = null;
    classId = null;
    collectionId = null;
    dynamicRoleId = null;
  }

  String metaKey;

  UUID classId;

  UUID collectionId;

  UUID dynamicRoleId;
  
}
