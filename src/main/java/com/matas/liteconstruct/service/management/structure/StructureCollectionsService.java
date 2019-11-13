package com.matas.liteconstruct.service.management.structure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.classes.abstractmodel.CustomerClassModelAbstract;
import com.matas.liteconstruct.db.models.classes.model.CustomerClassModel;
import com.matas.liteconstruct.db.models.classes.repos.CustomerClassRepository;
import com.matas.liteconstruct.db.models.collectioncase.abstractmodel.CollectionCaseAbstract;
import com.matas.liteconstruct.db.models.collectioncase.model.CollectionCase;
import com.matas.liteconstruct.db.models.collectiondynamicrole.abstractmodel.CollectionDynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.collectiondynamicrole.model.CollectionDynamicRoleModel;
import com.matas.liteconstruct.db.models.collectiondynamicrole.repos.CollectionDynamicRoleRepository;
import com.matas.liteconstruct.db.models.collections.abstractmodel.StructureCollectionAbstract;
import com.matas.liteconstruct.db.models.collections.abstractmodel.StructureCollectionTreeFactoryAbstract;
import com.matas.liteconstruct.db.models.collections.model.StructureCollectionImplemented;
import com.matas.liteconstruct.db.models.collections.repos.StructureCollectionsFieldsRepository;
import com.matas.liteconstruct.db.models.companyrelations.abstractmodel.CompanyRelationsAbstract;
import com.matas.liteconstruct.db.models.companyrelations.repos.CompanyRelationsRepository;
import com.matas.liteconstruct.db.models.dynamicclass.abstractmodel.DynamicClassRecordLineAbstract;
import com.matas.liteconstruct.db.models.dynamicclass.repos.DynamicClassesRepository;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.dynamicrole.model.DynamicRoleModel;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepository;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.model.FastStructureModel;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepository;
import com.matas.liteconstruct.db.models.security.abstractmodel.SystemRoles;
import com.matas.liteconstruct.db.models.structure.abstractmodel.StructureFieldAbstract;
import com.matas.liteconstruct.db.models.structure.model.StructureFieldImplemented;
import com.matas.liteconstruct.db.models.structure.repos.StructureFieldsRepository;
import com.matas.liteconstruct.db.service.manager.DynamicTablesService;
import com.matas.liteconstruct.db.tools.StructrueCollectionEnum;
import com.matas.liteconstruct.db.tools.permissions.PermissionHandler;
import com.matas.liteconstruct.service.SQLProtection;
import com.matas.liteconstruct.service.dynamic.CacheMainParams;
import com.matas.liteconstruct.service.dynamic.InnerRecordsDynamicClassService;
import com.matas.liteconstruct.service.security.AuthorizeStructureOperationsService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class StructureCollectionsService {

  private ObjectMapper objectMapper;

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private DynamicClassesRepository dynamicClassesRepository;

  @Autowired
  public void setDynamicClassesRepositoryImplemented(
      DynamicClassesRepository dynamicClassesRepository) {
    this.dynamicClassesRepository = dynamicClassesRepository;
  }

  private CacheMainParams cacheMainParams;

  @Autowired
  public void setCacheMainParams(CacheMainParams cacheMainParams) {
    this.cacheMainParams = cacheMainParams;
  }

  private CollectionDynamicRoleRepository collectionDynamicRoleRepositoryImplemented;

  @Autowired
  public void setCollectionDynamicRoleRepositoryImplemented(
      CollectionDynamicRoleRepository collectionDynamicRoleRepositoryImplemented) {
    this.collectionDynamicRoleRepositoryImplemented = collectionDynamicRoleRepositoryImplemented;
  }

  private DynamicRoleRepository dynamicRoleRepository;

  @Autowired
  public void setDynamicRoleRepositoryImplemented(DynamicRoleRepository dynamicRoleRepository) {
    this.dynamicRoleRepository = dynamicRoleRepository;
  }

  private CustomerClassRepository customerClassRepositoryImplemented;

  @Autowired
  public void setCustomerClassRepositoryImplemented(
      CustomerClassRepository customerClassRepositoryImplemented) {
    this.customerClassRepositoryImplemented = customerClassRepositoryImplemented;
  }

  private StructureFieldsRepository structureFieldRepository;

  @Autowired
  public void setStructureFieldsRepositoryImplemented(
      StructureFieldsRepository structureFieldRepository) {
    this.structureFieldRepository = structureFieldRepository;
  }

  private DynamicTablesService dynamicTablesService;

  @Autowired
  public void setDynamicTablesService(DynamicTablesService dynamicTablesService) {
    this.dynamicTablesService = dynamicTablesService;
  }

  private StructureCollectionsFieldsRepository structureCollectionsFieldsRepository;

  @Autowired
  public void setStructureCollectionsFieldsRepositoryImplemented(
      StructureCollectionsFieldsRepository structureCollectionsFieldsRepository) {
    this.structureCollectionsFieldsRepository = structureCollectionsFieldsRepository;
  }

  private StructureCollectionTreeFactoryAbstract factoryImplemented;

  @Autowired
  public void setStructureCollectionTreeFactoryImplemented(
      StructureCollectionTreeFactoryAbstract factoryImplemented) {
    this.factoryImplemented = factoryImplemented;
  }

  private FastStructureRepository fastStructureRepository;

  @Autowired
  public void setFastStructureRepositoryImplemented(
      FastStructureRepository fastStructureRepository) {
    this.fastStructureRepository = fastStructureRepository;
  }

  private CompanyRelationsRepository companyRelationsRepository;

  @Autowired
  public void setCompanyRelationsRepositoryImplemented(
      CompanyRelationsRepository companyRelationsRepository) {
    this.companyRelationsRepository = companyRelationsRepository;
  }

  private AuthorizeStructureOperationsService authorizeStructureOperationsService;

  @Autowired
  public void setAuthorizeStructureOperationsService(
      AuthorizeStructureOperationsService authorizeStructureOperationsService) {
    this.authorizeStructureOperationsService = authorizeStructureOperationsService;
  }

  private InnerRecordsDynamicClassService innerRecordsDynamicClassService;

  @Autowired
  public void setInnerRecordsDynamicClassService(
      InnerRecordsDynamicClassService innerRecordsDynamicClassService) {
    this.innerRecordsDynamicClassService = innerRecordsDynamicClassService;
  }

  private String[] systemFields = {"id", "owner", "date_create", "date_change"};

  public String getListCollectionsByCompany(UUID classId, DynamicRoleModelAbstract operatorDynRole,
      UUID companyId) throws NullPointerException {
    String errorMessage = authorizeStructureOperationsService.checkPermissionsForOperation(
        operatorDynRole, ManagementSettingsPermissionsItems.GET_COLLECTIONS);
    if (errorMessage != null) {
      throw new NullPointerException("This is admin mode. exit.");
    }
    if (!operatorDynRole.getCompanyId().equals(companyId)) {
      CompanyRelationsAbstract presentRecord =
          companyRelationsRepository.getCompanyRelationsByMasterSlaveRole(
              operatorDynRole.getCompanyId(), companyId, operatorDynRole.getRoleId());
      if (presentRecord == null) {
        throw new NullPointerException("Company is not attached. exit.");
      }
    }
    getFastStructureWithUpdated(classId, operatorDynRole.getCompanyId(),
        operatorDynRole.getServiceId(), operatorDynRole.getRoleId());
    return fastStructureRepository.getAllFastStructuresForClassByCompany(classId, companyId)
        .stream().filter(x -> authorizeStructureOperationsService
            .isAllowWorkWithCollection(operatorDynRole, x.getRoleId()))
        .map(x -> {
          try {
            return objectMapper.writeValueAsString(x);
          } catch (JsonProcessingException e) {
            return null;
          }
        }).filter(x -> x != null).collect(Collectors.joining(",", "[", "]"));
  }

  public String addCustomClass(UUID systemContactId, UUID classId, String name, byte type,
      int permission, String description, DynamicRoleModelAbstract operatorDynRole) {
    log.info("try work in addCustomClass");
    if (!authorizeStructureOperationsService.canInsertClass(operatorDynRole))
      return "Do not have permissions.";
    String checkedName = (String) SQLProtection.protectStringTrimLower(name);
    CustomerClassModelAbstract presentedClass = customerClassRepositoryImplemented.getById(classId);
    if (presentedClass != null) {
      return updateCustomClass(systemContactId, classId, name, type, permission, description,
          operatorDynRole);
    }
    presentedClass = customerClassRepositoryImplemented.getByName(checkedName);
    if (presentedClass != null && !presentedClass.getId().equals(classId)) {
      return "Class with same name is present yet.";
    }
    // add new class tables
    customerClassRepositoryImplemented
        .addCustomerClass(new CustomerClassModel(classId, name, type, permission));

    // add default fields to structure
    UUID idFieldId = UUID.randomUUID();
    UUID ownerFieldId = UUID.randomUUID();
    UUID createFieldId = UUID.randomUUID();
    UUID changeFieldId = UUID.randomUUID();
    addNewObjectTables(classId, idFieldId, ownerFieldId, createFieldId, changeFieldId);
    addStructureField(systemContactId, idFieldId, classId, "id",
        PrimitiveCustomClass.CUUID.getUuid(), (byte) 0, "ID", operatorDynRole);
    addStructureField(systemContactId, ownerFieldId, classId, "owner",
        PrimitiveCustomClass.CUUID.getUuid(), (byte) 0, "OWNER", operatorDynRole);
    addStructureField(systemContactId, createFieldId, classId, "date_create",
        PrimitiveCustomClass.LONG.getUuid(), (byte) 0, "DATE CREATE", operatorDynRole);
    addStructureField(systemContactId, changeFieldId, classId, "date_change",
        PrimitiveCustomClass.LONG.getUuid(), (byte) 0, "DATE CHANGE", operatorDynRole);

    // create default collection for superadmin
    // default: for superadmin
    DynamicRoleModelAbstract sudoDynRole = getSUDODynamicRole();// getSuperAdminDynamicRole(classId,
                                                                // operatorDynRole);
    List<CollectionDynamicRoleModelAbstract> collections =
        collectionDynamicRoleRepositoryImplemented
            .getCollectionDynamicRoleByDroleClass(sudoDynRole.getId(), classId, 1);
    if (collections == null || collections.isEmpty()) {
      // try insert new collection and found it again
      collectionDynamicRoleRepositoryImplemented
          .addCollectionDynamicRole(new CollectionDynamicRoleModel(UUID.randomUUID(),
              UUID.randomUUID(), 1, classId, sudoDynRole, null));
      collections = collectionDynamicRoleRepositoryImplemented
          .getCollectionDynamicRoleByDroleClass(sudoDynRole.getId(), classId, 1);
      if (collections == null || collections.isEmpty())
        return "not found collections for superadmin";
      else
        log.info("found new collection: {}", collections.get(0));
    }

    UUID adminCollectionField = collections.get(0).getCollectionId();
    addFieldToCollection(adminCollectionField, classId, idFieldId, 0, true, true, true, true, true,
        true, operatorDynRole, null);
    addFieldToCollection(adminCollectionField, classId, ownerFieldId, 1, true, true, true, true,
        true, true, operatorDynRole, null);
    addFieldToCollection(adminCollectionField, classId, createFieldId, 2, true, true, true, true,
        true, true, operatorDynRole, null);
    addFieldToCollection(adminCollectionField, classId, changeFieldId, 3, true, true, true, true,
        true, true, operatorDynRole, null);
    createAndSaveStructureCache(classId, sudoDynRole.getId(), collections.get(0).getCollectionId(),
        false, null);
    // create default collection for create user
    if (!sudoDynRole.getRoleId().equals(operatorDynRole.getRoleId())) {
      List<CollectionDynamicRoleModelAbstract> currentCollections =
          collectionDynamicRoleRepositoryImplemented
              .getCollectionDynamicRoleByDroleClass(operatorDynRole.getId(), classId, 1);
      if (currentCollections == null || currentCollections.isEmpty()) {
        collectionDynamicRoleRepositoryImplemented
            .addCollectionDynamicRole(new CollectionDynamicRoleModel(UUID.randomUUID(),
                UUID.randomUUID(), 1, classId, operatorDynRole, null));
        currentCollections = collectionDynamicRoleRepositoryImplemented
            .getCollectionDynamicRoleByDroleClass(operatorDynRole.getId(), classId, 1);
        if (currentCollections == null || currentCollections.isEmpty())
          return "not found collections for superadmin";
        else
          log.info("found new collection: {}", collections.get(0));
      }

      UUID currentCollectionField = currentCollections.get(0).getCollectionId();
      addFieldToCollection(currentCollectionField, classId, idFieldId, 0, true, true, true, true,
          true, true, operatorDynRole, null);
      addFieldToCollection(currentCollectionField, classId, ownerFieldId, 1, true, true, true, true,
          true, true, operatorDynRole, null);
      addFieldToCollection(currentCollectionField, classId, createFieldId, 2, true, true, true,
          true, true, true, operatorDynRole, null);
      addFieldToCollection(currentCollectionField, classId, changeFieldId, 3, true, true, true,
          true, true, true, operatorDynRole, null);
      createAndSaveStructureCache(classId, operatorDynRole.getId(),
          currentCollections.get(0).getCollectionId(), false, null);
    }

    // create values for operator dynamic role
    return null;
  }

  public String updateCustomClass(UUID systemContactId, UUID id, String name, byte type,
      int permission, String description, DynamicRoleModelAbstract operatorDynRole) {
    if (!authorizeStructureOperationsService.canEditClass(operatorDynRole))
      return "Do not have permissions.";
    CustomerClassModelAbstract presentedClass = customerClassRepositoryImplemented.getById(id);
    if (presentedClass == null) {
      return addCustomClass(systemContactId, id, name, type, permission, description,
          operatorDynRole);
    }
    customerClassRepositoryImplemented
        .updateCustomerClass(new CustomerClassModel(id, name, type, permission));
    return null;
  }

  public String deleteCustomClass(UUID classId, DynamicRoleModelAbstract operatorDynRole) {
    if (!authorizeStructureOperationsService.canDeleteClass(operatorDynRole))
      return "Do not have permissions.";
    CustomerClassModelAbstract presentedClass = customerClassRepositoryImplemented.getById(classId);
    if (presentedClass == null) {
      return "Not found class";
    }
    customerClassRepositoryImplemented.removeCustomerClass(classId);
    removeObjectTables(classId);
    structureFieldRepository.removeStructureFieldsByclassId(classId);
    // remove collections fields
    structureCollectionsFieldsRepository.removeStructureFieldsByclassId(classId);
    // remove structure cache
    fastStructureRepository.removeFastStructuresByclassId(classId);
    // remove relation between collection and dynamic role
    collectionDynamicRoleRepositoryImplemented.removeCollectionDynamicRolesByclassId(classId);
    return null;
  }

  // work with tables
  // 1. add object tables
  public String addNewObjectTables(UUID classId, UUID idFieldId, UUID ownerFieldId,
      UUID createFieldId, UUID changeFieldId) {
    dynamicTablesService.addTables(classId, idFieldId, ownerFieldId, createFieldId, changeFieldId);
    return null;
  }

  public String removeObjectTables(UUID classId) {
    dynamicTablesService.removeTables(classId);
    return null;
  }

  // work with collections
  // create default collection for superUser
  public String createNewCollection(UUID collectionId, UUID classId, UUID forCompanyId,
      UUID forServiceId, UUID forRoleId, DynamicRoleModelAbstract operatorDynRole,
      String keyCollectionCase) {
    if (!authorizeStructureOperationsService.canInsertCollection(operatorDynRole))
      return "{\"error\":\"Do not have permissions.\"}";
    if (!authorizeStructureOperationsService.checkCompanyLocation(operatorDynRole.getRoleId(),
        operatorDynRole.getCompanyId(), forCompanyId)) {
      return "{\"error\":\"Company not attached.\"}";
    }
    DynamicRoleModelAbstract sudoDynRole = getSUDODynamicRole();
    FastStructureModelAbstract fastStructureAdmin =
        fastStructureRepository.getActiveFastStructuresForClassByDynamicRole(sudoDynRole.getId(),
            classId, keyCollectionCase);
    Map<String, Object> fastStructureMap = null;
    try {
      fastStructureMap = objectMapper.readValue(fastStructureAdmin.getFastStructureJSON(),
          new TypeReference<Map<String, Object>>() {});
    } catch (IOException e) {
      log.error("error in objectMapper {}", e.getLocalizedMessage());
      return "{\"error\":\"Not found system collection.\"}";
    }
    DynamicRoleModelAbstract currentDynamicRole = dynamicRoleRepository
        .getDynamicRoleByCompanyServiceRole(forCompanyId, forServiceId, forRoleId);
    if (currentDynamicRole == null) {
      currentDynamicRole = addNewDynamicRoleWithCollection(classId, forCompanyId, forServiceId,
          forRoleId, keyCollectionCase);
    }
    fastStructureRepository.setAllInActiveForClassByCompanyServiceRole(classId, forCompanyId,
        forServiceId, forRoleId);
    if (fastStructureMap == null)
      return "{\"error\":\"Structure is wrong.\"}";
    // UUID currentCollectionField = UUID.randomUUID();
    // log.info("element 0: {}", (String) ((Map<String, Object>)
    // fastStructureMap.get("1")).get("id"));
    addFieldToCollection(collectionId, classId,
        UUID.fromString((String) ((Map<String, Object>) fastStructureMap.get("0")).get("id")), 0,
        true, false, false, false, false, false, operatorDynRole, null);
    addFieldToCollection(collectionId, classId,
        UUID.fromString((String) ((Map<String, Object>) fastStructureMap.get("1")).get("id")), 1,
        true, false, false, false, false, false, operatorDynRole, null);
    addFieldToCollection(collectionId, classId,
        UUID.fromString((String) ((Map<String, Object>) fastStructureMap.get("2")).get("id")), 2,
        true, false, false, false, false, false, operatorDynRole, null);
    addFieldToCollection(collectionId, classId,
        UUID.fromString((String) ((Map<String, Object>) fastStructureMap.get("3")).get("id")), 3,
        true, false, false, false, false, false, operatorDynRole, null);
    createAndSaveStructureCache(classId, currentDynamicRole.getId(), collectionId, false,
        getCollectionCaseObject(keyCollectionCase, classId, currentDynamicRole.getId(),
            collectionId));
    collectionDynamicRoleRepositoryImplemented
        .addCollectionDynamicRole(new CollectionDynamicRoleModel(UUID.randomUUID(), collectionId, 1,
            classId, currentDynamicRole, getCollectionCaseObject(keyCollectionCase, classId,
                currentDynamicRole.getId(), collectionId)));
    return fastStructureRepository.getActiveFastStructuresForClassByDynamicRole(
        currentDynamicRole.getId(), classId, keyCollectionCase).getFastStructureJSON();
  }

  private String addFieldToCollection(UUID collectionId, UUID classId, UUID fieldId, int turn,
      boolean useful, boolean visible, boolean edit, boolean delete, boolean insert,
      boolean isAdmin, DynamicRoleModelAbstract operatorDynRole, String keyCollectionCase) {
    // int allowInt = turn > 3 || isAdmin ? turn : 4;
    StructureCollectionAbstract currentCollectionField = structureCollectionsFieldsRepository
        .getStructureCollectionFieldByKey(collectionId, classId, fieldId);
    if (currentCollectionField != null) {
      return updateFieldInCollection(collectionId, classId, fieldId, turn, useful, visible, edit,
          delete, insert, operatorDynRole, keyCollectionCase);
    }
    structureCollectionsFieldsRepository
        .addStructureFields(new StructureCollectionImplemented(collectionId, classId, fieldId,
            getMaxTurn(collectionId, classId), useful, visible, edit, delete, insert, null));
    return null;
  }

  public String updateFieldInCollectionAndStructure(UUID systemContactId, UUID collectionId,
      UUID classId, UUID fieldId, String name, UUID classType, byte innerType, String showName,
      int turn, String permission, DynamicRoleModelAbstract operatorDynRole, boolean isActive,
      String keyCollectionCase) {

    int permissionInt = Integer.parseInt(permission);
    boolean useful = PermissionHandler.isUseful(permissionInt);
    boolean visible = PermissionHandler.isVisible(permissionInt);
    boolean edit = PermissionHandler.isEdit(permissionInt);
    boolean delete = PermissionHandler.isDelete(permissionInt);
    boolean insert = PermissionHandler.isInsert(permissionInt);


    // check permission for updator
    if (!authorizeStructureOperationsService.canEditStructure(operatorDynRole))
      return "Do not have permissions.";
    // if updator can change structure:

    String errorMessage = null;
    errorMessage = authorizeStructureOperationsService.checkTheFieldIsUpdatable(classId,
        operatorDynRole.getRoleId(), fieldId, name, innerType, classType, showName, false);
    CollectionDynamicRoleModelAbstract workCollection = collectionDynamicRoleRepositoryImplemented
        .getCollectionDynamicRoleByCollectionId(collectionId);
    if (workCollection == null) {
      return "Not found collections for id.";
    }
    DynamicRoleModelAbstract collectionDynamicRole = workCollection.getDynamicRole();
    if (!authorizeStructureOperationsService.checkCompanyLocation(operatorDynRole,
        collectionDynamicRole))
      return "You can't work with another company collections.";
    if (errorMessage != null)
      return errorMessage;

    StructureFieldAbstract presentField = structureFieldRepository.getStructureFieldsById(fieldId);

    if (presentField == null) {
      return "Field not found.";
      // return addStructureField(fieldId, classId, name, classType, innerType, showName);
    }
    errorMessage = updateFieldToDataTable(classId, fieldId, presentField.getDataClass(), classType,
        "description");
    if (errorMessage == null)
      structureFieldRepository.updateStructureFields(
          new StructureFieldImplemented(fieldId, classId, name, classType, innerType, showName));

    DynamicRoleModelAbstract sudoDynRole = getSUDODynamicRole();
    List<CollectionDynamicRoleModelAbstract> collections =
        collectionDynamicRoleRepositoryImplemented.getCollectionDynamicRoleByclassId(classId);
    // collectionDynamicRoleRepositoryImplemented
    // .getCollectionDynamicRoleByDroleClass(sudoDynRole.getId(), classId, 1);
    if (collections == null || collections.isEmpty()) {
      collectionDynamicRoleRepositoryImplemented
          .addCollectionDynamicRole(new CollectionDynamicRoleModel(UUID.randomUUID(),
              UUID.randomUUID(), 1, classId, sudoDynRole, getCollectionCaseObject(keyCollectionCase,
                  classId, sudoDynRole.getId(), collectionId)));
      collections = collectionDynamicRoleRepositoryImplemented
          .getCollectionDynamicRoleByDroleClass(sudoDynRole.getId(), classId, 1);
      if (collections == null || collections.isEmpty())
        return "not found collections for superadmin";
      else
        log.info("found new collection: {}", collections.get(0));
    }

    // and update collection
    errorMessage = updateFieldInCollection(collectionId, classId, fieldId, turn, useful, visible,
        edit, delete, insert, operatorDynRole, keyCollectionCase);

    collections.forEach(collection -> {
      log.info("structure service try update collection: {}", collection.getCollectionId());
      UUID currentCollectionId = collection.getCollectionId();
      createAndSaveStructureCache(classId, collection.getDynamicRole().getId(), currentCollectionId,
          isActive,
          getCollectionCaseObject(keyCollectionCase, classId, sudoDynRole.getId(), collectionId));
    });
    return errorMessage;
  }

  private String updateFieldInCollection(UUID collectionId, UUID classId, UUID fieldId, int turn,
      boolean useful, boolean visible, boolean edit, boolean delete, boolean insert,
      DynamicRoleModelAbstract operatorDynRole, String keyCollectionCase) {
    int allowInt = turn > 3 ? turn : 4;
    StructureCollectionAbstract currentCollectionField = structureCollectionsFieldsRepository
        .getStructureCollectionFieldByKey(collectionId, classId, fieldId);
    if (currentCollectionField == null) {
      return addFieldToCollection(collectionId, classId, fieldId, allowInt, useful, visible, edit,
          delete, insert, false, operatorDynRole, keyCollectionCase);
    }
    if (currentCollectionField.getStructureField().getFieldName().equals("id"))
      allowInt = 0;
    else if (currentCollectionField.getStructureField().getFieldName().equals("owner"))
      allowInt = 1;
    else if (currentCollectionField.getStructureField().getFieldName().equals("date_create"))
      allowInt = 2;
    else if (currentCollectionField.getStructureField().getFieldName().equals("date_change"))
      allowInt = 3;
    if (!checkAdminCollection(currentCollectionField, operatorDynRole, useful, visible, edit,
        delete, insert)) {
      log.info("too low permissions for dyn role: {}", operatorDynRole);
      return "Permissions rights is too low.";
    }
    List<StructureCollectionAbstract> presents =
        structureCollectionsFieldsRepository.selectOrderedFields(collectionId, classId);

    StructureCollectionAbstract element = new StructureCollectionImplemented(collectionId, classId,
        fieldId, allowInt, useful, visible, edit, delete, insert, null);
    log.info("element insert to db: {}", element);
    presents = (ArrayList<StructureCollectionAbstract>) presents.stream()
        .filter(colElement -> !colElement.getFieldId().equals(fieldId))
        .collect(Collectors.toList());
    CollectionDynamicRoleModelAbstract collection = collectionDynamicRoleRepositoryImplemented
        .getCollectionDynamicRoleByCollectionId(collectionId);
    if (collection == null) {
      return "Not found collections for id.";
    }
    DynamicRoleModelAbstract collectionDynamicRole = collection.getDynamicRole();
    if (collectionDynamicRole == null || operatorDynRole == null) {
      return "Not found dynamic role";
    }
    // check useful
    if (!useful) {
      if ((operatorDynRole.getRoleId().equals(SystemRoles.SUPERADMIN_ROLE.getUUID())
          && collectionDynamicRole.getRoleId().equals(SystemRoles.SUPERADMIN_ROLE.getUUID()))
          || (operatorDynRole.getRoleId().equals(SystemRoles.LOCAL_ADMIN_ROLE.getUUID())
              && collectionDynamicRole.getRoleId()
                  .equals(SystemRoles.LOCAL_ADMIN_ROLE.getUUID()))) {
        presents.add(allowInt, element);
      } else {
        structureCollectionsFieldsRepository.removeStructureField(collectionId, classId, fieldId);
      }
    } else
      presents.add(allowInt, element);
    updateOrderedElements(presents);
    processUpdateDependsForAdminCollections(operatorDynRole, collectionDynamicRole, element,
        collection.getActive() == 1);
    createAndSaveStructureCache(classId, collection.getDynamicRole().getId(), collectionId,
        collection.getActive() == 1, getCollectionCaseObject(keyCollectionCase, classId,
            collection.getDynamicRole().getId(), collectionId));
    return null;
  }

  private String removeFieldFromCollectionIfStructureExists(UUID collectionId, UUID classId,
      UUID fieldId, DynamicRoleModelAbstract operatorDynRole) {
    CollectionDynamicRoleModelAbstract collection = collectionDynamicRoleRepositoryImplemented
        .getCollectionDynamicRoleByCollectionId(collectionId);
    if (collection == null) {
      return "Not found collections for id.";
    }
    DynamicRoleModelAbstract collectionDynamicRole = collection.getDynamicRole();
    if (collectionDynamicRole == null || operatorDynRole == null) {
      return "Not found dynamic role";
    }
    if ((!operatorDynRole.getRoleId().equals(SystemRoles.SUPERADMIN_ROLE.getUUID())
        && !operatorDynRole.getRoleId().equals(SystemRoles.LOCAL_ADMIN_ROLE.getUUID()))
        || (operatorDynRole.getRoleId().equals(SystemRoles.LOCAL_ADMIN_ROLE.getUUID())
            && collectionDynamicRole.getRoleId().equals(SystemRoles.SUPERADMIN_ROLE.getUUID()))) {
      return "You don't have permissions.";
    }
    if ((operatorDynRole.getRoleId().equals(SystemRoles.LOCAL_ADMIN_ROLE.getUUID())
        && collectionDynamicRole.getRoleId().equals(SystemRoles.LOCAL_ADMIN_ROLE.getUUID()))) {
      updateFieldInCollection(collectionId, classId, fieldId, getMaxTurn(collectionId, classId),
          false, false, false, false, false, operatorDynRole,
          collection.getCollectionCase().getMetaKey());
    } else
      structureCollectionsFieldsRepository.removeStructureField(collectionId, classId, fieldId);
    List<StructureCollectionAbstract> presents =
        structureCollectionsFieldsRepository.selectOrderedFields(collectionId, classId);
    updateOrderedElements(presents);
    createAndSaveStructureCache(classId, operatorDynRole.getId(), collection.getCollectionId(),
        collection.getActive() == 1, collection.getCollectionCase());
    return null;
  }

  private int getMaxTurn(UUID id, UUID classId) {
    List<StructureCollectionAbstract> presents =
        structureCollectionsFieldsRepository.selectOrderedFields(id, classId);
    if (presents == null) {
      return 0;
    }
    return presents.size();
  }

  private String updateOrderedElements(List<StructureCollectionAbstract> presents) {
    int[] increment = {0};
    presents.forEach(item -> {
      item.setTurn(increment[0]);
      structureCollectionsFieldsRepository.updateStructureFields(item);
      increment[0]++;
    });
    return null;
  }

  // work with structure fields
  public String addStructureField(UUID systemContactId, UUID fieldId, UUID classId, String extName,
      UUID classType, byte innerType, String extShowName,
      DynamicRoleModelAbstract operatorDynRole) {
    if (!authorizeStructureOperationsService.canInsertStructure(operatorDynRole))
      return "Do not have permissions.";

    String name = (String) SQLProtection.protectStringTrimLower(extName);
    String showName = (String) SQLProtection.protectRequestObject(extShowName);
    CustomerClassModelAbstract presentedClass =
        customerClassRepositoryImplemented.getById(classType);
    if (presentedClass == null) {
      return "Not found class for type id.";
    }
    StructureFieldAbstract presentField = structureFieldRepository.getStructureFieldsById(fieldId);
    log.info("presentedField: {}", presentField);
    if (presentField != null) {
      return "The field is present yet.";
    }
    presentField = structureFieldRepository.getStructureFieldsByName(classId, name);
    log.info("with name field: {}", presentField);
    if (presentField != null && !presentField.getId().equals(fieldId)) {
      return "Class with same name is present yet.";
    }
    addFieldToDataTable(systemContactId, classId, fieldId, classType, "description");
    structureFieldRepository.addStructureFields(
        new StructureFieldImplemented(fieldId, classId, name, classType, innerType, showName));
    DynamicRoleModelAbstract sudoDynRole = getSUDODynamicRole();
    List<CollectionDynamicRoleModelAbstract> collections =
        collectionDynamicRoleRepositoryImplemented
            .getCollectionDynamicRoleByDroleClass(sudoDynRole.getId(), classId, 1);
    if (collections == null || collections.isEmpty()) {
      UUID newCollectionId = UUID.randomUUID();
      collectionDynamicRoleRepositoryImplemented.addCollectionDynamicRole(
          new CollectionDynamicRoleModel(UUID.randomUUID(), newCollectionId, 1, classId,
              sudoDynRole, getCollectionCaseObject(null, null, null, null)));
      collections = collectionDynamicRoleRepositoryImplemented
          .getCollectionDynamicRoleByDroleClass(sudoDynRole.getId(), classId, 1);
      if (collections == null || collections.isEmpty())
        return "not found collections for superadmin";
      else
        log.info("found new collection: {}", collections.get(0));
    }
    collections.forEach(collection -> {
      UUID adminCollectionField = collection.getCollectionId();
      addFieldToCollection(adminCollectionField, classId, fieldId, innerType, true, true, true,
          true, true, true, operatorDynRole, collection.getCollectionCase().getMetaKey());
      createAndSaveStructureCache(classId, sudoDynRole.getId(), adminCollectionField,
          collection.getActive() == 1, collection.getCollectionCase());
    });
    return null;
  }

  public String removeStructureField(UUID systemContactId, UUID classId, UUID fieldId,
      DynamicRoleModelAbstract operatorDynRole) throws NullPointerException {
    String errorMessage = authorizeStructureOperationsService.checkTheFieldIsUpdatable(classId,
        operatorDynRole.getRoleId(), fieldId, null, (byte) -1, null, null, true);

    // delete inner records from belong and implemented tables
    Map<String, Object> fieldDescription =
        cacheMainParams.getFieldDescriptionByFieldIdRecursively(systemContactId, classId, fieldId);
    innerRecordsDynamicClassService.deleteAllInnerRecordsBeforeDeleteField(classId,
        UUID.fromString((String) fieldDescription.get(StructrueCollectionEnum.OBJECT.toString())),
        fieldId);
    if (errorMessage == null)
      structureFieldRepository.removeStructureField(fieldId);
    else {
      log.info("error: {}", errorMessage);
      throw new NullPointerException("Cant delete value from structure.");
    }

    removeFieldFromDataTable(classId, fieldId);

    // get all collections
    List<CollectionDynamicRoleModelAbstract> collections =
        collectionDynamicRoleRepositoryImplemented.getCollectionDynamicRoleByclassId(classId);
    collections.forEach(collection -> {
      structureCollectionsFieldsRepository.removeStructureField(collection.getCollectionId(),
          classId, fieldId);
      List<StructureCollectionAbstract> presents = structureCollectionsFieldsRepository
          .selectOrderedFields(collection.getCollectionId(), classId);
      updateOrderedElements(presents);
      createAndSaveStructureCache(classId, operatorDynRole.getId(), collection.getCollectionId(),
          collection.getActive() == 1, collection.getCollectionCase());
    });
    return errorMessage;
  }

  public String addFieldToDataTable(UUID systemContactId, UUID classId, UUID fieldId, UUID dataType,
      String description) throws NullPointerException {

    UUID realDataType = dataType;
    CustomerClassModelAbstract customClass =
        customerClassRepositoryImplemented.getById(realDataType);
    if (customClass != null && customClass.getType() == 1) {
      // check permission
      Map<String, Object> permissions =
          authorizeStructureOperationsService.checkAccessToClass(systemContactId, realDataType);
      if (permissions == null)
        throw new NullPointerException(
            "Access denied. Permissions for work with custom class are not enough.");
      else
        realDataType = PrimitiveCustomClass.CUUID.getUuid();
    }
    dynamicTablesService.addNewField(classId, fieldId,
        PrimitiveCustomClass.getTableDataType(PrimitiveCustomClass.getById(realDataType)), null,
        false);
    return null;
  }

  public String updateFieldToDataTable(UUID classId, UUID fieldId, UUID oldDataType,
      UUID newDataType, String description) {
    if (oldDataType.equals(newDataType)) {
      return null;
    }
    PrimitiveCasePermission resultChange =
        PrimitiveCustomClass.canChangeToClass(oldDataType, newDataType);
    if (resultChange.equals(PrimitiveCasePermission.NO)) {
      return "Can't change type for this field";
    }
    if (resultChange.equals(PrimitiveCasePermission.YES)
        || resultChange.equals(PrimitiveCasePermission.YES_CASE)) {
      dynamicTablesService.updateFieldType(classId, fieldId,
          PrimitiveCustomClass.getTableDataType(PrimitiveCustomClass.getById(newDataType)), null,
          false);
      return null;
    }
    if (resultChange.equals(PrimitiveCasePermission.YES_OPERATION)) {
      return "The operation for change data not found";
    }
    return "Not found operation";
  }

  public String removeFieldFromDataTable(UUID classId, UUID fieldId) {
    dynamicTablesService.removeFieldType(classId, fieldId);
    return null;
  }

  // dynamic role operations
  private DynamicRoleModelAbstract getSuperAdminDynamicRole(UUID classId,
      DynamicRoleModelAbstract operatorDynamicRole) {
    if (operatorDynamicRole == null) {
      return null;
    }
    if (operatorDynamicRole.getRoleId().equals(SystemRoles.SUPERADMIN_ROLE.getUUID())) {
      return operatorDynamicRole;
    }
    DynamicRoleModelAbstract superAdminDynamicRole =
        dynamicRoleRepository.getDynamicRoleByCompanyServiceRole(operatorDynamicRole.getCompanyId(),
            operatorDynamicRole.getServiceId(), SystemRoles.SUPERADMIN_ROLE.getUUID());
    if (superAdminDynamicRole == null || superAdminDynamicRole.getRoleId() == null) {
      // create new dynamic role for superAdmin
      superAdminDynamicRole =
          addNewDynamicRoleWithCollection(classId, operatorDynamicRole.getCompanyId(),
              operatorDynamicRole.getServiceId(), SystemRoles.SUPERADMIN_ROLE.getUUID(), null);
      // superAdminDynamicRole = new DynamicRoleModel(UUID.randomUUID(),
      // "superadmin " + operatorDynamicRole.getCompanyId(), operatorDynamicRole.getCompanyId(),
      // operatorDynamicRole.getServiceId(), SystemRoles.SUPERADMIN_ROLE.getUUID());
      // dynamicRoleRepository.addDynamicRole(superAdminDynamicRole);
    }
    return superAdminDynamicRole;
  }

  public DynamicRoleModelAbstract addNewDynamicRoleWithCollection(UUID classId, UUID companyId,
      UUID serviceId, UUID roleId, String keyCollectionCase) {
    // check company and get name
    Map<String, Object> conditions = new HashMap<>(2);
    String name = "";
    conditions.put("d700550b-7890-4836-91ae-b52e8a4cde6d", companyId.toString());
    Map<String, Object> fastMap =
        getRealFastStructureMapForObject(UUID.fromString(DBConstants.COMPANY_ID),
            UUID.fromString("62900a19-88a9-4655-a7ac-71488070b659"), keyCollectionCase);
    DynamicClassRecordLineAbstract checkedRecord =
        dynamicClassesRepository.getAdminDynamicClassesRecordByOverOwnerId(
            UUID.fromString(DBConstants.COMPANY_ID), fastMap, conditions, null, false);
    if (checkedRecord == null) {
      throw new NullPointerException("Not found company.");
    }
    name += checkedRecord.getFieldValues().getOrDefault("6235200f-5c07-4aa3-8ead-ff37c2317a4b",
        "not found");
    // check service and get name
    conditions.clear();
    conditions.put("b5afac44-2df9-42b5-88c3-694e63d3dd0a", serviceId.toString());
    fastMap = getRealFastStructureMapForObject(UUID.fromString(DBConstants.SERVICE_ID),
        UUID.fromString("62900a19-88a9-4655-a7ac-71488070b659"), keyCollectionCase);
    checkedRecord = dynamicClassesRepository.getAdminDynamicClassesRecordByOverOwnerId(
        UUID.fromString(DBConstants.SERVICE_ID), fastMap, conditions, null, false);
    if (checkedRecord == null) {
      throw new NullPointerException("Not found service.");
    }
    name += checkedRecord.getFieldValues().getOrDefault("d76789d5-3812-46a1-9a63-2125802b632f",
        "not found");
    // check role and get name
    conditions.clear();
    conditions.put("ea4d5b30-1c60-4bce-a2cd-452e9b075434", roleId.toString());
    fastMap = getRealFastStructureMapForObject(UUID.fromString(DBConstants.ROLE_ID),
        UUID.fromString("62900a19-88a9-4655-a7ac-71488070b659"), keyCollectionCase);
    checkedRecord = dynamicClassesRepository.getAdminDynamicClassesRecordByOverOwnerId(
        UUID.fromString(DBConstants.ROLE_ID), fastMap, conditions, null, false);
    if (checkedRecord == null) {
      throw new NullPointerException("Not found role.");
    }
    name += checkedRecord.getFieldValues().getOrDefault("f5aa4922-2a94-464b-b658-d8893fb8e614",
        "not found");
    // end checks
    DynamicRoleModelAbstract newDynamicRole =
        new DynamicRoleModel(UUID.randomUUID(), name, companyId, serviceId, roleId);
    dynamicRoleRepository.addDynamicRole(newDynamicRole);
    collectionDynamicRoleRepositoryImplemented
        .addCollectionDynamicRole(new CollectionDynamicRoleModel(UUID.randomUUID(),
            UUID.randomUUID(), 1, classId, newDynamicRole, null));
    return newDynamicRole;
  }

  // structure cache
  private String createAndSaveStructureCache(UUID classId, UUID dynamicRoleId, UUID collectionId,
      boolean isActive, CollectionCaseAbstract collectionCase) {
    log.info("try work with values classId: {}, dynamicRoleId: {}, collectionId: {}", classId,
        dynamicRoleId, collectionId);
    // factoryImplemented.setDynamicRoleId(dynamicRoleId.toString());
    // factoryImplemented.setStartclassId(classId.toString());
    // factoryImplemented.setStartCollectionId(collectionId.toString());
    String value = factoryImplemented.getJsonQuery(
        factoryImplemented.createTree(dynamicRoleId, classId, collectionId, collectionCase));
    if (value.length() > 2) {
      FastStructureModelAbstract fastStructure = new FastStructureModel(dynamicRoleId, collectionId,
          classId, value.toString(), isActive, collectionCase);
      List<FastStructureModelAbstract> searchResult =
          fastStructureRepository.getFastStructuresForDynamicRoleAndCollection(
              fastStructure.getDynamicRoleId(), fastStructure.getCollectionId());
      if (searchResult != null && !searchResult.isEmpty()) {
        log.info("udate fast structure: {}", fastStructure);
        fastStructureRepository.updateFastStructure(fastStructure);
      } else {
        log.info("add new fast structure: {}", fastStructure);
        fastStructureRepository.addFastStructure(fastStructure);
      }
    }
    return null;
  }

  private DynamicRoleModelAbstract getSUDODynamicRole() {
    return new DynamicRoleModel(UUID.fromString("62900a19-88a9-4655-a7ac-71488070b659"),
        "superadmin", UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b"),
        UUID.fromString("b56b99b6-2c6f-4103-849a-e914e8594869"),
        UUID.fromString("1d021b86-41c6-47c1-a38e-0aa89b98dc28"));
  }

  public String getFastStructureWithUpdated(UUID classId, UUID companyId, UUID serviceId,
      UUID roleId) {
    if (!fastStructureRepository.checkActiveFastStructuresForClassByCompanyServiceRole(classId,
        companyId, serviceId, roleId)) {
      DynamicRoleModelAbstract currentDynamicRole =
          dynamicRoleRepository.getDynamicRoleByCompanyServiceRole(companyId, serviceId, roleId);
      return createAndSaveStructureCache(classId, currentDynamicRole.getId(), UUID.randomUUID(),
          false, getCollectionCaseObject(null, null, null, null));
    }
    return null;
  }

  public Map<String, Object> getRealFastStructureMapForObject(UUID currentclassId, UUID dynamicRole,
      String keyCollectionCase) {
    FastStructureModelAbstract fastStructure =
        fastStructureRepository.getActiveFastStructuresForClassByDynamicRole(dynamicRole,
            currentclassId, keyCollectionCase);

    if (fastStructure == null)
      return null;
    else {
      try {
        return objectMapper.readValue(fastStructure.getFastStructureJSON(),
            new TypeReference<Map<String, Object>>() {});
      } catch (IOException e) {
        return null;
      }
    }
  }

  private CollectionCaseAbstract getCollectionCaseObject(String keyCollectionCase, UUID classId,
      UUID currentDynamicRoleId, UUID collectionId) {
    return keyCollectionCase == null ? new CollectionCase()
        : new CollectionCase(keyCollectionCase, classId, currentDynamicRoleId, collectionId);
  }

  private FastStructureModelAbstract getFastStructureModelByFieldId(UUID classId,
      UUID dynamicRoleId, UUID fieldId, FastStructureModelAbstract presentStructure,
      Map<String, Object> currentFastStructureMap, String keyCollectionCase) {
    return cacheMainParams.getStructureCollectionByFieldId(classId, dynamicRoleId, fieldId,
        presentStructure, currentFastStructureMap, keyCollectionCase);
  }

  /**
   * Search for collections, which includes the desired, depending on the role.
   * 
   * @return
   */
  private List<FastStructureModelAbstract> getFastStructuresWhichIncludesOne(UUID includesClassId,
      DynamicRoleModelAbstract includesDynamicRole, boolean isStructureUpdate) {
    List<DynamicRoleModelAbstract> whiteList = null;
    List<DynamicRoleModelAbstract> blackList = null;
    if (isStructureUpdate) {
      // update all structures

    } else
    // update process depends on dynamic role
    if (includesDynamicRole.getRoleId().equals(SystemRoles.SUPERADMIN_ROLE.getUUID())) {

    } else if (includesDynamicRole.getRoleId().equals(SystemRoles.LOCAL_ADMIN_ROLE.getUUID())) {
      blackList = new ArrayList<>(2);
      blackList.add(dynamicRoleRepository.getDynamicRoleByCompanyServiceRole(
          includesDynamicRole.getCompanyId(), includesDynamicRole.getServiceId(),
          SystemRoles.SUPERADMIN_ROLE.getUUID()));
      blackList.add(includesDynamicRole);
    }
    return fastStructureRepository.getAllWhichIncludesClassAndRole(includesClassId, whiteList,
        blackList);
  }

  private List<FastStructureModelAbstract> getDependsStructures(UUID forClassId, UUID companyId,
      UUID serviceId, DynamicRoleModelAbstract includesDynamicRole, boolean isStructureUpdate) {
    List<DynamicRoleModelAbstract> whiteList = null;
    List<DynamicRoleModelAbstract> blackList = null;
    if (isStructureUpdate) {

    } else
    // update process depends on dynamic role
    if (includesDynamicRole.getRoleId().equals(SystemRoles.SUPERADMIN_ROLE.getUUID())) {

    } else if (includesDynamicRole.getRoleId().equals(SystemRoles.LOCAL_ADMIN_ROLE.getUUID())) {
      blackList = new ArrayList<>(2);
      blackList.add(dynamicRoleRepository.getDynamicRoleByCompanyServiceRole(companyId, serviceId,
          SystemRoles.SUPERADMIN_ROLE.getUUID()));
      blackList.add(includesDynamicRole);
    }
    return fastStructureRepository.getAllDependsStructures(forClassId, companyId, serviceId,
        isStructureUpdate, whiteList, blackList);
  }

  private boolean checkAdminCollection(StructureCollectionAbstract currentCollectionField,
      DynamicRoleModelAbstract operatorDynamicRole, boolean useful, boolean visible, boolean edit,
      boolean delete, boolean insert) {
    log.info("checkAdminCollection dynrole: {}", operatorDynamicRole);
    if (operatorDynamicRole.getRoleId().equals(SystemRoles.SUPERADMIN_ROLE.getUUID())) {
      return true;
    }
    if (operatorDynamicRole.getRoleId().equals(SystemRoles.LOCAL_ADMIN_ROLE.getUUID())) {
      CollectionDynamicRoleModelAbstract operatorCollection =
          collectionDynamicRoleRepositoryImplemented.getCollectionDynamicRoleByCompanyServiceRole(
              currentCollectionField.getClassId(), operatorDynamicRole.getCompanyId(),
              operatorDynamicRole.getServiceId(), operatorDynamicRole.getRoleId(), 1);
      // can't use admin collection for change
      if (operatorCollection == null
          || currentCollectionField.getId().equals(operatorCollection.getCollectionId())) {
        return false;
      } else {
        StructureCollectionAbstract operatorCollectionField = structureCollectionsFieldsRepository
            .getStructureCollectionFieldByKey(operatorCollection.getCollectionId(),
                currentCollectionField.getClassId(), currentCollectionField.getFieldId());
        if (operatorCollectionField == null) {
          return false;
        }
        if (!operatorCollectionField.isUseful() && operatorCollectionField.isUseful() != useful)
          return false;
        if (!operatorCollectionField.isVisible() && operatorCollectionField.isVisible() != visible)
          return false;
        if (!operatorCollectionField.isEdit() && operatorCollectionField.isEdit() != edit)
          return false;
        if (!operatorCollectionField.isDelete() && operatorCollectionField.isDelete() != delete)
          return false;
        if (!operatorCollectionField.isInsert() && operatorCollectionField.isInsert() != insert)
          return false;
      }
    }
    return false;
  }

  private void processUpdateDependsForAdminCollections(DynamicRoleModelAbstract operatorDynRole,
      DynamicRoleModelAbstract collectionDynamicRole, StructureCollectionAbstract newElement,
      boolean isActive) {
    if (operatorDynRole.getRoleId().equals(SystemRoles.SUPERADMIN_ROLE.getUUID())
        && collectionDynamicRole.getRoleId().equals(SystemRoles.LOCAL_ADMIN_ROLE.getUUID())
        && isActive) {
      // start process update
      if (!newElement.isUseful()) {
        structureCollectionsFieldsRepository.updateDependsStructureFields(newElement.getClassId(),
            newElement.getFieldId(), collectionDynamicRole.getCompanyId(), "usef");
      }
      if (!newElement.isVisible()) {
        structureCollectionsFieldsRepository.updateDependsStructureFields(newElement.getClassId(),
            newElement.getFieldId(), collectionDynamicRole.getCompanyId(), "visible");
      }
      if (!newElement.isEdit()) {
        structureCollectionsFieldsRepository.updateDependsStructureFields(newElement.getClassId(),
            newElement.getFieldId(), collectionDynamicRole.getCompanyId(), "edit");
      }
      if (!newElement.isDelete()) {
        structureCollectionsFieldsRepository.updateDependsStructureFields(newElement.getClassId(),
            newElement.getFieldId(), collectionDynamicRole.getCompanyId(), "delete");
      }
      if (!newElement.isInsert()) {
        structureCollectionsFieldsRepository.updateDependsStructureFields(newElement.getClassId(),
            newElement.getFieldId(), collectionDynamicRole.getCompanyId(), "insert");
      }
    }
  }

  public String updateFieldWithCheckIsPresentInStructure(UUID systemContactId, UUID collectionId,
      UUID classId, UUID fieldId, String name, UUID classType, byte innerType, String showName,
      int turn, String permission, DynamicRoleModelAbstract operatorDynamicRole, boolean isActive,
      String keyCollectionCase) {
    Map<String, Object> sudoStructureMap =
        getRealFastStructureMapForObject(classId, getSUDODynamicRole().getId(), keyCollectionCase);
    UUID realClassId = getClassIdForFieldInStructure(classId, fieldId, sudoStructureMap);
    UUID realCollectionId = collectionId;
    if (realClassId == null || !realClassId.equals(classId)) {
      // find new collectionId for new class
      // and start update for it
      log.error("this field is from another class {}", realClassId);
      CollectionDynamicRoleModelAbstract operatorCollection =
          collectionDynamicRoleRepositoryImplemented.getCollectionDynamicRoleByCompanyServiceRole(
              realClassId, operatorDynamicRole.getCompanyId(), operatorDynamicRole.getServiceId(),
              operatorDynamicRole.getRoleId(), 1);
      realCollectionId = operatorCollection.getCollectionId();
      log.error("collection for class {}", realCollectionId);
    }
    // log.info("Find class for incoming data: {}",
    // getFastStructuresWhichIncudesOne(realClassId, operatorDynamicRole, true));
    String resultUpdate = updateFieldInCollectionAndStructure(systemContactId, realCollectionId,
        realClassId, fieldId, name, classType, innerType, showName, turn, permission,
        operatorDynamicRole, isActive, keyCollectionCase);
    if (resultUpdate == null) {
      List<FastStructureModelAbstract> superCollections =
          getFastStructuresWhichIncludesOne(realClassId, operatorDynamicRole, true);
      log.info("Find class for incoming data: {}", superCollections);
      DynamicRoleModelAbstract sudoDynRole = getSUDODynamicRole();
      superCollections.stream().forEach(collection -> {
        log.info("structure service try update super collection: {}", collection.getCollectionId());
        UUID currentCollectionId = collection.getCollectionId();
        createAndSaveStructureCache(classId, collection.getDynamicRoleId(), currentCollectionId,
            isActive,
            getCollectionCaseObject(keyCollectionCase, classId, sudoDynRole.getId(), collectionId));
      });
    }
    return resultUpdate;
  }

  /**
   * get classId for fieldId or return external classId if fieldId is present in high level
   * 
   * @param classId
   * @param fieldId
   * @param localStructure
   * @return
   */
  private UUID getClassIdForFieldInStructure(UUID classId, UUID fieldId,
      Map<String, Object> localStructure) {
    for (Map.Entry<String, Object> entry : localStructure.entrySet()) {
      if (((Map<String, Object>) entry.getValue()).get(StructrueCollectionEnum.ID.toString())
          .toString().equals(fieldId.toString())) {
        return classId;
      }
      if (((Map<String, Object>) entry.getValue()).get(StructrueCollectionEnum.INNER.toString())
          .equals("3")
          && ((Map<String, Object>) entry.getValue())
              .containsKey(StructrueCollectionEnum.NESTED.toString())) {
        UUID result = getClassIdForFieldInStructure(
            UUID.fromString((String) ((Map<String, Object>) entry.getValue())
                .get(StructrueCollectionEnum.OBJECT.toString())),
            fieldId, (Map<String, Object>) ((Map<String, Object>) entry.getValue())
                .get(StructrueCollectionEnum.NESTED.toString()));
        if (result != null)
          return result;
      }
    }
    return null;
  }



}
