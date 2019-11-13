package com.matas.liteconstruct.service.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactory;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactoryImplemented;
import com.matas.liteconstruct.db.models.companyrelations.abstractmodel.CompanyRelationsAbstract;
import com.matas.liteconstruct.db.models.companyrelations.repos.CompanyRelationsRepository;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.security.abstractmodel.SystemRoles;
import com.matas.liteconstruct.db.models.structure.abstractmodel.StructureFieldAbstract;
import com.matas.liteconstruct.db.models.structure.repos.StructureFieldsRepository;
import com.matas.liteconstruct.service.dynamic.CacheMainParams;
import com.matas.liteconstruct.service.management.structure.ManagementSettingsPermissionsItems;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthorizeStructureOperationsService {

  private StructureFieldsRepository structureFieldRepository;

  @Autowired
  public void setStructureFieldsRepositoryImplemented(
      StructureFieldsRepository structureFieldRepository) {
    this.structureFieldRepository = structureFieldRepository;
  }

  private CompanyRelationsRepository companyRelationsRepository;

  @Autowired
  public void setCompanyRelationsRepositoryImplemented(
      CompanyRelationsRepository companyRelationsRepository) {
    this.companyRelationsRepository = companyRelationsRepository;
  }

  private AccessRuleQueryFactory accessRuleQueryFactoryImplemented;

  @Autowired
  void setAccessRuleQueryFactoryImplemented(
      AccessRuleQueryFactoryImplemented accessRuleQueryFactoryImplemented) {
    this.accessRuleQueryFactoryImplemented = accessRuleQueryFactoryImplemented;
  }

  private CacheMainParams cacheMainParams;

  @Autowired
  public void setCacheMainParams(CacheMainParams cacheMainParams) {
    this.cacheMainParams = cacheMainParams;
  }

  public enum ServiceField {
    id, owner, date_create, date_change;
  }

  private String[] systemFields = {ServiceField.id.toString(), ServiceField.owner.toString(),
      ServiceField.date_create.toString(), ServiceField.date_change.toString()};

  public Map<String, String> getDefaultManagementSettings(
      DynamicRoleModelAbstract operatorDynRole) {
    Map<String, String> result = new HashMap<>(9);
    result.put(ManagementSettingsPermissionsItems.GET_CLASSES.toString(), "true");
    result.put(ManagementSettingsPermissionsItems.GET_STRUCTURES.toString(), "true");
    result.put(ManagementSettingsPermissionsItems.GET_COLLECTIONS.toString(), "true");
    if (operatorDynRole.getRoleId().equals(SystemRoles.LOCAL_ADMIN_ROLE.getUUID())) {
      result.put(ManagementSettingsPermissionsItems.EDIT_CLASS.toString(), "true");
      result.put(ManagementSettingsPermissionsItems.INSERT_CLASS.toString(), "false");
      result.put(ManagementSettingsPermissionsItems.DELETE_CLASS.toString(), "false");
      result.put(ManagementSettingsPermissionsItems.EDIT_STRUCTURE.toString(), "true");
      result.put(ManagementSettingsPermissionsItems.INSERT_STRUCTURE.toString(), "false");
      result.put(ManagementSettingsPermissionsItems.DELETE_STRUCTURE.toString(), "false");
      result.put(ManagementSettingsPermissionsItems.EDIT_COLLECTIONS.toString(), "true");
      result.put(ManagementSettingsPermissionsItems.INSERT_COLLECTIONS.toString(), "true");
      result.put(ManagementSettingsPermissionsItems.DELETE_COLLECTIONS.toString(), "true");
    } else if (operatorDynRole.getRoleId().equals(SystemRoles.GLOBAL_SUPERVISOR_ROLE.getUUID())) {
      result.put(ManagementSettingsPermissionsItems.EDIT_CLASS.toString(), "false");
      result.put(ManagementSettingsPermissionsItems.INSERT_CLASS.toString(), "false");
      result.put(ManagementSettingsPermissionsItems.DELETE_CLASS.toString(), "false");
      result.put(ManagementSettingsPermissionsItems.EDIT_STRUCTURE.toString(), "false");
      result.put(ManagementSettingsPermissionsItems.INSERT_STRUCTURE.toString(), "false");
      result.put(ManagementSettingsPermissionsItems.DELETE_STRUCTURE.toString(), "false");
      result.put(ManagementSettingsPermissionsItems.EDIT_COLLECTIONS.toString(), "true");
      result.put(ManagementSettingsPermissionsItems.INSERT_COLLECTIONS.toString(), "false");
      result.put(ManagementSettingsPermissionsItems.DELETE_COLLECTIONS.toString(), "false");
    }
    return result;
  }

  public String checkTheFieldIsUpdatable(UUID classId, UUID roleId, UUID fieldId, String name,
      byte innerType, UUID fieldClass, String showName, boolean isForDelete) {
		  log.info("checkTheFieldIsUpdatable try check updatable for classId: {}", classId);
    StructureFieldAbstract fieldValue = structureFieldRepository.getStructureFieldsById(fieldId);
    if (fieldValue == null)
      return "Not found field.";
    if (!classId.equals(fieldValue.getClassId())) {
      return "Wrong class id.";
    }

    if ((isForDelete || (!isForDelete && !name.equals(fieldValue.getFieldName())
        || innerType != fieldValue.getInnerType() || !fieldClass.equals(fieldValue.getDataClass())))
        && Arrays.asList(systemFields).contains(fieldValue.getFieldName())) {
      return "The field is a system";
    }
    return null;
  }

  public boolean checkCompanyLocation(DynamicRoleModelAbstract operatorDynRole,
      DynamicRoleModelAbstract dataCollectionDynamicRole) {
    return checkCompanyLocation(operatorDynRole.getRoleId(), operatorDynRole.getCompanyId(),
        dataCollectionDynamicRole.getCompanyId());
  }

  public boolean checkCompanyLocation(UUID operatorRole, UUID companyOperator,
      UUID companyManipulation) {
    if (!operatorRole.equals(SystemRoles.SUPERADMIN_ROLE.getUUID())
        && !companyOperator.equals(companyManipulation)) {
      CompanyRelationsAbstract presentRecord = companyRelationsRepository
          .getCompanyRelationsByMasterSlaveRole(companyOperator, companyManipulation, operatorRole);
      return presentRecord != null;
    } else
      return true;
  }

  public String checkPermissionsForOperation(DynamicRoleModelAbstract operatorDynRole,
      ManagementSettingsPermissionsItems operationType) {
    log.info("operatorDynRole: {}", operatorDynRole);
    if (!DBConstants.adminsPryorities.contains(operatorDynRole.getRoleId())) {
      return "This is admin mode. exit.";
    }
    if (!checkValue(operatorDynRole, operationType)) {
      return "Operation is denied. " + operationType;
    }
    return null;
  }

  public boolean checkValue(DynamicRoleModelAbstract operatorDynRole,
      ManagementSettingsPermissionsItems item) {
    if (operatorDynRole.getRoleId().equals(SystemRoles.SUPERADMIN_ROLE.getUUID()))
      return true;
    return (getDBManagementSettings(operatorDynRole).containsKey(item.toString())
        && getDBManagementSettings(operatorDynRole).get(item.toString()).equals("true"));
  }

  public Map<String, String> getDBManagementSettings(DynamicRoleModelAbstract operatorDynRole) {
    return getDefaultManagementSettings(operatorDynRole);
  }

  public boolean canEditClass(DynamicRoleModelAbstract operatorDynRole) {
    return checkValue(operatorDynRole, ManagementSettingsPermissionsItems.EDIT_CLASS);
  }

  public boolean canDeleteClass(DynamicRoleModelAbstract operatorDynRole) {
    return checkValue(operatorDynRole, ManagementSettingsPermissionsItems.DELETE_CLASS);
  }

  public boolean canInsertClass(DynamicRoleModelAbstract operatorDynRole) {
    return checkValue(operatorDynRole, ManagementSettingsPermissionsItems.INSERT_CLASS);
  }

  public boolean canEditStructure(DynamicRoleModelAbstract operatorDynRole) {
    return checkValue(operatorDynRole, ManagementSettingsPermissionsItems.EDIT_STRUCTURE);
  }

  public boolean canDeleteStructure(DynamicRoleModelAbstract operatorDynRole) {
    return checkValue(operatorDynRole, ManagementSettingsPermissionsItems.DELETE_STRUCTURE);
  }

  public boolean canInsertStructure(DynamicRoleModelAbstract operatorDynRole) {
    return checkValue(operatorDynRole, ManagementSettingsPermissionsItems.INSERT_STRUCTURE);
  }

  public boolean canEditCollection(DynamicRoleModelAbstract operatorDynRole) {
    return checkValue(operatorDynRole, ManagementSettingsPermissionsItems.EDIT_COLLECTIONS);
  }

  public boolean canDeleteCollection(DynamicRoleModelAbstract operatorDynRole) {
    return checkValue(operatorDynRole, ManagementSettingsPermissionsItems.DELETE_COLLECTIONS);
  }

  public boolean canInsertCollection(DynamicRoleModelAbstract operatorDynRole) {
    return checkValue(operatorDynRole, ManagementSettingsPermissionsItems.INSERT_COLLECTIONS);
  }

  public boolean isAllowWorkWithCollection(DynamicRoleModelAbstract operatorDynRole,
      UUID currentCollectionRoleId) {

    if (operatorDynRole.getRoleId().equals(SystemRoles.SUPERADMIN_ROLE.getUUID()))
      return true;
    if (operatorDynRole.getRoleId().equals(SystemRoles.LOCAL_ADMIN_ROLE.getUUID())
        && !currentCollectionRoleId.equals(SystemRoles.SUPERADMIN_ROLE.getUUID())
        && !currentCollectionRoleId.equals(SystemRoles.GLOBAL_SUPERVISOR_ROLE.getUUID()))
      return true;
    if (operatorDynRole.getRoleId().equals(SystemRoles.GLOBAL_SUPERVISOR_ROLE.getUUID())
        && !currentCollectionRoleId.equals(SystemRoles.SUPERADMIN_ROLE.getUUID()))
      return true;
    if (operatorDynRole.getRoleId().equals(SystemRoles.LOCAL_SUPERVISOR_ROLE.getUUID())
        && !currentCollectionRoleId.equals(SystemRoles.SUPERADMIN_ROLE.getUUID())
        && !currentCollectionRoleId.equals(SystemRoles.LOCAL_ADMIN_ROLE.getUUID())
        && !currentCollectionRoleId.equals(SystemRoles.GLOBAL_SUPERVISOR_ROLE.getUUID()))
      return true;
    return false;
  }

  public Map<String, Object> checkAccessToClass(UUID systemContactId, UUID workCustomClassId) {
    return accessRuleQueryFactoryImplemented.getResultPermissionForAccess(workCustomClassId,
        cacheMainParams.getSystemUserCache(systemContactId).getInstantAccessPermissions());
  }
}


