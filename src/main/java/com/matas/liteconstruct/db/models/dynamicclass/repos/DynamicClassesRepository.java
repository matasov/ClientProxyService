package com.matas.liteconstruct.db.models.dynamicclass.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.dynamicclass.abstractmodel.DynamicClassRecordLineAbstract;

public interface DynamicClassesRepository {

  void addDynamicClassesRecord(DynamicClassRecordLineAbstract dynamicRecord, long timeStamp);

  void removeDynamicClassesRecord(DynamicClassRecordLineAbstract dynamicRecord);

  void updateDynamicClassesRecord(DynamicClassRecordLineAbstract dynamicRecord, long timeStamp);

  DynamicClassRecordLineAbstract getDynamicClassesRecordById(UUID classId, UUID dynamicRecord,
      Map<String, Object> fastStructure);

  boolean hasDynamicClassesRecordById(UUID classId, UUID dynamicRecord,
      Map<String, Object> fastStructure);

  DynamicClassRecordLineAbstract getDynamicClassesRecordByOverOwnerId(UUID classId,
      Map<String, Object> actualPermissions, Map<String, Object> fastStructure,
      Map<String, Object> conditions);

  DynamicClassRecordLineAbstract getAdminDynamicClassesRecordByOverOwnerId(UUID classId,
      Map<String, Object> fastStructure, Map<String, Object> conditions, UUID sortID,
      boolean increase);

  List<String> getDynamicSpaceShapeClassBySubquery(String sql);
  
  Map<String, Object> getDynamicGarbadge(String sql);
  
  List<Map<String, Object>> getDynamicGarbadgeList(String sql);
}
