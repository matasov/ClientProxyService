package com.matas.liteconstruct.db.models.serviceauthorized.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.serviceauthorized.abstractmodel.AuthorizedContactAbstract;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthorozedContact implements AuthorizedContactAbstract {
  
  public enum MapKey{
    PERMISSIONS
  }

  private UUID contactId;
  
  private UUID ownerId;

  private String name;

  private UUID dynamicRoleId;
  private UUID companyId;
  private UUID serviceId;
  private UUID roleId;

  private UUID domainId;
  
  private UUID companyContactClassId;

  public Map<MapKey, Object> getPermissions() {
    Map<MapKey, Object> result = new HashMap<>(1);
    Map<String, Object> permissions = new HashMap<>(5);
    permissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID), contactId.toString());
    permissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID), roleId.toString());
    permissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID), companyId.toString());
    permissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.SERVICE_ID), serviceId.toString());
    permissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID),
        dynamicRoleId.toString());
    permissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DOMAIN_ID), domainId.toString());
    result.put(MapKey.PERMISSIONS, permissions);
    return result;
  }

}
