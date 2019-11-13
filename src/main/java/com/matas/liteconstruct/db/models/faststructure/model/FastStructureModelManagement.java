package com.matas.liteconstruct.db.models.faststructure.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.collectioncase.abstractmodel.CollectionCaseAbstract;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FastStructureModelManagement implements FastStructureModelAbstract {
  private UUID dynamicRoleId;

  private UUID collectionId;

  private UUID classId;

  private String fastStructureJSON;

  private String companyName;

  private String serviceName;

  private UUID roleId;

  private String roleName;

  private boolean isActive;
  
  private CollectionCaseAbstract collectionCase;


}
