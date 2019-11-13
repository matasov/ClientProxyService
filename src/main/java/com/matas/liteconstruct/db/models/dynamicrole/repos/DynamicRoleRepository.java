package com.matas.liteconstruct.db.models.dynamicrole.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;

public interface DynamicRoleRepository {

  void addDynamicRole(DynamicRoleModelAbstract dynamicRole);

  void removeDynamicRole(DynamicRoleModelAbstract dynamicRole);

  void updateDynamicRole(DynamicRoleModelAbstract dynamicRole);

  DynamicRoleModelAbstract getDynamicRoleById(UUID dynamicRoleId);

  DynamicRoleModelAbstract getDynamicRoleByCompanyServiceRole(UUID company, UUID service,
      UUID role);
  
  List<DynamicRoleModelAbstract> getDynamicRolesByCompanyServiceContact(UUID company,
      UUID service, UUID contact);
  
  List<DynamicRoleModelAbstract> getDynamicRolesByCompanyServiceLogin(UUID company,
      UUID service, String contactLogin);
  
  UUID getContactByLoginPassword(String contactLogin, String contactPassword);
  
  UUID getContactByLogin(String contactLogin);
}
