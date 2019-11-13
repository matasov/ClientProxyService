package com.matas.liteconstruct.db.models.collectiondynamicrole.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.collectioncase.abstractmodel.CollectionCaseAbstract;
import com.matas.liteconstruct.db.models.collectiondynamicrole.abstractmodel.CollectionDynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionDynamicRoleModel implements CollectionDynamicRoleModelAbstract {

  private UUID id;
  private UUID collectionId;
  private int active;
  private UUID classId;
  private DynamicRoleModelAbstract dynamicRole;
  private CollectionCaseAbstract collectionCase;

}
