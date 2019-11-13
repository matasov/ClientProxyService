package com.matas.liteconstruct.db.models.security.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
  protected String id;
  protected String login;
  protected String password;
  protected String repeatPassword;
  protected String roleId;
  private Map<String, String> contactExtraData;
  private Map<String, String> companyContactExtraData;

  public ContactAuth getContactAuth(UUID companyId, UUID serviceId, UUID extRoleId) {
    RoleAuth roleRecord =
        new RoleAuth(UUID.randomUUID(), "register", companyId, serviceId, extRoleId);
    return new ContactAuth(UUID.fromString(id), login, password, new ArrayList() {
      {
        add(roleRecord);
      }
    }, contactExtraData, companyContactExtraData);
  }

  public boolean isPasswordRepeated() {
    return password.equals(repeatPassword);
  }


}
