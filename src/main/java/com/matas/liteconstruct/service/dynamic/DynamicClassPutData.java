package com.matas.liteconstruct.service.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.aspect.log.LogExecutionTime;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactory;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactoryImplemented;
import com.matas.liteconstruct.db.models.dynamicclass.abstractmodel.DynamicClassRecordLineAbstract;
import com.matas.liteconstruct.db.models.dynamicclass.model.DynamicClassRecordLine;
import com.matas.liteconstruct.db.models.dynamicclass.repos.DynamicClassesRepository;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepository;
import com.matas.liteconstruct.db.models.recordowner.abstractmodel.RecordsOwnerModelAbstract;
import com.matas.liteconstruct.db.models.recordowner.model.RecordsOwnerModel;
import com.matas.liteconstruct.db.models.recordowner.repos.RecordsOwnerRepository;
import com.matas.liteconstruct.db.models.serviceauthorized.model.AuthorozedContact.MapKey;
import com.matas.liteconstruct.db.models.serviceauthorized.repos.AuthorizedContactRepository;
import com.matas.liteconstruct.db.models.stafflog.abstractmodel.ClassLogAbstract;
import com.matas.liteconstruct.db.models.stafflog.model.ClassLog;
import com.matas.liteconstruct.db.models.stafflog.repos.ClassLogRepository;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import com.matas.liteconstruct.db.tools.StructrueCollectionEnum;
import com.matas.liteconstruct.db.tools.permissions.PermissionHandler;
import com.matas.liteconstruct.service.HttpReqRespUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DynamicClassPutData {

  private CacheMainParams cacheMainParams;

  @Autowired
  public void setCacheMainParams(CacheMainParams cacheMainParams) {
    this.cacheMainParams = cacheMainParams;
  }

  private AccessRuleQueryFactory accessRuleQueryFactoryImplemented;

  @Autowired
  void setAccessRuleQueryFactoryImplemented(
      AccessRuleQueryFactoryImplemented accessRuleQueryFactoryImplemented) {
    this.accessRuleQueryFactoryImplemented = accessRuleQueryFactoryImplemented;
  }

  private AuthorizedContactRepository authorizedContactRepository;

  @Autowired
  void setAuthorozedContactRepository(AuthorizedContactRepository authorozedContactRepository) {
    this.authorizedContactRepository = authorozedContactRepository;
  }

  private FastStructureRepository fastStructureRepository;

  @Autowired
  public void setFastStructureRepositoryImplemented(
      FastStructureRepository fastStructureRepository) {
    this.fastStructureRepository = fastStructureRepository;
  }

  private DynamicClassesRepository dynamicClassesRepository;

  @Autowired
  public void setDynamicClassesRepositoryImplemented(
      DynamicClassesRepository dynamicClassesRepository) {
    this.dynamicClassesRepository = dynamicClassesRepository;
  }

  private RecordsOwnerRepository recordsOwnerRepository;

  @Autowired
  public void setRecordsOwnerRepositoryImplemented(RecordsOwnerRepository recordsOwnerRepository) {
    this.recordsOwnerRepository = recordsOwnerRepository;
  }

  private ClassLogRepository classLogRepository;

  @Autowired
  public void setClassLogRepositoryImplemented(ClassLogRepository classLogRepository) {
    this.classLogRepository = classLogRepository;
  }

  private ObjectMapper objectMapper;

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private Map<String, Object> getAccessRulesPermissionsFilter(UUID classId) {
    if (classId == null) {
      return null;
    }
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetail = (UserDetails) auth.getPrincipal();
    if (userDetail != null && userDetail.getUsername() != null
        && userDetail.getUsername().length() > 2) {
      return accessRuleQueryFactoryImplemented.getResultPermissionForAccess(classId,
          (Map<String, Object>) authorizedContactRepository
              .getAuthorizedContactByName(userDetail.getUsername()).getPermissions()
              .get(MapKey.PERMISSIONS));
    } else {
      return null;
    }
  }

  private Map<String, Object> accessRulesByUserPermissionsFilter(UserDetails userDetail,
      UUID classId) {
    if (classId == null) {
      return null;
    }
    return accessRuleQueryFactoryImplemented.getResultPermissionForAccess(classId,
        (Map<String, Object>) authorizedContactRepository
            .getAuthorizedContactByName(userDetail.getUsername()).getPermissions()
            .get(MapKey.PERMISSIONS));
  }

  private Map<String, Object> getStructureForField(String fieldId,
      Map<String, Object> fieldStructure) {
    return (Map<String, Object>) fieldStructure.entrySet().parallelStream().map(x -> x.getValue())
        .filter(x -> ((Map<String, Object>) x).get("id").toString().equals(fieldId)).findAny()
        .orElse(null);
  }

  @LogExecutionTime
  public ResponseEntity<?> setDynamicClassValue(UUID contactSystemId, String ipAddress,
      Map<String, Object> request) {
    long time = System.currentTimeMillis();
    Map<String, List<String>> errors = new LinkedHashMap<>();
    Map<String, List<String>> success = new LinkedHashMap<>();
    try {
      log.info("try set value");
      if (request != null && !request.isEmpty() /* && request.containsKey("data") */) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) auth.getPrincipal();
        if (userDetail != null && userDetail.getUsername() != null
            && userDetail.getUsername().length() > 2) {
          // ((List<Object>) request.get("data")).forEach(partBody -> {
          // UUID currentClass =
          // UUID.fromString((String) ((Map<String, Object>) partBody).get("cclass"));
          UUID currentClass = UUID.fromString((String) request.get("cclass"));
          Map<String, Object> actualPermissions =
              accessRulesByUserPermissionsFilter(userDetail, currentClass);

          if (actualPermissions == null) {
            addToNotificationMap(errors, currentClass,
                "Not found actual permissions for class: " + currentClass + "");
          } else {
            List<Map<String, List<String>>> results = setValuesToDynamicTable(contactSystemId,
                currentClass, ipAddress, (Map<String, Object>) request);
            addAllToNotificationMap(success, currentClass, results.get(0));
            addAllToNotificationMap(errors, currentClass, results.get(1));
          }
          // });
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      return new ResponseEntity<>("error: " + ex.getLocalizedMessage(), HttpStatus.FORBIDDEN);
    }
    Map<String, Map> results = new HashMap<>(2);
    results.put("success", success);
    results.put("errors", errors);
    return new ResponseEntity<>(results, HttpStatus.OK);
  }

  public List<Map<String, List<String>>> setValuesToDynamicTable(UUID contactSystemId,
      UUID currentclassId, String ipAddress, Map<String, Object> objectData)
      throws org.postgresql.util.PSQLException {
    Map<String, List<String>> errors = new LinkedHashMap<>();
    Map<String, List<String>> success = new LinkedHashMap<>();
    Map<String, Object> actualPermissions =
        cacheMainParams.getSystemUserCache(contactSystemId).getInstantAccessPermissions();
    if (actualPermissions == null) {
      addToNotificationMap(errors, currentclassId,
          "Not found actual permissions for class: " + currentclassId + "");
    } else {
      FastStructureModelAbstract fastStructure =
          cacheMainParams.getFastStructureCurrentClassForContact(contactSystemId, currentclassId);
      // fastStructureRepository.getActiveFastStructuresForClassByDynamicRole(
      // UUID.fromString((String) actualPermissions
      // .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID))),
      // currentClass);

      if (fastStructure == null) {
        addToNotificationMap(errors, currentclassId,
            "Not found actual structure cache for class: " + currentclassId + "");
      } else {
        // try {
        final Map<String, Object> mapStructure = cacheMainParams
            .getFastStructureMapCurrentClassForContact(contactSystemId, currentclassId);
        // objectMapper.readValue(
        // fastStructure.getFastStructureJSON(), new TypeReference<Map<String, Object>>() {});
        if (mapStructure != null) {
          UUID currentDynamicUUID =
              cacheMainParams.getSystemUserCache(contactSystemId).getInstantDynamicRoleId();
          // UUID.fromString((String) actualPermissions
          // .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID)));
          ((List<Object>) ((Map<String, Object>) objectData).get("records")).forEach(record -> {


            List<Map<String, List<String>>> results = setItemToDynamicTable(contactSystemId,
                currentclassId, (Map<String, Object>) record);
            addAllToNotificationMap(success, currentclassId, results.get(0));
            addAllToNotificationMap(errors, currentclassId, results.get(1));
          });
        } else {
          addToNotificationMap(errors, currentclassId,
              "Not found object structure cache for class: " + currentclassId + "");
        }
        // } catch (IOException e) {
        // e.printStackTrace();
        // addToNotificationMap(errors, currentclassId,
        // "Error parsing structure from json for collection: " + fastStructure.getCollectionId()
        // + "");
        // }
      }
    }
    return new ArrayList() {
      {
        add(success);
        add(errors);
      }
    };
  }

  public List<Map<String, List<String>>> setItemToDynamicTable(UUID contactSystemId, UUID classId,
      Map<String, Object> record) {
    Map<String, List<String>> errors = new LinkedHashMap<>();
    Map<String, List<String>> success = new LinkedHashMap<>();
    Map<String, Object> fields = (Map<String, Object>) ((Map<String, Object>) record).get("fields");
    long timeStamp = System.currentTimeMillis();
    Map<String, Object> contactFastMapStructure =
        cacheMainParams.getFastStructureMapCurrentClassForContact(contactSystemId, classId);
    Map<String, Object> sudoFastStructureMap =
        cacheMainParams.getSUDOFastStructureMapForObject(contactSystemId, classId);
    Map<String, Object> actualPermissions =
        cacheMainParams.getAccessPermissionsByClass(contactSystemId, classId);
    // TODO only for migration uncomment!!!
    Object dataValue =
        fields.get((String) ((Map<String, Object>) sudoFastStructureMap.get("2")).get("id"));
    if (dataValue != null) {
      if (dataValue instanceof String)
        timeStamp = Long.parseLong((String) dataValue);
      else if (dataValue instanceof Integer)
        timeStamp = (Integer) dataValue;
      else
        timeStamp = (Long) dataValue;
    }
    // if (fields.containsKey((String) ((Map<String, Object>) mapStructure.get("3")).get("id"))) {
    // if (fields
    // .get((String) ((Map<String, Object>) mapStructure.get("3")).get("id")) instanceof String)
    // timeStamp = Long.parseLong(
    // (String) fields.get((String) ((Map<String, Object>) mapStructure.get("3")).get("id")));
    // else
    // timeStamp =
    // (Long) fields.get((String) ((Map<String, Object>) mapStructure.get("3")).get("id"));
    // }
    if (fields
        .containsKey((String) ((Map<String, Object>) sudoFastStructureMap.get("1")).get("id"))) {
      // do nothing
    } else {
      fields.put((String) ((Map<String, Object>) sudoFastStructureMap.get("1")).get("id"),
          cacheMainParams.getSystemUserCache(contactSystemId).getAuthorizedContact().getOwnerId()
              .toString());
    }

    // end
    if (!((Map<String, Object>) record).containsKey("id")
        || ((Map<String, Object>) record).get("id").equals("null")
        || (!((Map<String, Object>) record).get("id").equals("null")
            && !dynamicClassesRepository.hasDynamicClassesRecordById(classId,
                UUID.fromString((String) ((Map<String, Object>) record).get("id")),
                contactFastMapStructure))) {

      // insert algorithm
      if (!PermissionHandler.isInsert(
          Integer.parseInt((String) ((Map<String, Object>) contactFastMapStructure.get("0"))
              .get(StructrueCollectionEnum.PERM.toString())))) {
        addToNotificationMap(errors, classId,
            "You do not have permissions for insert in class: " + classId + "");
      } else {
        UUID newRecordUUID = UUID.randomUUID();
        if (fields.containsKey(
            ((Map<String, Object>) sudoFastStructureMap.get("0")).get("id").toString())) {
          newRecordUUID = UUID.fromString((String) fields
              .get((String) ((Map<String, Object>) sudoFastStructureMap.get("0")).get("id")));
        }
        List<Map<String, List<String>>> insertResults = insertNewRecord(classId, newRecordUUID,
            contactFastMapStructure, sudoFastStructureMap, fields, actualPermissions, timeStamp);
        updateDbStaffLog(classId, newRecordUUID, timeStamp, actualPermissions,
            (Map<String, Object>) record, sudoFastStructureMap);
        addAllToNotificationMap(success, classId, insertResults.get(0));
        addAllToNotificationMap(errors, classId, insertResults.get(1));
      }
    } else {
      // update algorithm
      UUID recordId = UUID.fromString((String) ((Map<String, Object>) record).get("id"));

      updateRecord(classId, recordId, contactFastMapStructure, sudoFastStructureMap, fields,
          actualPermissions, timeStamp);
      updateDbStaffLog(classId, recordId, timeStamp, actualPermissions,
          (Map<String, Object>) record, sudoFastStructureMap);
      addToNotificationMap(success, classId, String.format("update record: %1$s", recordId));
    }
    return new ArrayList() {
      {
        add(success);
        add(errors);
      }
    };
  }

  public List<Map<String, List<String>>> insertNewRecord(UUID classId, UUID newRecordUUID,
      Map<String, Object> contactFastMapStructure, Map<String, Object> sudoFastMapStructure,
      Map<String, Object> data, Map<String, Object> actualPermissions, long timeStamp) {
    Map<String, List<String>> errors = new LinkedHashMap<>();
    Map<String, List<String>> success = new LinkedHashMap<>();
    log.info("try insert new record with values: {}", data);
    Map<String, Object> checkData;
    try {
      data.entrySet().parallelStream().forEach(record -> {
        log.info("check work with field {}: {}", classId, record.getKey().getClass());
      });
      checkData = // new LinkedHashMap<>(data.size());
          data.entrySet().parallelStream()
              .peek(record -> log.info("work with field: {}", record.getKey().getClass()))
              .filter(item -> getStructureForField(item.getKey().toString(),
                  sudoFastMapStructure) != null
                  && item.getValue() != null
                  && ((Map<String, Object>) getStructureForField(item.getKey(),
                      contactFastMapStructure))
                          .get(StructrueCollectionEnum.NESTED.toString()) == null
                  && !item.getKey()
                      .equals(((Map<String, Object>) sudoFastMapStructure.get("3")).get("id"))
                  && (PermissionHandler
                      .isInsert(Integer.parseInt(
                          (String) ((Map<String, Object>) getStructureForField(item.getKey(),
                              contactFastMapStructure))
                                  .get(StructrueCollectionEnum.PERM.toString())))
                      || item.getKey()
                          .equals(data.get(((Map<String, Object>) sudoFastMapStructure.get("0"))
                              .get("id").toString()))
                      || item.getKey()
                          .equals(data.get(((Map<String, Object>) sudoFastMapStructure.get("1"))
                              .get("id").toString()))
                      || item.getKey()
                          .equals(data.get(((Map<String, Object>) sudoFastMapStructure.get("2"))
                              .get("id").toString()))
                  // || item.getKey().equals(
                  // (String) data.get(((Map<String, Object>)
                  // sudoFastMapStructure.get("3")).get("id")))
                  ))
              .collect(Collectors.toMap(item -> item.getKey().toString(),
                  item -> updateDataFieldStructureToJson(item.getValue(), item.getKey(),
                      sudoFastMapStructure)));
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
    log.info("checkData: {}", checkData);
    dynamicClassesRepository.addDynamicClassesRecord(new DynamicClassRecordLine(classId,
        newRecordUUID,
        UUID.fromString((String) ((Map<String, Object>) sudoFastMapStructure.get("1")).get("id")),
        (UUID) checkData.get(((Map<String, Object>) sudoFastMapStructure.get("1")).get("id")),
        UUID.fromString((String) ((Map<String, Object>) sudoFastMapStructure.get("2")).get("id")),
        UUID.fromString((String) ((Map<String, Object>) sudoFastMapStructure.get("3")).get("id")),
        timeStamp,
        UUID.fromString((String) ((Map<String, Object>) sudoFastMapStructure.get("0")).get("id")),
        checkData), timeStamp);
    addToNotificationMap(success, classId, String.format("insert new record: %1$s", newRecordUUID));
    return new ArrayList() {
      {
        add(success);
        add(errors);
      }
    };
  }

  public List<Map<String, List<String>>> updateRecord(UUID classId, UUID recordId,
      Map<String, Object> contactFastMapStructure, Map<String, Object> sudoFastMapStructure,
      Map<String, Object> data, Map<String, Object> actualPermissions, long timeStamp) {
    Map<String, List<String>> errors = new LinkedHashMap<>();
    Map<String, List<String>> success = new LinkedHashMap<>();
    List<String> successResult = new ArrayList<>(1);
    log.info("update data: {}", data);
    Map<String, Object> checkData = // new LinkedHashMap<>(data.size());
        data.entrySet().parallelStream().filter(item -> getStructureForField(item.getKey(),
            contactFastMapStructure) != null
            && item.getValue() != null
            && ((Map<String, Object>) getStructureForField(item.getKey(), contactFastMapStructure))
                .get(StructrueCollectionEnum.NESTED.toString()) == null
            && !item.getKey()
                .equals(((Map<String, Object>) sudoFastMapStructure.get("0")).get("id"))
            && !item.getKey()
                .equals(((Map<String, Object>) sudoFastMapStructure.get("3")).get("id"))
            && (PermissionHandler
                .isEdit(Integer
                    .parseInt((String) ((Map<String, Object>) getStructureForField(item.getKey(),
                        contactFastMapStructure)).get(StructrueCollectionEnum.PERM.toString())))
                || item.getKey()
                    .equals((String) data
                        .get(((Map<String, Object>) sudoFastMapStructure.get("0")).get("id")))
                || item.getKey()
                    .equals((String) data
                        .get(((Map<String, Object>) sudoFastMapStructure.get("1")).get("id")))
                || (data
                    .get(((Map<String, Object>) sudoFastMapStructure.get("2")).get("id")) != null
                    && item.getKey().equals(Long.toString(getLong(data
                        .get(((Map<String, Object>) sudoFastMapStructure.get("2")).get("id"))))))
            // || (data.get(((Map<String, Object>) fastStructure.get("3")).get("id")) != null
            // && item.getKey()
            // .equals((data.get(((Map<String, Object>) fastStructure.get("3"))
            // .get("id")) instanceof String)
            // ? (String) data
            // .get(((Map<String, Object>) fastStructure.get("3")).get("id"))
            // : Long.toString((Long) data.get(
            // ((Map<String, Object>) fastStructure.get("3")).get("id")))))
            ))
            .collect(Collectors.toMap(item -> item.getKey(),
                item -> updateDataFieldStructureToJson(item.getValue(), item.getKey(),
                    sudoFastMapStructure)));
    log.info("try update: {}", checkData);
    dynamicClassesRepository.updateDynamicClassesRecord(new DynamicClassRecordLine(classId,
        recordId,
        UUID.fromString((String) ((Map<String, Object>) sudoFastMapStructure.get("1")).get("id")),
        UUID.fromString(
            (String) data.get(((Map<String, Object>) sudoFastMapStructure.get("1")).get("id"))),
        UUID.fromString((String) ((Map<String, Object>) sudoFastMapStructure.get("2")).get("id")),
        UUID.fromString((String) ((Map<String, Object>) sudoFastMapStructure.get("3")).get("id")),
        timeStamp,
        UUID.fromString((String) ((Map<String, Object>) sudoFastMapStructure.get("0")).get("id")),
        checkData), timeStamp);
    successResult.add(recordId.toString());
    return new ArrayList() {
      {
        add(success);
        add(errors);
      }
    };
  }

  private Long getLong(Object value) {
    if (value == null)
      return -1l;
    if (value instanceof String)
      return Long.parseLong((String) value);
    if (value instanceof Integer)
      return ((Integer) value).longValue();
    return -1l;
  }

  public Object updateDataFieldStructureToJson(Object fieldData, String fieldId,
      Map<String, Object> sudoStructure) {
    if (fieldData instanceof Map || fieldData instanceof List)
      try {
        return objectMapper.writeValueAsString(fieldData);
      } catch (JsonProcessingException e) {
        return null;
      }
    else
      return fieldData;
  }

  public void updateDbStaffLog(UUID currentClass, UUID recordId, long timeStamp,
      Map<String, Object> actualPermissions, Map<String, Object> record,
      Map<String, Object> structure) throws IllegalArgumentException {
    ClassLogAbstract logElement =
        new ClassLog(null, currentClass, "data_use", recordId, null, null, null, timeStamp,
            UUID.fromString((String) actualPermissions
                .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID))),
            UUID.fromString((String) actualPermissions
                .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID))),
            HttpReqRespUtils.getClientIpAddressIfServletRequestExist());
    ((Map<String, Object>) ((Map<String, Object>) record).get("fields")).entrySet().parallelStream()
        .distinct().forEach(field -> {
          ClassLogAbstract oldLogItem = null;
          boolean isError = false;
          try {
            oldLogItem = classLogRepository.getClassLogByclassIdAndFieldId(currentClass, recordId,
                UUID.fromString((String) field.getKey()));
          } catch (IllegalArgumentException iex) {
            log.error("error with field: {}", field.getKey());
          }
          if (!isError || oldLogItem == null || oldLogItem.getValueNew() == null
              || !oldLogItem.getValueNew().equals((String) field.getValue())) {
            logElement.setFieldId(UUID.fromString(field.getKey()));
            // if (field.getValue() instanceof UUID) {
            // logElement.setValueNew(((UUID) field.getValue()).toString());
            // } else
            if (field.getValue() != null && !(field.getValue() instanceof String)) {
              logElement.setValueNew(field.getValue().toString());
            } else
              logElement.setValueNew((String) field.getValue());
            logElement.setValueOld(oldLogItem == null ? null : oldLogItem.getValueNew());
            classLogRepository.addClassLogWithOldValue(logElement);
          }
        });
  }

  public DynamicClassRecordLineAbstract getDynamicClassesRecordByOverOwnerId(UUID classId,
      Map<String, Object> actualPermissions, Map<String, Object> fastStructure) {
    return dynamicClassesRepository.getDynamicClassesRecordByOverOwnerId(classId, actualPermissions,
        fastStructure, null);
  }

  public DynamicClassRecordLineAbstract getRealUserIdByLogin(UUID classId,
      Map<String, Object> fastStructure, Map<String, Object> conditions) {
    return dynamicClassesRepository.getAdminDynamicClassesRecordByOverOwnerId(classId,
        fastStructure, conditions,
        UUID.fromString((String) ((Map<String, Object>) fastStructure.get("2")).get("id")), false);
  }

  public DynamicClassRecordLineAbstract getAdminDynamicClassesRecordByOverOwnerId(UUID classId,
      Map<String, Object> fastStructure, Map<String, Object> conditions, UUID sortID,
      boolean increase) {
    return dynamicClassesRepository.getAdminDynamicClassesRecordByOverOwnerId(classId,
        fastStructure, conditions, sortID, increase);
  }

  public Map<String, List<String>> addToNotificationMap(Map<String, List<String>> notifyMap,
      UUID classId, String value) {
    List<String> classResultList = notifyMap.get(classId.toString());
    if (classResultList == null) {
      classResultList = new ArrayList<>(2);
    }
    classResultList.add(value);
    notifyMap.put(classId.toString(), classResultList);
    return notifyMap;
  }

  public Map<String, List<String>> addAllToNotificationMap(Map<String, List<String>> notifyMap,
      UUID classId, Map<String, List<String>> values) {
    List<String> classResultList = notifyMap.get(classId.toString());
    if (classResultList == null) {
      classResultList = new ArrayList<>(2);
    }
    List<String> classInputList = values.get(classId.toString());
    if (classInputList != null) {
      classResultList.addAll(classInputList);
      notifyMap.put(classId.toString(), classResultList);
    }
    return notifyMap;
  }

  public RecordsOwnerModelAbstract getRecordsOwner(Map<String, Object> permissions) {
    RecordsOwnerModelAbstract presentOwner =
        recordsOwnerRepository.getRecordsOwnerByDynamicRoleData(
            UUID.fromString((String) permissions
                .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID))),
            UUID.fromString((String) permissions
                .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID))),
            UUID.fromString((String) permissions
                .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.SERVICE_ID))),
            UUID.fromString((String) permissions
                .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID))));
    if (presentOwner == null) {
      presentOwner = new RecordsOwnerModel(UUID.randomUUID(),
          UUID.fromString((String) permissions
              .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID))),
          UUID.fromString((String) permissions
              .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID))),
          UUID.fromString((String) permissions
              .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.SERVICE_ID))),
          UUID.fromString((String) permissions
              .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID))));
      recordsOwnerRepository.addRecordsOwner(presentOwner);
    }
    return presentOwner;
  }

}
