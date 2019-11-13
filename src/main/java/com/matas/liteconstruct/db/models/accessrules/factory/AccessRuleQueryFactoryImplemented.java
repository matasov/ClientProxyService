package com.matas.liteconstruct.db.models.accessrules.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matas.liteconstruct.controller.MainController;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.accessfiltersgroup.factorygroup.AccessFactoryGroupImplemented;
import com.matas.liteconstruct.db.models.accessrules.AccessRuleConstants;
import com.matas.liteconstruct.db.models.accessrules.abstractmodel.AccessRuleAbstract;
import com.matas.liteconstruct.db.models.accessrules.repos.AccessRuleRepository;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepository;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccessRuleQueryFactoryImplemented implements AccessRuleQueryFactory {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MainController.class);

  private AccessRuleRepository accessRuleRepository;

  private AccessFactoryGroupImplemented accessFactoryGroupImplemented;

  @Autowired
  public void setAccessRuleRepositoryImplemented(
      AccessRuleRepository accessRuleRepository) {
    this.accessRuleRepository = accessRuleRepository;
  }

  @Autowired
  public void setAccessFactoryGroupImplemented(
      AccessFactoryGroupImplemented accessFactoryGroupImplemented) {
    this.accessFactoryGroupImplemented = accessFactoryGroupImplemented;
  }

  DynamicRoleRepository dynamicRoleRepository;

  @Autowired
  void setDynamicRoleRepositoryImplemented(DynamicRoleRepository dynamicRoleRepository) {
    this.dynamicRoleRepository = dynamicRoleRepository;
  }

  @Override
  public List<UUID> getListFiltersGroupForSettings(UUID classId, Map<String, Object> settings)
      throws NullPointerException {
    List<AccessRuleAbstract> result =
        accessRuleRepository.getForUseAccessRuleForCompanyClassByPermission(UUID.fromString(
            (String) settings.get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID))),
            classId, (short) 2);
    return result == null ? null
        : result.parallelStream().map(e -> e.getLevelValue()).collect(Collectors.toList());
  }

  @Override
  public Map<String, Object> getResultPermissionForAccess(UUID classId,
      Map<String, Object> permissions) throws IllegalArgumentException {
    LOGGER.info("start getResultPermissionForAccess with: {}, {}", classId, permissions);
    Map<String, Object> result = permissions;
    List<UUID> usedRoles = new ArrayList<>();
    usedRoles.add(UUID.fromString(
        (String) result.get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID))));
    boolean isRoleChanged = false;
    // String roleId =
    // (String) result.get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID));
    do {
      isRoleChanged = false;
      if (!isAccessToClass(classId, result))
        return null;
      AccessRuleAbstract nextRole = changeRole(classId, result);
      if (nextRole != null) {
        isRoleChanged = true;
        if (usedRoles.contains(nextRole.getLevelValue())) {
          isRoleChanged = false;
        } else
          usedRoles.add(nextRole.getLevelValue());
      }
    } while (isRoleChanged);
    if (usedRoles.size() > 1) {
      DynamicRoleModelAbstract newDynamicRole =
          dynamicRoleRepository.getDynamicRoleById(usedRoles.get(usedRoles.size() - 1));
      if (newDynamicRole == null) {
        log.error("error while search access permission: {}", classId);
        throw new IllegalArgumentException(
            "Not found Dynamic role for id: " + usedRoles.get(usedRoles.size() - 1));
      }
      result.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID),
          newDynamicRole.getRoleId().toString());
      result.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID),
          newDynamicRole.getCompanyId().toString());
      result.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.SERVICE_ID),
          newDynamicRole.getServiceId().toString());
      result.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID),
          newDynamicRole.getId().toString());
    }
    log.info("found list access rules: {}", result);
    return result;
  }

  // global access block

  private boolean isAccessToClass(UUID classId, Map<String, Object> permissions) {
    List<AccessRuleAbstract> rulesList =
        accessRuleRepository.getForUseAccessRuleForCompanyClassByPermission(
            UUID.fromString((String) permissions
                .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID))),
            classId, (short) 0);
    if (rulesList == null || rulesList.isEmpty())
      return false;
    List<AccessRuleAbstract> result =
        rulesList.stream().filter(x -> isAllowAccessForCurrentElement(x, permissions)).limit(1)
            .collect(Collectors.toList());
    return result != null && !result.isEmpty();
  }

  private boolean isAllowAccessForCurrentElement(AccessRuleAbstract rule,
      Map<String, Object> requestSettings) {
    String accessSubqueryById =
        getAccessSubqueryByRuleID(rule.getAccessFilterGroupId(), requestSettings);
    LOGGER.info("found subquery: {}", accessSubqueryById);
    if (accessSubqueryById == null)
      return false;
    AccessRuleAbstract checkResult =
        accessRuleRepository.getAccessRuleByIdWithPermission(rule.getId(), accessSubqueryById);
    return checkResult != null
        && checkResult.getLevelValue().equals(AccessRuleConstants.ACCESS_ALLOW_TO_CLASS);
  }

  private boolean isPresentAnyChangeRoleRulesForCurrentElement(AccessRuleAbstract rule,
      Map<String, Object> requestSettings) {
    String accessSubqueryById =
        getAccessSubqueryByRuleID(rule.getAccessFilterGroupId(), requestSettings);
    if (accessSubqueryById == null)
      return false;
    AccessRuleAbstract checkResult =
        accessRuleRepository.getAccessRuleByIdWithPermission(rule.getId(), accessSubqueryById);
    return checkResult != null;
  }

  // change role of incoming contact

  private AccessRuleAbstract changeRole(UUID classId, Map<String, Object> permissions) {
    List<AccessRuleAbstract> rulesList =
        accessRuleRepository.getForUseAccessRuleForCompanyClassByPermission(
            UUID.fromString((String) permissions
                .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID))),
            classId, (short) 1);
    if (rulesList == null || rulesList.isEmpty())
      return null;
    List<AccessRuleAbstract> result =
        rulesList.stream().filter(x -> isPresentAnyChangeRoleRulesForCurrentElement(x, permissions))
            .limit(1).collect(Collectors.toList());
    if (result != null && !result.isEmpty()) {
      return result.get(0);
    } else
      return null;
  }

  private String getAccessSubqueryByRuleID(UUID id, Map<String, Object> requestSettings) {
    accessFactoryGroupImplemented.setFilterGroupId(id);
    return accessFactoryGroupImplemented.getSubquery(requestSettings);
  }

}
