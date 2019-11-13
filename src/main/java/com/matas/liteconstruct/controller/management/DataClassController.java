package com.matas.liteconstruct.controller.management;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactory;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactoryImplemented;
import com.matas.liteconstruct.db.models.classes.repos.CustomerClassRepository;
import com.matas.liteconstruct.db.models.dynamicclass.queryfactory.DynamicClassesQueryFactory;
import com.matas.liteconstruct.db.models.dynamicclass.repos.DynamicClassesRepository;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepository;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepository;
import com.matas.liteconstruct.db.models.serviceauthorized.repos.AuthorizedContactRepository;
import com.matas.liteconstruct.service.HttpReqRespUtils;
import com.matas.liteconstruct.service.SQLProtection;
import com.matas.liteconstruct.service.dynamic.CacheMainParams;
import com.matas.liteconstruct.service.dynamic.DynamicClassPutData;
import com.matas.liteconstruct.service.dynamic.InnerRecordsDynamicClassService;
import com.matas.liteconstruct.service.dynamic.model.LngWrapperService;
import com.matas.liteconstruct.service.management.ManagementAccessService;
import com.matas.liteconstruct.service.management.structure.StructureCollectionsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin/data")
public class DataClassController {

  private StructureCollectionsService structureCollectionsService;

  @Autowired
  public void setStructureCollectionsService(
      StructureCollectionsService structureCollectionsService) {
    this.structureCollectionsService = structureCollectionsService;
  }

  DynamicRoleRepository dynamicRoleRepository;

  @Autowired
  public void setDynamicRoleRepositoryImplemented(DynamicRoleRepository dynamicRoleRepository) {
    this.dynamicRoleRepository = dynamicRoleRepository;
  }

  private DynamicClassesQueryFactory dynamicClassesQueryFactory;

  @Autowired
  public void setDynamicClassesQueryFactory(DynamicClassesQueryFactory dynamicClassesQueryFactory) {
    this.dynamicClassesQueryFactory = dynamicClassesQueryFactory;
  }

  private DynamicClassesRepository dynamicClassesRepositoryImplemented;

  @Autowired
  public void setDynamicClassesRepositoryImplemented(
      DynamicClassesRepository dynamicClassesRepositoryImplemented) {
    this.dynamicClassesRepositoryImplemented = dynamicClassesRepositoryImplemented;
  }

  private InnerRecordsDynamicClassService innerRecordsDynamicClassService;

  @Autowired
  public void setInnerRecordsDynamicClassService(
      InnerRecordsDynamicClassService innerRecordsDynamicClassService) {
    this.innerRecordsDynamicClassService = innerRecordsDynamicClassService;
  }

  private ObjectMapper objectMapper;

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
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

  private CustomerClassRepository customerClassRepository;

  @Autowired
  void setCustomerClassRepositoryImplemented(CustomerClassRepository customerClassRepository) {
    this.customerClassRepository = customerClassRepository;
  }

  private CacheMainParams cacheMainParams;

  @Autowired
  public void setCacheMainParams(CacheMainParams cacheMainParams) {
    this.cacheMainParams = cacheMainParams;
  }

  private FastStructureRepository fastStructureRepository;

  @Autowired
  public void setFastStructureRepositoryImplemented(
      FastStructureRepository fastStructureRepository) {
    this.fastStructureRepository = fastStructureRepository;
  }

  private DynamicClassPutData dynamicClassPutData;

  @Autowired
  public void setDynamicClassPutData(DynamicClassPutData dynamicClassPutData) {
    this.dynamicClassPutData = dynamicClassPutData;
  }

  private ManagementAccessService managementAccessService;

  @Autowired
  void setManagementAccessService(ManagementAccessService managementAccessService) {
    this.managementAccessService = managementAccessService;
  }

  private LngWrapperService lngWrapperService;

  @Autowired
  public void setLngWrapperService(LngWrapperService lngWrapperService) {
    this.lngWrapperService = lngWrapperService;
  }

  private final String keyCollectionCase = null;

  @RequestMapping(value = "setvalue", method = RequestMethod.PUT)
  public ResponseEntity<?> setDynamicClassValue(@RequestBody Map<String, Object> request) {
    Map<String, Object> checkedInput = SQLProtection.protectMap(request);
    UUID contactSystemId = managementAccessService.initUserByIncomingRequestForManagement(
        keyCollectionCase, (String) checkedInput.get("lang"));
    UUID currentClassId = UUID.fromString((String) checkedInput.get("cclass"));
    try {
      List<Map<String, List<String>>> results =
          dynamicClassPutData.setValuesToDynamicTable(contactSystemId, currentClassId,
              HttpReqRespUtils.getClientIpAddressIfServletRequestExist(),
              (Map<String, Object>) checkedInput);
      return new ResponseEntity<>(results, HttpStatus.OK);
    } catch (Exception pge) {
      log.info("error class: {}", pge.getClass());
      pge.printStackTrace();
      return new ResponseEntity<>("{\"respond\":\"error with params\"}", HttpStatus.OK);
    }
  }

