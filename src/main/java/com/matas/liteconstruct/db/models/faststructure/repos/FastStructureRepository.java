package com.matas.liteconstruct.db.models.faststructure.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.model.FastStructureModelManagement;

public interface FastStructureRepository {

  void addFastStructure(FastStructureModelAbstract fastStructure);

  void removeFastStructure(FastStructureModelAbstract fastStructure);

  void removeFastStructuresByclassId(UUID classId);

  void updateFastStructure(FastStructureModelAbstract fastStructure);

  List<FastStructureModelAbstract> getFastStructuresForClass(UUID classId);

  List<FastStructureModelAbstract> getFastStructuresForDynamicRoleAndCollection(UUID dynamicRoleId,
      UUID collectionId);

  FastStructureModelAbstract getActiveFastStructuresForClassByDynamicRole(UUID dynamicRoleId,
      UUID classId, String keyCollectionCase);

  List<FastStructureModelManagement> getAllFastStructuresForClassByCompany(UUID classId,
      UUID companyId);

  boolean checkActiveFastStructuresForClassByCompanyServiceRole(UUID classId, UUID companyId,
      UUID serviceId, UUID roleId);

  void setAllInActiveForClassByCompanyServiceRole(UUID classId, UUID companyId, UUID serviceId,
      UUID roleId);

  List<FastStructureModelAbstract> getAllWhichIncludesClassAndRole(UUID includeClassId,
      List<DynamicRoleModelAbstract> whiteList, List<DynamicRoleModelAbstract> blackList);

  List<FastStructureModelAbstract> getAllDependsStructures(UUID forClassId, UUID companyId,
      UUID serviceId, boolean isStructureUpdate, List<DynamicRoleModelAbstract> whiteList,
      List<DynamicRoleModelAbstract> blackList);
}
