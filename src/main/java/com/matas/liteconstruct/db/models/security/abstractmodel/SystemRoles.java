package com.matas.liteconstruct.db.models.security.abstractmodel;

import java.util.UUID;
import java.util.stream.Stream;

public enum SystemRoles {
  SUPERADMIN_ROLE("1d021b86-41c6-47c1-a38e-0aa89b98dc28"), GLOBAL_SUPERVISOR_ROLE(
      "2b021b86-41c6-47c1-a38e-0aa89b98dc29"), LOCAL_ADMIN_ROLE(
          "65fe5829-ff9a-4b58-aa76-d8a92eaeee7e"), LOCAL_SUPERVISOR_ROLE(
              "3c021b86-41c6-47c1-a38e-0aa89b98dc30"), SYSTEM_USER(
                  "1c3bf8ff-7235-4400-974e-d7a3b58de566"), REGISTRANT(
                      "45f89ee0-f15f-4657-8f60-4518625e119e"), ANONYMOUS(
                          "69ebe402-022a-4fb1-9472-f16c4b768c26");
  private String value;

  SystemRoles(String value) {
    this.value = value;
  }

  public SystemRoles getById(String roleId) {
    return Stream.of(SystemRoles.values()).filter(x -> x.equals(roleId)).findAny()
        .orElse(ANONYMOUS);
  }

  public SystemRoles getById(UUID roleId) {
    if (roleId == null)
      return ANONYMOUS;
    String roleIdstr = roleId.toString();
    return Stream.of(SystemRoles.values()).filter(x -> x.equals(roleIdstr)).findAny()
        .orElse(ANONYMOUS);
  }

  public String getId() {
    return value;
  }

  public UUID getUUID() {
    return UUID.fromString(value);
  }

}
