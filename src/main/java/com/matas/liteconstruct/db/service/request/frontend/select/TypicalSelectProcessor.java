package com.matas.liteconstruct.db.service.request.frontend.select;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactory;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactoryImplemented;
import com.matas.liteconstruct.db.models.collectiondynamicrole.abstractmodel.CollectionDynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.collectiondynamicrole.repos.CollectionDynamicRoleRepository;
import com.matas.liteconstruct.db.models.collectiondynamicrole.repos.CollectionDynamicRoleRepositoryImplemented;
import com.matas.liteconstruct.db.models.collections.abstractmodel.StructureCollectionTreeFactoryImplemented;
import com.matas.liteconstruct.db.models.dynamicclass.queryfactory.DynamicClassesQueryFactory;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepository;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepositoryImplemented;
import com.matas.liteconstruct.db.models.recordowner.repos.RecordsOwnerClassSettingsRepository;
import com.matas.liteconstruct.db.models.recordowner.repos.RecordsOwnerClassSettingsRepositoryImplemented;
import com.matas.liteconstruct.db.models.recordowner.repos.RecordsOwnerRepositoryImplemented;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import com.matas.liteconstruct.db.service.request.frontend.FrontendRequest;
import lombok.Data;

@Data
@Service
public class TypicalSelectProcessor implements SelectProcessor {

  private FrontendRequest adaptedIncomingData;

  private List<UUID> filtersGroups;

  private DynamicClassesQueryFactory dynamicClassesQueryFactory;

  private AccessRuleQueryFactory accessRuleQueryFactoryImplemented;

  private StructureCollectionTreeFactoryImplemented factoryImplemented;

  private CollectionDynamicRoleRepository collectionDynamicRoleRepository;

  private FastStructureRepository fastStructureRepository;

  RecordsOwnerClassSettingsRepository recordsOwnerClassSettingsRepositoryImplemented;

  @Autowired
  public void setDynamicClassesQueryFactory(DynamicClassesQueryFactory dynamicClassesQueryFactory) {
    this.dynamicClassesQueryFactory = dynamicClassesQueryFactory;
  }

  @Autowired
  public void setAccessRuleQueryFactoryImplemented(
      AccessRuleQueryFactory accessRuleQueryFactoryImplemented) {
    this.accessRuleQueryFactoryImplemented = accessRuleQueryFactoryImplemented;
  }

  @Autowired
  public void setStructureCollectionTreeFactoryImplemented(
      StructureCollectionTreeFactoryImplemented factoryImplemented) {
    this.factoryImplemented = factoryImplemented;
  }

  @Autowired
  public void setCollectionDynamicRoleRepositoryImplemented(
      CollectionDynamicRoleRepository collectionDynamicRoleRepository) {
    this.collectionDynamicRoleRepository = collectionDynamicRoleRepository;
  }

  @Autowired
  public void setFastStructureRepositoryImplemented(
      FastStructureRepository fastStructureRepository) {
    this.fastStructureRepository = fastStructureRepository;
  }

  @Autowired
  public void setRecordsOwnerClassSettingsRepositoryImplemented(
      RecordsOwnerClassSettingsRepository recordsOwnerClassSettingsRepositoryImplemented) {
    this.recordsOwnerClassSettingsRepositoryImplemented =
        recordsOwnerClassSettingsRepositoryImplemented;
  }

  private UUID getUUIDFromMap(Map<String, Object> block, String key) throws NullPointerException {
    return UUID.fromString((String) (block.get(key)));
  }

  public String getSelectQuery() throws NullPointerException {

    if (DBConstants.isPresentNeccessaryDataInSettingsMap(getAdaptedIncomingData().getSettings())) {

      List<CollectionDynamicRoleModelAbstract> collections =
          collectionDynamicRoleRepository.getCollectionDynamicRoleByDroleClass(
              getUUIDFromMap(getAdaptedIncomingData().getSettings(), DBConstants.DYNAMIC_ROLE_ID),
              getUUIDFromMap(getAdaptedIncomingData().getWork(), DBConstants.WORK_CLASS), 1);
      if (collections == null || collections.isEmpty())
        throw new NullPointerException("Not found active collection.");
      List<FastStructureModelAbstract> dbCacheStructures =
          fastStructureRepository.getFastStructuresForDynamicRoleAndCollection(
              getUUIDFromMap(getAdaptedIncomingData().getSettings(), DBConstants.DYNAMIC_ROLE_ID),
              UUID.fromString(collections.get(0).getId().toString()));
      if (dbCacheStructures == null || dbCacheStructures.isEmpty())
        throw new NullPointerException("Not found fast structure.");

//      try {
//        return dynamicClassesQueryFactory.getLineSubquery(
//            getUUIDFromMap(getAdaptedIncomingData().getWork(), DBConstants.WORK_CLASS),
//            getAdaptedIncomingData(),
//            new ObjectMapper().readValue(dbCacheStructures.get(0).getFastStructure(),
//                new TypeReference<Map<String, Object>>() {}),
//            accessRuleQueryFactoryImplemented.getListFiltersGroupForSettings(
//                getUUIDFromMap(getAdaptedIncomingData().getWork(), DBConstants.WORK_CLASS), getAdaptedIncomingData().getSettings()),
//            recordsOwnerClassSettingsRepositoryImplemented.getRecordsOwnerClassSettingsByclassId(
//                getUUIDFromMap(getAdaptedIncomingData().getWork(), DBConstants.WORK_CLASS)));
//      } catch (IOException e) {
//        e.printStackTrace();
//        return null;
//      }
    }
    return null;
  }

}
