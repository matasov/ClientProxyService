package com.matas.liteconstruct.service.dynamic.model;

import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.dynamicclass.abstractmodel.DynamicClassRecordLineAbstract;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import com.matas.liteconstruct.db.models.lngs.abstractmodel.LngCompanyAccess;
import com.matas.liteconstruct.db.models.recordowner.abstractmodel.RecordsOwnerClassSettingsAbstract;
import com.matas.liteconstruct.db.models.serviceauthorized.abstractmodel.AuthorizedContactAbstract;

public interface SessionMainParams {

  String getKeyCollectionCase();

  AuthorizedContactAbstract getAuthorizedContact();

  FastStructureModelAbstract getFastStructureForClass(UUID classId);

  FastStructureModelAbstract getSudoFastStructureForClass(UUID classId);

  Map<String, Object> getFastStructureMapForClass(UUID classId);

  Map<String, Object> getSudoFastStructureMapForClass(UUID classId);

  UUID getInstantDynamicRoleId();

  void addFastStructureForClass(UUID classId, FastStructureModelAbstract structure);

  void addFastStructureMapForClass(UUID classId, Map<String, Object> structureMap);

  DynamicRoleModelAbstract getSudoDynamicRole();

  Map<String, Object> getInstantAccessPermissions();

  UUID getContactCompanyClass();

  LngCompanyAccess getDefaultLng();

  void setDefaultLng(LngCompanyAccess lng);

  void setContactCompanyClass(UUID contactCompanyClass);

  long getDateEndingWork();

  void setDateEndingWork(long dateEnding);

  long getDateNextUpdate();

  void setDateNextUpdate(long dateNextUpdate);

  void updateStructure(FastStructureModelAbstract classStructure);

  void setSudoStructure(FastStructureModelAbstract sudoClassStructure);

  void setSudoStructureMap(UUID classId, Map<String, Object> sudoClassStrucutreMap);

  void setPermissionsByClass(UUID classId, Map<String, Object> accessPermissions);

  Map<String, Object> getAccessPermissionsByClass(UUID classId);
}
