package com.matas.liteconstruct.service.dynamic.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import com.matas.liteconstruct.db.models.lngs.abstractmodel.LngCompanyAccess;
import com.matas.liteconstruct.db.models.recordowner.abstractmodel.RecordsOwnerClassSettingsAbstract;
import com.matas.liteconstruct.db.models.serviceauthorized.abstractmodel.AuthorizedContactAbstract;
import com.matas.liteconstruct.db.models.serviceauthorized.model.AuthorozedContact.MapKey;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionMainParamsStorage implements SessionMainParams {

  @Getter
  private String keyCollectionCase;
  @Getter
  private AuthorizedContactAbstract authorizedContact;
  @Setter
  @Getter
  private Map<String, Object> instantAccessPermissions;
  @Setter
  @Getter
  private UUID contactCompanyClass;
  private Map<UUID, FastStructureModelAbstract> fastStructures;
  private Map<UUID, FastStructureModelAbstract> sudoFastStructures;
  private Map<UUID, Map<String, Object>> mapFastStructures;
  private Map<UUID, Map<String, Object>> mapSudoFastStructures;
  private Map<UUID, Map<String, Object>> accessPermissionsByClass;
  private DynamicRoleModelAbstract sudoDRForOperations;

  @Setter
  @Getter
  private long dateEndingWork;

  @Setter
  @Getter
  private long dateNextUpdate;

  @Setter
  @Getter
  LngCompanyAccess defaultLng;

  public SessionMainParamsStorage(AuthorizedContactAbstract authorizedContact,
      DynamicRoleModelAbstract sudoDRForOperations, String keyCollectionCase) {
    this.authorizedContact = authorizedContact;
    this.contactCompanyClass = authorizedContact.getCompanyContactClassId();
    this.instantAccessPermissions =
        (Map<String, Object>) authorizedContact.getPermissions().get(MapKey.PERMISSIONS);
    this.fastStructures = new HashMap<>(1);
    this.mapFastStructures = new HashMap<>(1);
    this.mapSudoFastStructures = new HashMap<>(1);
    this.sudoDRForOperations = sudoDRForOperations;
    sudoFastStructures = new HashMap<>(1);
    accessPermissionsByClass = new HashMap<>(1);
    dateEndingWork = System.currentTimeMillis() + 20000;
    this.keyCollectionCase = keyCollectionCase;
  }

  @Override
  public FastStructureModelAbstract getFastStructureForClass(UUID classId) {
    return fastStructures.get(classId);
  }

  @Override
  public FastStructureModelAbstract getSudoFastStructureForClass(UUID classId) {
    return sudoFastStructures.get(classId);
  }

  @Override
  public Map<String, Object> getSudoFastStructureMapForClass(UUID classId) {
    return mapSudoFastStructures.get(classId);
  }

  @Override
  public Map<String, Object> getFastStructureMapForClass(UUID classId) {
    return mapFastStructures.get(classId);
  }

  @Override
  public void addFastStructureForClass(UUID classId, FastStructureModelAbstract structure) {
    fastStructures.put(classId, structure);
  }

  @Override
  public void addFastStructureMapForClass(UUID classId, Map<String, Object> structureMap) {
    mapFastStructures.put(classId, structureMap);
  }

  @Override
  public UUID getInstantDynamicRoleId() {
    return UUID.fromString((String) instantAccessPermissions
        .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID)));
  }

  @Override
  public DynamicRoleModelAbstract getSudoDynamicRole() {
    return sudoDRForOperations;
  }

  @Override
  public void updateStructure(FastStructureModelAbstract classStructure) {
    fastStructures.put(classStructure.getClassId(), classStructure);
  }

  @Override
  public void setSudoStructure(FastStructureModelAbstract sudoStructure) {
    sudoFastStructures.put(sudoStructure.getClassId(), sudoStructure);
  }

  @Override
  public void setPermissionsByClass(UUID classId, Map<String, Object> accessPermissions) {
    accessPermissionsByClass.put(classId, accessPermissions);
  }

  @Override
  public Map<String, Object> getAccessPermissionsByClass(UUID classId) {
    return accessPermissionsByClass.get(classId);
  }

  @Override
  public void setSudoStructureMap(UUID classId, Map<String, Object> sudoClassStrucutreMap) {
    mapSudoFastStructures.put(classId, sudoClassStrucutreMap);
  }

}
