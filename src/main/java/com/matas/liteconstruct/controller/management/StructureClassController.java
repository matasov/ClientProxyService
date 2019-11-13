package com.matas.liteconstruct.controller.management;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactory;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactoryImplemented;
import com.matas.liteconstruct.db.models.classes.abstractmodel.CustomerClassModelAbstract;
import com.matas.liteconstruct.db.models.classes.repos.CustomerClassRepository;
import com.matas.liteconstruct.db.models.collections.abstractmodel.StructureCollectionTreeFactoryImplemented;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.dynamicrole.model.DynamicRoleModel;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepository;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.model.FastStructureModel;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepository;
import com.matas.liteconstruct.db.models.security.abstractmodel.SystemRoles;
import com.matas.liteconstruct.db.models.serviceauthorized.abstractmodel.AuthorizedContactAbstract;
import com.matas.liteconstruct.db.models.serviceauthorized.repos.AuthorizedContactRepository;
import com.matas.liteconstruct.db.tools.StructrueCollectionEnum;
import com.matas.liteconstruct.service.SQLProtection;
import com.matas.liteconstruct.service.dynamic.CacheMainParams;
import com.matas.liteconstruct.service.dynamic.model.SessionMainParams;
import com.matas.liteconstruct.service.management.ManagementAccessService;
import com.matas.liteconstruct.service.management.structure.StructureCollectionsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin")
public class StructureClassController {

  private StructureCollectionsService structureCollectionsService;

  @Autowired
  public void setStructureCollectionsService(
      StructureCollectionsService structureCollectionsService) {
    this.structureCollectionsService = structureCollectionsService;
  }

  private DynamicRoleRepository dynamicRoleRepository;

  @Autowired
  public void setDynamicRoleRepositoryImplemented(DynamicRoleRepository dynamicRoleRepository) {
    this.dynamicRoleRepository = dynamicRoleRepository;
  }

  private ManagementAccessService managementAccessService;

