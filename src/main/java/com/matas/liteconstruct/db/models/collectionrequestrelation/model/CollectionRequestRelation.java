package com.matas.liteconstruct.db.models.collectionrequestrelation.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.collectionrequestrelation.abstractmodel.CollectionRequestRelationAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollectionRequestRelation implements CollectionRequestRelationAbstract {

  private String url;

  private UUID classId;

  private UUID dynamicRoleId;

  private UUID collectionId;
  
  private int editAccess;
}
