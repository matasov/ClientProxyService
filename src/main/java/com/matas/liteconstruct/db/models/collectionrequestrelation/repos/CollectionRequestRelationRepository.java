package com.matas.liteconstruct.db.models.collectionrequestrelation.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.collectionrequestrelation.abstractmodel.CollectionRequestRelationAbstract;

public interface CollectionRequestRelationRepository {
  
  void addCollectionRequest(CollectionRequestRelationAbstract collectionRequest);

  void removeCollectionRequest(CollectionRequestRelationAbstract collectionRequest);

  void removeClassCollectionRequestsByCompanyId(UUID classId, UUID companyId);

  void updateCollectionRequest(CollectionRequestRelationAbstract collectionRequest);

  CollectionRequestRelationAbstract getCollectionRequestByKeys(String url, UUID classId,
      UUID dynamicRoleId);

  List<CollectionRequestRelationAbstract> getCollectionRequestForUrl(String url, UUID companyId,
      int editAccess);

  List<CollectionRequestRelationAbstract> getCollectionRequestForClass(String url, UUID classId,
      int editAccess);
}
