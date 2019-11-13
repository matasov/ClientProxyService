package com.matas.liteconstruct.service.dynamic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactory;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactoryImplemented;
import com.matas.liteconstruct.db.models.dynamicclass.abstractmodel.DynamicClassRecordLineAbstract;
import com.matas.liteconstruct.db.models.dynamicclass.model.DynamicClassRecordLine;
import com.matas.liteconstruct.db.models.dynamicclass.queryfactory.DynamicClassesQueryFactory;
import com.matas.liteconstruct.db.models.dynamicclass.repos.DynamicClassesRepository;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepository;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepository;
import com.matas.liteconstruct.db.models.lngs.abstractmodel.LngCompanyAccess;
import com.matas.liteconstruct.db.models.recordowner.abstractmodel.RecordsOwnerClassSettingsAbstract;
import com.matas.liteconstruct.db.models.recordowner.repos.RecordsOwnerClassSettingsRepository;
import com.matas.liteconstruct.db.models.security.abstractmodel.SystemRoles;
import com.matas.liteconstruct.db.models.serviceauthorized.abstractmodel.AuthorizedContactAbstract;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import com.matas.liteconstruct.db.tools.StructrueCollectionEnum;
import com.matas.liteconstruct.service.common.LngDataService;
import com.matas.liteconstruct.service.dynamic.model.SessionMainParams;
import com.matas.liteconstruct.service.dynamic.model.SessionMainParamsStorage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CacheMainParams {

  private FastStructureRepository fastStructureRepository;

  @Autowired
  public void setFastStructureRepositoryImplemented(
      FastStructureRepository fastStructureRepository) {
    this.fastStructureRepository = fastStructureRepository;
  }

  private ObjectMapper objectMapper;

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private DynamicRoleRepository dynamicRoleRepository;

  @Autowired
  public void setDynamicRoleRepositoryImplemented(DynamicRoleRepository dynamicRoleRepository) {
    this.dynamicRoleRepository = dynamicRoleRepository;
  }

  private AccessRuleQueryFactory accessRuleQueryFactoryImplemented;

  @Autowired
  void setAccessRuleQueryFactoryImplemented(
      AccessRuleQueryFactoryImplemented accessRuleQueryFactoryImplemented) {
    this.accessRuleQueryFactoryImplemented = accessRuleQueryFactoryImplemented;
  }

  private DynamicClassesQueryFactory dynamicClassesQueryFactory;

  @Autowired
  public void setDynamicClassesQueryFactory(DynamicClassesQueryFactory dynamicClassesQueryFactory) {
    this.dynamicClassesQueryFactory = dynamicClassesQueryFactory;
  }

  private RecordsOwnerClassSettingsRepository recordsOwnerClassSettingsRepositoryImplemented;

  @Autowired
  public void setRecordsOwnerClassSettingsRepositoryImplemented(
      RecordsOwnerClassSettingsRepository recordsOwnerClassSettingsRepositoryImplemented) {
    this.recordsOwnerClassSettingsRepositoryImplemented =
        recordsOwnerClassSettingsRepositoryImplemented;
  }

  private DynamicClassesRepository dynamicClassesRepositoryImplemented;

  @Autowired
  public void setDynamicClassesRepositoryImplemented(
      DynamicClassesRepository dynamicClassesRepositoryImplemented) {
    this.dynamicClassesRepositoryImplemented = dynamicClassesRepositoryImplemented;
  }

  private LngDataService lngDataService;

  @Autowired
  public void setLngDataService(LngDataService lngDataService) {
    this.lngDataService = lngDataService;
  }

  public static final UUID SUDO_CONTACT_ID =
      UUID.fromString("7d82bde3-7740-41d7-9610-8d1fc75db803");

  public static final UUID CONTACT_VALUES_CLASS =
      UUID.fromString("7052a1e5-8d00-43fd-8f57-f2e4de0c8b24");

  public final UUID LOGIN_FIELD = UUID.fromString("81613045-4bb7-4576-9752-12dc08689b7d");

  Map<UUID, SessionMainParams> userCacheParams;

  private volatile boolean isInited = false;

  private boolean isServerStoped = false;

  private Map<UUID, RecordsOwnerClassSettingsAbstract> ownerSettingsForClasses;

  public void initService() {
    if (!isInited) {
      isInited = true;
      ownerSettingsForClasses = new HashMap<>(3, 1);
      userCacheParams = new HashMap<>(5, 5);
      final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
      executorService.scheduleAtFixedRate(() -> {
        if (isServerStoped) {
          executorService.shutdown();
        }
        userCacheParams.entrySet().removeIf(
            element -> element.getValue().getDateEndingWork() < System.currentTimeMillis());
      }, 0, 5000, TimeUnit.MILLISECONDS);
    } else {
      log.info("queue executor is inited yet.");
    }
  }

  public String initContact(AuthorizedContactAbstract authorizedContact, String keyCollectionCase,
      String lngToken) {
    SessionMainParamsStorage present = new SessionMainParamsStorage(authorizedContact,
        getSUDODynamicRole(authorizedContact.getCompanyId(), authorizedContact.getServiceId()),
        keyCollectionCase);
    present.setDefaultLng(lngDataService.getLngByToken(authorizedContact.getCompanyId(), lngToken));
    userCacheParams.put(authorizedContact.getContactId(), present);
    return null;
  }

  public String deleteUser(UUID contactSystemId) {
    try {
      if (userCacheParams.containsKey(contactSystemId))
        userCacheParams.remove(contactSystemId);
      else
        return "Can't delete user. not found";
    } catch (NullPointerException nex) {
      return "Can't delete user. not found";
    }
    return "User deleted successfully";
  }

  public SessionMainParams getSystemUserCache(UUID contactSystemId) {
    return userCacheParams.get(contactSystemId);
  }

  public LngCompanyAccess getDefaultLng(UUID contactSystemId) {
    if (!userCacheParams.containsKey(contactSystemId))
      throw new NullPointerException("Not found user.");
    return userCacheParams.get(contactSystemId).getDefaultLng();
  }

  public DynamicRoleModelAbstract getSUDODynamicRole(UUID companyId, UUID serviceId) {
    return dynamicRoleRepository.getDynamicRoleByCompanyServiceRole(companyId, serviceId,
        SystemRoles.SUPERADMIN_ROLE.getUUID());
  }

  public DynamicRoleModelAbstract getSUDODynamicRole(UUID contactSystemId) {
    if (!userCacheParams.containsKey(contactSystemId))
      throw new NullPointerException("Not found user.");
    return getSUDODynamicRole(
        userCacheParams.get(contactSystemId).getAuthorizedContact().getCompanyId(),
        userCacheParams.get(contactSystemId).getAuthorizedContact().getServiceId());
  }

  private FastStructureModelAbstract getRealFastStructureMapForClass(UUID currentclassId,
      UUID dynamicRole, String keyCollectionCase) {
    return fastStructureRepository.getActiveFastStructuresForClassByDynamicRole(dynamicRole,
        currentclassId, keyCollectionCase);
  }

  public Map<String, Object> getSUDOPermissions(UUID contactSystemId) {
    if (!userCacheParams.containsKey(contactSystemId))
      throw new NullPointerException("Not found user.");
    SessionMainParams cache = userCacheParams.get(contactSystemId);
    cache.getSudoDynamicRole();
    Map<String, Object> actualPermissions = new HashMap<>(5);
    // super user
    actualPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID),
        "00000000-0000-0000-0000-000000000000");
    actualPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID),
        cache.getSudoDynamicRole().getCompanyId().toString());
    actualPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.SERVICE_ID),
        cache.getSudoDynamicRole().getServiceId().toString());
    actualPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID),
        SystemRoles.SUPERADMIN_ROLE.getId());
    actualPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID),
        cache.getSudoDynamicRole().getId().toString());
    return actualPermissions;
  }

  public FastStructureModelAbstract getFastStructureCurrentClassForContact(UUID contactSystemId,
      UUID currentclassId) throws NullPointerException {
    if (!userCacheParams.containsKey(contactSystemId))
      throw new NullPointerException("Not found user.");
    SessionMainParams cache = userCacheParams.get(contactSystemId);
    FastStructureModelAbstract fastStructure = cache.getFastStructureForClass(currentclassId);
    if (fastStructure == null) {
      log.info("Not found fast structure. Start search process");
      updateCacheFastStructure(contactSystemId, currentclassId);
    }
    return cache.getFastStructureForClass(currentclassId);
  }

  public Map<String, Object> getFastStructureMapCurrentClassForContact(UUID contactSystemId,
      UUID currentClassId) throws NullPointerException {
    if (!userCacheParams.containsKey(contactSystemId))
      throw new NullPointerException("Not found user.");
    SessionMainParams cache = userCacheParams.get(contactSystemId);
    Map<String, Object> fastStructureMap = cache.getFastStructureMapForClass(currentClassId);
    if (fastStructureMap == null) {
      log.info("Not found fast structure. Start search process");
      updateCacheFastStructure(contactSystemId, currentClassId);
    }
    return cache.getFastStructureMapForClass(currentClassId);
  }

  private void updateCacheFastStructure(UUID contactSystemId, UUID currentclassId)
      throws NullPointerException {
    if (!userCacheParams.containsKey(contactSystemId))
      throw new NullPointerException("Not found user.");
    SessionMainParams cache = userCacheParams.get(contactSystemId);
    Map<String, Object> accessPermissions =
        getAccessPermissionsByClass(contactSystemId, currentclassId);
    if (accessPermissions == null)
      throw new NullPointerException("Not found access permissions.");
    FastStructureModelAbstract fastStructure = getRealFastStructureMapForClass(currentclassId,
        UUID.fromString((String) accessPermissions
            .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID))),
        cache.getKeyCollectionCase());
    Map<String, Object> fastStructureMap = null;
    if (fastStructure == null)
      throw new NullPointerException("Not found company structure.");
    else {
      try {
        fastStructureMap = objectMapper.readValue(fastStructure.getFastStructureJSON(),
            new TypeReference<Map<String, Object>>() {});
      } catch (IOException e) {
        throw new NullPointerException("Can't parse company structure.");
      }
    }
    cache.addFastStructureForClass(currentclassId, fastStructure);
    cache.addFastStructureMapForClass(currentclassId, fastStructureMap);
    userCacheParams.put(contactSystemId, cache);
  }

  public DynamicClassRecordLineAbstract getSelfRecordFromContactCompanyClass(UUID contactSystemId)
      throws NullPointerException {
    if (!userCacheParams.containsKey(contactSystemId))
      throw new NullPointerException("Not found user.");
    UUID contactCompanyclassId = userCacheParams.get(contactSystemId).getContactCompanyClass();
    List<Map<String, Object>> dynamicFilters = null;
    final Map<String, Object> sortingObject = new HashMap() {
      {
        put("map", "null.null." + FactoryGroupAbstract.getFirstPartOfUUID(contactCompanyclassId)
            + ".a09c3abf");
        put("direct", "1");
      }
    };
    Map<String, Object> sudoMapStructure;
    try {
      sudoMapStructure = getSUDOFastStructureMapForObject(contactSystemId, contactCompanyclassId);
      dynamicFilters = objectMapper.readValue("[{\"null.null." + contactCompanyclassId + "."
          + ((Map<String, Object>) sudoMapStructure.get("1")).get("id") + "\" : {\"value\": \""
          + userCacheParams.get(contactSystemId).getAuthorizedContact().getOwnerId()
          + "\", \"operator\":\"0\"}}]", new TypeReference<List<Map<String, Object>>>() {});

    } catch (Exception ex) {
      sudoMapStructure = null;
      throw new NullPointerException("exception in structure.");
    }
    List<Map<String, Object>> rows =
        getRecordsBySudo(contactSystemId, contactCompanyclassId, dynamicFilters, sortingObject,
            null, userCacheParams.get(contactSystemId).getDefaultLng().getLngId());
    if (rows == null || rows.isEmpty())
      return null;
    // return new DynamicClassRecordLine(contactCompanyclassId, result.get(0).get("0"),
    // ((Map<String, Object>) sudoMapStructure.get("1")).get("id"), );
    return new DynamicClassRecordLine(contactCompanyclassId,
        (UUID) rows.get(0).get(((Map<String, Object>) sudoMapStructure.get("0")).get("id")),
        UUID.fromString((String) ((Map<String, Object>) sudoMapStructure.get("1")).get("id")),
        (UUID) rows.get(0).get(((Map<String, Object>) sudoMapStructure.get("1")).get("id")),
        UUID.fromString((String) ((Map<String, Object>) sudoMapStructure.get("2")).get("id")),
        UUID.fromString((String) ((Map<String, Object>) sudoMapStructure.get("3")).get("id")),
        rows.get(0)
            .get(((Map<String, Object>) sudoMapStructure.get("3")).get("id")) instanceof Double
                ? Math.round((Double) rows.get(0)
                    .get(((Map<String, Object>) sudoMapStructure.get("3")).get("id")))
                : (Long) rows.get(0)
                    .get(((Map<String, Object>) sudoMapStructure.get("3")).get("id")),
        UUID.fromString((String) ((Map<String, Object>) sudoMapStructure.get("0")).get("id")),
        rows.get(0));
  }

  public List<Map<String, Object>> getRecordsBySudo(UUID contactSystemId, UUID currentclassId,
      List<Map<String, Object>> dynamicFilters, Map<String, Object> sortingObject,
      List<Integer> limits, UUID lng) throws NullPointerException {
    if (!userCacheParams.containsKey(contactSystemId))
      throw new NullPointerException("Not found user.");
    Map<String, Object> sudoMapStructure;
    try {
      sudoMapStructure = getSUDOFastStructureMapForObject(contactSystemId, currentclassId);
    } catch (Exception ex) {
      throw new NullPointerException("exception in structure.");
    }
    if (sudoMapStructure == null) {
      throw new NullPointerException("exception in structure.");
    }
    return getRecordsByExternalMapStructure(contactSystemId, currentclassId, sudoMapStructure,
        dynamicFilters, sortingObject, limits, lng);
  }

  public List<Map<String, Object>> getRecordsByLocal(UUID contactSystemId, UUID currentclassId,
      List<Map<String, Object>> dynamicFilters, Map<String, Object> sortingObject,
      List<Integer> limits, UUID lng) throws NullPointerException {
    if (!userCacheParams.containsKey(contactSystemId))
      throw new NullPointerException("Not found user.");
    Map<String, Object> mapStructure;
    try {
      mapStructure =
          this.getFastStructureMapCurrentClassForContact(contactSystemId, currentclassId);
    } catch (Exception ex) {
      throw new NullPointerException("exception in structure.");
    }
    if (mapStructure == null) {
      throw new NullPointerException("exception in structure.");
    }
    return getRecordsByExternalMapStructure(contactSystemId, currentclassId, mapStructure,
        dynamicFilters, sortingObject, limits, lng);
  }

  public List<Map<String, Object>> getRecordsByExternalMapStructure(UUID contactSystemId,
      UUID currentclassId, Map<String, Object> mapStructure,
      List<Map<String, Object>> dynamicFilters, Map<String, Object> sortingObject,
      List<Integer> limits, UUID lng) throws NullPointerException {
    if (!userCacheParams.containsKey(contactSystemId))
      throw new NullPointerException("Not found user.");
    String result = dynamicClassesQueryFactory.getLineSubquery(currentclassId,
        this.getSUDOPermissions(contactSystemId), mapStructure, null, dynamicFilters,
        this.getOwnerSettingsByClass(contactSystemId, currentclassId), new ArrayList() {
          {
            add(sortingObject);
          }
        }, limits, lng);
    log.info("query string: {}", result);
    List<String> queryResult =
        dynamicClassesRepositoryImplemented.getDynamicSpaceShapeClassBySubquery(result);
    if (queryResult == null)
      return null;
    return queryResult.stream().map(record -> mapQueryResult(record, mapStructure))
        .collect(Collectors.toList());
  }

  private Map<String, Object> mapQueryResult(String line, Map<String, Object> structure) {
    String cheatStr = line.replace("\\\"", "\"").replace("\\\\\"", "\\\"").replace("\"{\"", "{\"")
        .replace("}\",", "},").replace("}\"]", "}]").replace("\":\"{", "\":{")
        .replace("\\\":\\\"", "\":\"").replace("\\\",\\\"", "\",\"").replace("{\\\"", "{\"")
        .replace("\\\"},", "\"},");

    Map<String, Object> result;
    try {
      result = objectMapper.readValue(cheatStr, new TypeReference<Map<String, Object>>() {});
    } catch (IOException e) {
      result = null;
    }
    return result;
  }

  public Map<String, Object> getSUDOFastStructureMapForObject(UUID contactSystemId,
      UUID currentclassId) {
    if (!userCacheParams.containsKey(contactSystemId))
      throw new NullPointerException("Not found user.");
    Map<String, Object> fastStructureMap =
        userCacheParams.get(contactSystemId).getSudoFastStructureMapForClass(currentclassId);
    FastStructureModelAbstract fastStructure;
    if (fastStructureMap != null) {
      return fastStructureMap;
    } else {
      fastStructure = fastStructureRepository.getActiveFastStructuresForClassByDynamicRole(
          userCacheParams.get(contactSystemId).getSudoDynamicRole().getId(), currentclassId,
          userCacheParams.get(contactSystemId).getKeyCollectionCase());
      if (fastStructure == null)
        return null;
      userCacheParams.get(contactSystemId).setSudoStructure(fastStructure);
    }
    try {
      Map<String, Object> sudoStructureMap = objectMapper.readValue(
          fastStructure.getFastStructureJSON(), new TypeReference<Map<String, Object>>() {});
      if (sudoStructureMap != null) {
        userCacheParams.get(contactSystemId).setSudoStructureMap(currentclassId, sudoStructureMap);
      }
      return sudoStructureMap;
    } catch (IOException e) {
      log.error("error in parse!");
      return null;
    }
  }

  public void processUpdateClass(UUID classId) {
    userCacheParams.entrySet().forEach(userCacheParams -> {
      if (userCacheParams.getValue().getSudoFastStructureForClass(classId) != null)
        userCacheParams.getValue()
            .setSudoStructure(fastStructureRepository.getActiveFastStructuresForClassByDynamicRole(
                userCacheParams.getValue().getSudoDynamicRole().getId(), classId,
                userCacheParams.getValue().getKeyCollectionCase()));
    });
  }

  public Map<String, Object> getAccessPermissionsByClass(UUID contactSystemId, UUID classId)
      throws NullPointerException {
    if (!userCacheParams.containsKey(contactSystemId))
      throw new NullPointerException("Not found user.");
    Map<String, Object> accessPermissions =
        userCacheParams.get(contactSystemId).getAccessPermissionsByClass(classId);
    if (accessPermissions == null) {
      accessPermissions = accessRuleQueryFactoryImplemented.getResultPermissionForAccess(classId,
          userCacheParams.get(contactSystemId).getInstantAccessPermissions());
      if (accessPermissions == null)
        throw new NullPointerException("Not found access permissions for class: " + classId);
      userCacheParams.get(contactSystemId).setPermissionsByClass(classId, accessPermissions);
    }
    return accessPermissions;
  }



  public RecordsOwnerClassSettingsAbstract getOwnerSettingsByClass(UUID contactSystemId,
      UUID classId) throws NullPointerException {
    RecordsOwnerClassSettingsAbstract ownerSettings = ownerSettingsForClasses.get(classId);
    if (ownerSettings == null) {
      ownerSettings = updateOwnerSettingsByClass(contactSystemId, classId);
      if (ownerSettings == null)
        throw new NullPointerException("Not found access owner settings for class: " + classId);
      ownerSettingsForClasses.put(classId, ownerSettings);
    }
    return ownerSettings;
  }

  public RecordsOwnerClassSettingsAbstract updateOwnerSettingsByClass(UUID contactSystemId,
      UUID classId) throws NullPointerException {
    Map<String, Object> mapStructure =
        this.getSUDOFastStructureMapForObject(contactSystemId, classId);
    RecordsOwnerClassSettingsAbstract ownerSettings =
        recordsOwnerClassSettingsRepositoryImplemented.getRecordsOwnerClassSettingsByclassId(
            classId, UUID.fromString((String) ((Map<String, Object>) mapStructure.get("1"))
                .get(StructrueCollectionEnum.ID.toString())));
    if (ownerSettings == null)
      throw new NullPointerException("Not found access owner settings for class: " + classId);
    ownerSettingsForClasses.put(classId, ownerSettings);
    return ownerSettings;
  }

  public boolean stopDaemon() {
    isServerStoped = true;
    return isServerStoped;
  }

  public int getIndexFieldInStructure(UUID fieldId, Map<String, Object> structureMap) {
    int index = 0;
    for (Entry<String, Object> entry : structureMap.entrySet()) {
      Object value = ((Map<String, Object>) entry.getValue()).get("id");
      if (value instanceof String && fieldId.toString().equals(value)
          || value instanceof UUID && fieldId.equals(value))
        return index;
      index++;
    }
    return -1;
  }

  public UUID getDepthIndexFieldInStructure(UUID classId, UUID fieldId,
      Map<String, Object> structureMap) {
    UUID resultClassId = classId;
    for (Entry<String, Object> entry : structureMap.entrySet()) {
      Object value = ((Map<String, Object>) entry.getValue()).get("id");
      if (value instanceof String && fieldId.toString().equals(value)
          || value instanceof UUID && fieldId.equals(value))
        return classId;
      if (((Map<String, Object>) entry.getValue())
          .containsValue(StructrueCollectionEnum.NESTED.toString())) {
        UUID innerClassId = getDepthIndexFieldInStructure(
            UUID.fromString((String) ((Map<String, Object>) entry.getValue())
                .get(StructrueCollectionEnum.OBJECT.toString())),
            fieldId, (Map<String, Object>) ((Map<String, Object>) entry.getValue())
                .get(StructrueCollectionEnum.NESTED.toString()));
        if (innerClassId != null)
          return innerClassId;
      }
    }
    return null;
  }

  public FastStructureModelAbstract getStructureCollectionByFieldId(UUID classId,
      UUID dynamicRoleId, UUID fieldId, FastStructureModelAbstract presentStructure,
      Map<String, Object> structureMap, String keyCollectionCase) throws NullPointerException {
    UUID innerClassId = getDepthIndexFieldInStructure(classId, fieldId, structureMap);
    if (innerClassId == null)
      throw new NullPointerException("not found field in structure.");
    if (innerClassId.equals(classId))
      return presentStructure;
    else
      return this.fastStructureRepository.getActiveFastStructuresForClassByDynamicRole(
          dynamicRoleId, innerClassId, keyCollectionCase);
  }

  public Map<String, Object> getFieldDescriptionByFieldIdRecursively(UUID systemContactId,
      UUID classId, UUID fieldId) {
    Map<String, Object> fastStructureMap =
        getFastStructureMapCurrentClassForContact(systemContactId, classId);
    return getFieldDescriptionByFieldIdRecursively(fieldId, fastStructureMap);
  }

  private Map<String, Object> getFieldDescriptionByFieldIdRecursively(UUID fieldId,
      Map<String, Object> fastStructureMap) {
    for (Map.Entry<String, Object> entry : fastStructureMap.entrySet()) {
      if (((Map<String, Object>) entry.getValue()).get(StructrueCollectionEnum.ID.toString())
          .equals(fieldId.toString())) {
        return (Map<String, Object>) entry.getValue();
      }
      if (((Map<String, Object>) entry.getValue()).get(StructrueCollectionEnum.INNER.toString())
          .equals("3")
          && ((Map<String, Object>) entry.getValue())
              .containsKey(StructrueCollectionEnum.NESTED.toString())) {
        Map<String, Object> result = getFieldDescriptionByFieldIdRecursively(fieldId,
            (Map<String, Object>) ((Map<String, Object>) entry.getValue())
                .get(StructrueCollectionEnum.NESTED.toString()));
        if (result != null)
          return result;
      }
    }
    return null;
  }

  public String getIndexOfTheFieldId(UUID fieldId, Map<String, Object> fastStructureMap) {
    for (Map.Entry<String, Object> entry : fastStructureMap.entrySet()) {
      if (((Map<String, Object>) entry.getValue()).get(StructrueCollectionEnum.ID.toString())
          .equals(fieldId.toString())) {
        return (String) entry.getKey();
      }
      if (((Map<String, Object>) entry.getValue()).get(StructrueCollectionEnum.INNER.toString())
          .equals("3")
          && ((Map<String, Object>) entry.getValue())
              .containsKey(StructrueCollectionEnum.NESTED.toString())) {
        String result = getIndexOfTheFieldId(fieldId,
            (Map<String, Object>) ((Map<String, Object>) entry.getValue())
                .get(StructrueCollectionEnum.NESTED.toString()));
        if (result != null)
          return result;
      }
    }
    return null;
  }

}