  @Autowired
  void setManagementAccessService(ManagementAccessService managementAccessService) {
    this.managementAccessService = managementAccessService;
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

  private StructureCollectionTreeFactoryImplemented factoryImplemented;

  @Autowired
  public void setStructureCollectionTreeFactoryImplemented(
      StructureCollectionTreeFactoryImplemented factoryImplemented) {
    this.factoryImplemented = factoryImplemented;
  }

  private final String keyCollectionCase = null;

  @RequestMapping(value = "classes", method = RequestMethod.POST)
  public ResponseEntity<?> getAllClasses(@RequestBody Map<String, String> request) {
    UUID systemContactId = managementAccessService.initUserByIncomingRequestForManagement(
        keyCollectionCase,
        (String) ((Map<String, String>) SQLProtection.protectRequestObject(request)).get("lang"));
    log.info("instant permissions: {}",
        cacheMainParams.getSystemUserCache(systemContactId).getInstantAccessPermissions());
    // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    // UserDetails userDetail = (UserDetails) auth.getPrincipal();
    // if (userDetail != null && userDetail.getUsername() != null
    // && userDetail.getUsername().length() > 2) {
    Map<UUID, CustomerClassModelAbstract> classesList = customerClassRepository.listByType(-1, -1);
    Map<UUID, CustomerClassModelAbstract> result = classesList.entrySet().stream()
        .filter(x -> (x.getValue().getType() == 0
            || (accessRuleQueryFactoryImplemented.getResultPermissionForAccess(x.getKey(),
                cacheMainParams.getSystemUserCache(systemContactId)
                    .getInstantAccessPermissions()) != null)))
        .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

    return new ResponseEntity<>(result, HttpStatus.OK);
    // }
    // return new ResponseEntity<>("{\"result\":\"it was easy\"}", HttpStatus.FAILED_DEPENDENCY);
  }

  @RequestMapping(value = "class", method = RequestMethod.POST)
  public ResponseEntity<?> getClassById(@RequestBody Map<String, Object> request) {
    UUID systemContactId = managementAccessService.initUserByIncomingRequestForManagement(
        keyCollectionCase,
        (String) ((Map<String, String>) SQLProtection.protectRequestObject(request)).get("lang"));
    Map<String, Object> checkedInput = SQLProtection.protectMap(request);
    UUID workCustomClassId = UUID.fromString((String) checkedInput.get("cclass"));
    // CustomerClassModelAbstract classValue = customerClassRepository.getById(workCustomClassId);
    Map<String, Object> nextAccess =
        accessRuleQueryFactoryImplemented.getResultPermissionForAccess(workCustomClassId,
            cacheMainParams.getSystemUserCache(systemContactId).getInstantAccessPermissions());

    if (nextAccess != null) {
      return new ResponseEntity<>(
          cacheMainParams.getFastStructureCurrentClassForContact(systemContactId, workCustomClassId)
              .getFastStructureJSON(),
          HttpStatus.OK);
    } else {
      return new ResponseEntity<>("{\"result\":\"Access denied.\"}", HttpStatus.BAD_REQUEST);
    }
    // }
    // return new ResponseEntity<>("{\"result\":\"it was easy\"}", HttpStatus.FAILED_DEPENDENCY);
  }

  @RequestMapping(value = "testcollections", method = RequestMethod.POST)
  public ResponseEntity<?> testGetCollections() {
    UUID newTestClass = UUID.fromString("104bc44f-2d58-456f-a8b5-7bfc9e22fa0a");
    try {
      String result = structureCollectionsService.getListCollectionsByCompany(newTestClass,
          new DynamicRoleModel(UUID.fromString("99900a19-88a9-4655-a7ac-71488070b659"),
              "superadmin", UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b"),
              UUID.fromString("b56b99b6-2c6f-4103-849a-e914e8594869"),
              SystemRoles.SUPERADMIN_ROLE.getUUID()),
          UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b"));
      String resultStr = result.replace("\\\"", "\"").replace("\\\\\"", "\\\"")
          .replace("\"{\"", "{\"").replace("}\",", "},").replace("}\"]", "}]");
      resultStr = resultStr.replace("\":\"{", "\":{").replace("\\\":\\\"", "\":\"")
          .replace("\\\",\\\"", "\",\"").replace("{\\\"", "{\"").replace("\\\"},", "\"},")
          .replace("}\"}", "}}");
      log.info("result of service: {}", resultStr);
    } catch (NullPointerException nuex) {
      nuex.printStackTrace();
    }

    log.info("ended create collection for class: {}", newTestClass);
    return new ResponseEntity<>("{}", HttpStatus.OK);
  }

  @RequestMapping(value = "testaddcollection", method = RequestMethod.POST)
  public ResponseEntity<?> testAddCollection() {
    UUID newTestClass = UUID.fromString("104bc44f-2d58-456f-a8b5-7bfc9e22fa0a");
    DynamicRoleModelAbstract operatorDynRole =
        new DynamicRoleModel(UUID.fromString("62900a19-88a9-4655-a7ac-71488070b659"), "superadmin",
            UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b"),
            UUID.fromString("b56b99b6-2c6f-4103-849a-e914e8594869"),
            SystemRoles.SUPERADMIN_ROLE.getUUID());

    log.info("result of service: {}",
        structureCollectionsService.createNewCollection(UUID.randomUUID(), newTestClass,
            UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b"),
            UUID.fromString("b56b99b6-2c6f-4103-849a-e914e8594869"),
            UUID.fromString("1c3bf8ff-7235-4400-974e-d7a3b58de566"), operatorDynRole, null));

    log.info("ended create collection for class: {}", newTestClass);
    return new ResponseEntity<>("{}", HttpStatus.OK);
  }

  @RequestMapping(value = "test", method = RequestMethod.POST)
  public ResponseEntity<?> testCreateClass() {
    UUID newTestClass = UUID.randomUUID();
    UUID systemContactId =
        managementAccessService.initUserByIncomingRequestForManagement(keyCollectionCase, null);
    log.info("result of service: {}",
        structureCollectionsService.addCustomClass(systemContactId, newTestClass, "test1", (byte) 1,
            1, "something",
            // new DynamicRoleModel(UUID.fromString("62900a19-88a9-4655-a7ac-71488070b659"),
            // "superadmin", UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b"),
            // UUID.fromString("b56b99b6-2c6f-4103-849a-e914e8594869"),
            // SystemRoles.SUPERADMIN_ROLE.getUUID())
            new DynamicRoleModel(UUID.fromString("99900a19-88a9-4655-a7ac-71488070b659"),
                "superadmin", UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b"),
                UUID.fromString("b56b99b6-2c6f-4103-849a-e914e8594869"),
                SystemRoles.LOCAL_ADMIN_ROLE.getUUID())));
    log.info("ended create collection for class: {}", newTestClass);
    return new ResponseEntity<>("{}", HttpStatus.OK);
  }

  @RequestMapping(value = "testdelete", method = RequestMethod.POST)
  public ResponseEntity<?> testDeleteObject() {
    UUID newTestClass = UUID.fromString("aaa749fc-acd7-4ad2-b2af-a4997fd4a327");
    log.info("result of service: {}",
        structureCollectionsService.deleteCustomClass(newTestClass,
            new DynamicRoleModel(UUID.fromString("99900a19-88a9-4655-a7ac-71488070b659"),
                "superadmin", UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b"),
                UUID.fromString("b56b99b6-2c6f-4103-849a-e914e8594869"),
                SystemRoles.LOCAL_ADMIN_ROLE.getUUID())));
    log.info("ended create collection for class: {}", newTestClass);
    return new ResponseEntity<>("{}", HttpStatus.OK);
  }

  @RequestMapping(value = "testinsert", method = RequestMethod.POST)
  public ResponseEntity<?> testInsertStructureField() {
    UUID newTestClass = UUID.fromString("104bc44f-2d58-456f-a8b5-7bfc9e22fa0a");
    UUID systemContactId =
        managementAccessService.initUserByIncomingRequestForManagement(keyCollectionCase, null);
    log.info("result of service: {}",
        structureCollectionsService.addStructureField(systemContactId,
            UUID.fromString("30000000-325d-4b9f-ac63-69f212b23adf"), newTestClass, "test_field4",
            UUID.fromString("37b1c6cb-722f-4918-a6e1-2595f19f8dd4"), (byte) 0, "something3",
            new DynamicRoleModel(UUID.fromString("99900a19-88a9-4655-a7ac-71488070b659"),
                "superadmin", UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b"),
                UUID.fromString("b56b99b6-2c6f-4103-849a-e914e8594869"),
                SystemRoles.LOCAL_ADMIN_ROLE.getUUID())));
    log.info("ended create collection for class: {}", newTestClass);
    return new ResponseEntity<>("{}", HttpStatus.OK);
  }

  @RequestMapping(value = "testupdate", method = RequestMethod.POST)
  public ResponseEntity<?> testUpdateStructureField() {
    UUID systemContactId =
        managementAccessService.initUserByIncomingRequestForManagement(keyCollectionCase, null);
    UUID newTestClass = UUID.fromString("104bc44f-2d58-456f-a8b5-7bfc9e22fa0a");
    UUID fieldId = UUID.fromString("30000000-325d-4b9f-ac63-69f212b23adf");
    UUID collectionId = UUID.fromString("f9242327-4c8c-4ef2-8674-bf9996bb4527");
    log.info("result of update collection: {}",
        structureCollectionsService.updateFieldInCollectionAndStructure(systemContactId,
            collectionId, newTestClass, fieldId, "test_field1",
            UUID.fromString("70b8c94f-7a92-4a1e-b8ab-25abd9211187"), (byte) 0, "something13", 4,
            "11111",
            new DynamicRoleModel(UUID.fromString("99900a19-88a9-4655-a7ac-71488070b659"),
                "superadmin", UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b"),
                UUID.fromString("b56b99b6-2c6f-4103-849a-e914e8594869"),
                SystemRoles.LOCAL_ADMIN_ROLE.getUUID()),
            true, null));
    log.info("ended create collection for class: {}", newTestClass);
    return new ResponseEntity<>("{}", HttpStatus.OK);
  }

  @RequestMapping(value = "classes/getstructurecache", method = RequestMethod.POST)
  public ResponseEntity<?> getStructureCache(@RequestBody Map<String, String> request) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetail = (UserDetails) auth.getPrincipal();
    if (userDetail != null && userDetail.getUsername() != null
        && userDetail.getUsername().length() > 2) {
      // factoryImplemented.setDynamicRoleID(request.get("dynamicID"));
      // factoryImplemented.setStartClassID(request.get("classID"));
      // factoryImplemented.setStartCollectionID(request.get("collectionID"));
      String value = factoryImplemented.getJsonQuery(factoryImplemented.createTree(
          UUID.fromString(request.get("dynamicId")), UUID.fromString(request.get("classId")),
          UUID.fromString(request.get("collectionId")), null));
      if (value.length() > 2) {
        FastStructureModelAbstract fastStructure = new FastStructureModel(
            UUID.fromString(request.get("dynamicId")), UUID.fromString(request.get("collectionId")),
            UUID.fromString(request.get("classId")), value.toString(), true, null);
        List<FastStructureModelAbstract> searchResult =
            fastStructureRepository.getFastStructuresForDynamicRoleAndCollection(
                fastStructure.getDynamicRoleId(), fastStructure.getCollectionId());
        if (searchResult != null && !searchResult.isEmpty()) {
          fastStructureRepository.updateFastStructure(fastStructure);
        } else {
          fastStructureRepository.addFastStructure(fastStructure);
        }
      }
      return new ResponseEntity<>(value, HttpStatus.OK);
    }
    return new ResponseEntity<>("{\"respond\":\"it was easy\"}", HttpStatus.FAILED_DEPENDENCY);
  }

  // real block

  @RequestMapping(value = "collection/list", method = RequestMethod.POST)
  public ResponseEntity<?> getCollectionsForClass(
      @RequestBody Map<String, Object> inputUpdateData) {
    log.info("inputUpdateData: {}", inputUpdateData);
    Map<String, Object> checkedInput = SQLProtection.protectMap(inputUpdateData);
    UUID workCustomClass = UUID.fromString((String) checkedInput.get("cclass"));
    UUID systemContactId = managementAccessService.initUserByIncomingRequestForManagement(
        keyCollectionCase, (String) checkedInput.get("lang"));
    AuthorizedContactAbstract authorizedContact =
        cacheMainParams.getSystemUserCache(systemContactId).getAuthorizedContact();
    try {
      String result = structureCollectionsService.getListCollectionsByCompany(workCustomClass,
          dynamicRoleRepository.getDynamicRoleByCompanyServiceRole(authorizedContact.getCompanyId(),
              authorizedContact.getServiceId(), authorizedContact.getRoleId()),
          authorizedContact.getCompanyId());
      String resultStr = result.replace("\\\"", "\"").replace("\\\\\"", "\\\"")
          .replace("\"{\"", "{\"").replace("}\",", "},").replace("}\"]", "}]");
      resultStr = resultStr.replace("\":\"{", "\":{").replace("\\\":\\\"", "\":\"")
          .replace("\\\",\\\"", "\",\"").replace("{\\\"", "{\"").replace("\\\"},", "\"},")
          .replace("}\"}", "}}");
      return new ResponseEntity<>(resultStr, HttpStatus.OK);
    } catch (NullPointerException nuex) {
      nuex.printStackTrace();
      return new ResponseEntity<>("{\"result\":\"error in db.\"}", HttpStatus.OK);
    }
  }

  @RequestMapping(value = "collection/field/update", method = RequestMethod.PUT)
  public ResponseEntity<?> updateCollectionField(@RequestBody Map<String, Object> inputUpdateData) {
    Map<String, Object> checkedInput = SQLProtection.protectMap(inputUpdateData);
    log.info("incoming data: {}", checkedInput);

    Map<String, Object> data = (Map<String, Object>) checkedInput.get("data");
    UUID workCustomClass = UUID.fromString((String) checkedInput.get("cclass"));
    UUID fieldId = UUID.fromString((String) checkedInput.get("fieldId"));
    UUID collectionId = UUID.fromString((String) checkedInput.get("collectionId"));

    boolean isActive =
        checkedInput.containsKey("isActive")
            ? (checkedInput.get("isActive") instanceof String
                ? ((String) checkedInput.get("isActive")).equals("true")
                : (Boolean) checkedInput.get("isActive"))
            : false;

    String keyCollectionCase = checkedInput.containsKey("keyCollectionCase")
        ? (String) checkedInput.get("keyCollectionCase")
        : null;
    UUID systemContactId = managementAccessService.initUserByIncomingRequestForManagement(
        keyCollectionCase, (String) checkedInput.get("lang"));
    try {
      log.info("result of update collection: {}",
          structureCollectionsService.updateFieldWithCheckIsPresentInStructure(systemContactId,
              collectionId, workCustomClass, fieldId,
              (String) data.get(StructrueCollectionEnum.NAME.toString()),
              UUID.fromString((String) data.get(StructrueCollectionEnum.OBJECT.toString())),
              (byte) Integer.parseInt((String) data.get(StructrueCollectionEnum.INNER.toString())),
              (String) data.get(StructrueCollectionEnum.TITLE.toString()),
              data.get("turn") instanceof String ? Integer.parseInt((String) data.get("turn"))
                  : (Integer) data.get("turn"),
              (String) data.get(StructrueCollectionEnum.PERM.toString()),
              cacheMainParams.getSUDODynamicRole(systemContactId), isActive, keyCollectionCase));
      log.info("ended create collection for class: {}", workCustomClass);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return new ResponseEntity<>("{}", HttpStatus.OK);
  }

  @RequestMapping(value = "collection/field/add", method = RequestMethod.PUT)
  public ResponseEntity<?> addCollectionField(@RequestBody Map<String, Object> inputUpdateData) {
    Map<String, Object> checkedInput = SQLProtection.protectMap(inputUpdateData);
    Map<String, Object> data = (Map<String, Object>) checkedInput.get("data");
    UUID workCustomClass = UUID.fromString((String) checkedInput.get("cclass"));
    UUID fieldId = UUID.fromString((String) checkedInput.get("fieldId"));
    UUID systemContactId = managementAccessService.initUserByIncomingRequestForManagement(
        keyCollectionCase, (String) checkedInput.get("lang"));
    // Map<String, Object> localAccessPermissions =
    // managementAccessService.getCorrectPermissionsForAdminPage(workCustomClass);
    // if (localAccessPermissions == null)
    // return new ResponseEntity<>("{\"result\":\"Access permission for class.\"}", HttpStatus.OK);
    // cacheMainParams.initUser(localAccessPermissions, workCustomClass);
    try {
      log.info("result of add field: {}",
          structureCollectionsService.addStructureField(systemContactId, fieldId, workCustomClass,
              (String) data.get(StructrueCollectionEnum.NAME.toString()),
              UUID.fromString((String) data.get(StructrueCollectionEnum.OBJECT.toString())),
              (byte) Integer.parseInt((String) data.get(StructrueCollectionEnum.INNER.toString())),
              (String) data.get(StructrueCollectionEnum.TITLE.toString()),
              cacheMainParams.getSUDODynamicRole(systemContactId)));
      log.info("ended create collection for class: {}", workCustomClass);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return new ResponseEntity<>("{}", HttpStatus.OK);
  }

  @RequestMapping(value = "collection/field/del", method = RequestMethod.PUT)
  public ResponseEntity<?> delCollectionField(@RequestBody Map<String, Object> inputUpdateData) {
    Map<String, Object> checkedInput = SQLProtection.protectMap(inputUpdateData);
    log.info("incoming data: {}", checkedInput);
    UUID workCustomClass = UUID.fromString((String) checkedInput.get("cclass"));
    UUID fieldId = UUID.fromString((String) checkedInput.get("fieldId"));
    UUID systemContactId = managementAccessService.initUserByIncomingRequestForManagement(
        keyCollectionCase, (String) checkedInput.get("lang"));
    try {
      log.info("result of del field: {}",
          structureCollectionsService.removeStructureField(systemContactId, workCustomClass,
              fieldId, cacheMainParams.getSUDODynamicRole(systemContactId)));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return new ResponseEntity<>("{}", HttpStatus.OK);
  }

  @RequestMapping(value = "collection/list/add", method = RequestMethod.PUT)
  public ResponseEntity<?> setCollectionToRole(@RequestBody Map<String, Object> inputUpdateData) {
    Map<String, Object> checkedInput = SQLProtection.protectMap(inputUpdateData);

    log.info("incoming data: {}", checkedInput);
    Map<String, Object> data = (Map<String, Object>) checkedInput.get("data");
    UUID workCustomClass = UUID.fromString((String) checkedInput.get("cclass"));
    Map<String, Object> localAccessPermissions =
        managementAccessService.getCorrectPermissionsForAdminPage(workCustomClass);
    if (localAccessPermissions == null)
      return new ResponseEntity<>("{\"result\":\"Access denied for class.\"}", HttpStatus.OK);
    return new ResponseEntity<>("{}", HttpStatus.OK);
  }

  @RequestMapping(value = "class/add", method = RequestMethod.POST)
  public ResponseEntity<?> createClass(@RequestBody Map<String, Object> inputUpdateData) {
    Map<String, Object> checkedInput = SQLProtection.protectMap(inputUpdateData);
    UUID systemContactId = managementAccessService.initUserByIncomingRequestForManagement(
        keyCollectionCase, (String) checkedInput.get("lang"));
    UUID workCustomClass = UUID.fromString((String) checkedInput.get("cclass"));
    String className = (String) checkedInput.get("className");
    String classDescription = (String) checkedInput.get("classDescription");
    int classType = Integer.parseInt((String) checkedInput.get("classType"));
    int classPermission = Integer.parseInt((String) checkedInput.get("classPermission"));
    AuthorizedContactAbstract authrizedParams =
        cacheMainParams.getSystemUserCache(systemContactId).getAuthorizedContact();
    log.info("result of service: {}",
        structureCollectionsService.addCustomClass(systemContactId, workCustomClass, className,
            (byte) classType, classPermission, classDescription,
            new DynamicRoleModel(authrizedParams.getDynamicRoleId(), "noname",
                authrizedParams.getCompanyId(), authrizedParams.getServiceId(),
                authrizedParams.getRoleId())));
    log.info("ended create collection for class: {}", workCustomClass);
    return new ResponseEntity<>("{}", HttpStatus.OK);
  }


}
