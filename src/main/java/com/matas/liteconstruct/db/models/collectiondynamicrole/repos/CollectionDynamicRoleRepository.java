package com.matas.liteconstruct.db.models.collectiondynamicrole.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.collectiondynamicrole.abstractmodel.CollectionDynamicRoleModelAbstract;

public interface CollectionDynamicRoleRepository {
  void addCollectionDynamicRole(CollectionDynamicRoleModelAbstract dynamicRole);

  void removeCollectionDynamicRole(CollectionDynamicRoleModelAbstract dynamicRole);

  void removeCollectionDynamicRolesByclassId(UUID classId);

  void updateCollectionDynamicRole(CollectionDynamicRoleModelAbstract dynamicRole);
  
  List<CollectionDynamicRoleModelAbstract> getCollectionDynamicRoleByclassId(
      UUID classId);

  List<CollectionDynamicRoleModelAbstract> getCollectionDynamicRoleByDroleClass(UUID dynamicRoleId,
      UUID classId, int active);
  
  CollectionDynamicRoleModelAbstract getCollectionDynamicRoleByCollectionId(UUID collectionId);

  CollectionDynamicRoleModelAbstract getCollectionDynamicRoleByCompanyServiceRole(
      UUID classId, UUID company, UUID service, UUID role, int isActive);
}
