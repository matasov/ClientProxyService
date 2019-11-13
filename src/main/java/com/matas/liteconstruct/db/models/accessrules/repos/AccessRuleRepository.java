package com.matas.liteconstruct.db.models.accessrules.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.accessrules.abstractmodel.AccessRuleAbstract;

public interface AccessRuleRepository {
  
  void addAccessRule(AccessRuleAbstract dynamicRole);

  void removeAccessRule(AccessRuleAbstract dynamicRole);

  void updateAccessRule(AccessRuleAbstract dynamicRole);

  AccessRuleAbstract getAccessRuleById(UUID accessRuleID);
  
  AccessRuleAbstract getAccessRuleByIdWithPermission(UUID accessRuleID, String subquery);

  List<AccessRuleAbstract> getForEditAccessRuleForCompanyClassByPermission(UUID companyId,
      UUID classId, int editAccess);
  
  List<AccessRuleAbstract> getForUseAccessRuleForCompanyClassByPermission(UUID companyId,
      UUID classId, short levelAccess);  

}
