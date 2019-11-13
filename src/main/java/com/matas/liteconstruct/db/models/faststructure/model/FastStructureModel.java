package com.matas.liteconstruct.db.models.faststructure.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.collectioncase.abstractmodel.CollectionCaseAbstract;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import lombok.Data;

@Data
public class FastStructureModel implements FastStructureModelAbstract {

  private UUID dynamicRoleId;

  private UUID collectionId;

  private UUID classId;

  private String fastStructureJSON;

  private CollectionCaseAbstract collectionCase;
  
  private boolean isActive;

  public FastStructureModel(UUID dynamicRoleId, UUID collectionId, UUID classId,
      String fastStructureJSON, boolean isActive, CollectionCaseAbstract collectionCase) {
    this.dynamicRoleId = dynamicRoleId;
    this.collectionId = collectionId;
    this.fastStructureJSON = fastStructureJSON;
    this.classId = classId;
    this.isActive = isActive;
    this.collectionCase = collectionCase;
  }



}