  @RequestMapping(value = "setinner", method = RequestMethod.PUT)
  public ResponseEntity<?> setInnerDynamicClassValue(@RequestBody Map<String, Object> request) {
    Map<String, Object> checkedInput = SQLProtection.protectMap(request);
    UUID contactSystemId = managementAccessService.initUserByIncomingRequestForManagement(
        keyCollectionCase, (String) checkedInput.get("lang"));
    UUID currentClassId = UUID.fromString((String) checkedInput.get("cclass"));
    try {
      UUID innerClassId = UUID.fromString((String) checkedInput.get("iclass"));
      UUID parentFieldId = UUID.fromString((String) checkedInput.get("fieldId"));
      UUID innerRecordId = UUID.fromString((String) checkedInput.get("irecord"));
      UUID implementedId = UUID.fromString((String) checkedInput.get("implemented"));
      int parentTurn = Integer.parseInt((String) checkedInput.get("turn"));
      innerRecordsDynamicClassService.insertRecordToInnerClass(contactSystemId, currentClassId,
          innerClassId, parentFieldId, innerRecordId, implementedId, parentTurn);

      return new ResponseEntity<>("{}", HttpStatus.OK);
    } catch (Exception pge) {
      log.info("error class: {}", pge.getClass());
      pge.printStackTrace();
      return new ResponseEntity<>("{\"respond\":\"error with params\"}", HttpStatus.OK);
    }
  }

  @RequestMapping(value = "list", method = RequestMethod.POST)
  public ResponseEntity<?> getAdminClasses(@RequestBody Map<String, Object> request) {
    Map<String, Object> checkedInput = SQLProtection.protectMap(request);

    UUID contactSystemId = managementAccessService.initUserByIncomingRequestForManagement(
        keyCollectionCase, (String) checkedInput.get("lang"));
    UUID currentClassId = UUID.fromString((String) checkedInput.get("cclass"));
    String errorMessage = null;

    List<Map<String, Object>> dynamicFilters = null;
    List<Map<String, Object>> sortingObject = null;
    try {
      if (request.get("order") != null && request.get("order") instanceof List)
        sortingObject = (List) request.get("order");

      if (request.get("filter") != null && request.get("filter") instanceof List)
        dynamicFilters = (List) request.get("filter");

    } catch (Exception ex) {
      ex.printStackTrace();
      sortingObject = null;
    }
    String result =
        dynamicClassesQueryFactory.getLineSubquery(currentClassId,
            cacheMainParams.getAccessPermissionsByClass(contactSystemId, currentClassId),
            cacheMainParams.getFastStructureMapCurrentClassForContact(contactSystemId,
                currentClassId),
            null, dynamicFilters,
            cacheMainParams.getOwnerSettingsByClass(contactSystemId, currentClassId),
            (List<Map<String, Object>>) sortingObject, null,
            cacheMainParams.getDefaultLng(contactSystemId).getLngId());
    log.info("query for data: {}", result);

    List<String> respondMap =
        dynamicClassesRepositoryImplemented.getDynamicSpaceShapeClassBySubquery(result);
    // lng block

    List<Map<String, Object>> parseResult = respondMap.stream().map(item -> {
      try {
        return objectMapper.readValue(item, new TypeReference<Map<String, Object>>() {});
      } catch (IOException e) {
        return new HashMap<String, Object>(0);
      }
    }).collect(Collectors.toList());
    log.info("result of the parse: {}",
        lngWrapperService.getLngList(contactSystemId, currentClassId, parseResult));
    // end
//    log.info("subquery result: {}", respondMap);
    try {
      String resultStr = objectMapper.writeValueAsString(parseResult);
      return ResponseEntity.ok(resultStr);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      errorMessage = "Result parser error.";
    }
    // try {
    // String resultStr = objectMapper.writeValueAsString(respondMap).replace("\\\"", "\"")
    // .replace("\\\\\"", "\\\"").replace("\"{\"", "{\"").replace("}\",", "},")
    // .replace("}\"]", "}]");
    // resultStr = resultStr.replace("\":\"{", "\":{").replace("\\\":\\\"", "\":\"")
    // .replace("\\\",\\\"", "\",\"").replace("{\\\"", "{\"").replace(" \\n ", "")
    // .replace("\\\"},", "\"},");
    // return ResponseEntity.ok(resultStr);
    // } catch (JsonProcessingException e) {
    // e.printStackTrace();
    // errorMessage = "Result parser error.";
    // }
    return ResponseEntity.ok("{\"error\":\"" + errorMessage + "\"}");

  }
}
