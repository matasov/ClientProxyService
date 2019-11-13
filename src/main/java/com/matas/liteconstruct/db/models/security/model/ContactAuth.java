package com.matas.liteconstruct.db.models.security.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactAuth {
  private UUID id;
  private String login;
  private String password;
  private List<RoleAuth> roles;
  private Map<String, String> contactExtraData;
  private Map<String, String> companyContactExtraData;
}
