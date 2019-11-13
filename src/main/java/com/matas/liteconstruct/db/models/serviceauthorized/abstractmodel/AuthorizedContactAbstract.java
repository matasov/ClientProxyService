package com.matas.liteconstruct.db.models.serviceauthorized.abstractmodel;

import java.util.UUID;
import com.matas.liteconstruct.db.models.serviceauthorized.model.AuthorozedContact.MapKey;
import java.util.Map;


public interface AuthorizedContactAbstract {

  UUID getContactId();
  
  UUID getOwnerId();

  String getName();

  UUID getDynamicRoleId();

  UUID getCompanyId();

  UUID getServiceId();

  UUID getRoleId();

  UUID getDomainId();
  
  UUID getCompanyContactClassId();

  void setContactId(UUID contactId);
  
  void setOwnerId(UUID ownerId);

  void setName(String name);

  void setDynamicRoleId(UUID dynamicRole);

  void setCompanyId(UUID companyId);

  void setServiceId(UUID serviceId);

  void setRoleId(UUID roleId);

  void setDomainId(UUID dynamicRoleId);
  
  void setCompanyContactClassId(UUID companyContactclassId);

  Map<MapKey, Object> getPermissions();
}
