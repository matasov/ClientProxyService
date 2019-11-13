package com.matas.liteconstruct.db.models.collectioncase.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.collectioncase.abstractmodel.CollectionCaseAbstract;

public interface CollectionCaseRepository {

  void addCollectionCase(CollectionCaseAbstract collectionCase);

  void removeCollectionCase(CollectionCaseAbstract collectionCase);

  void updateCollectionCase(CollectionCaseAbstract collectionCase);

  CollectionCaseAbstract getCollectionCaseByParams(String metaKey, UUID classId,
      UUID dynamicRoleId);

  List<CollectionCaseAbstract> getCollectionCasesByCaseAndDynamicRole(String metaKey,
      UUID dynamicRoleId);

  List<CollectionCaseAbstract> getCollectionCasesByCaseAndClassId(String metaKey, UUID classId);
}