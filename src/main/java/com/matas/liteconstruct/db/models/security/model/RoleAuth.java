package com.matas.liteconstruct.db.models.security.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleAuth {
  private UUID id;
  private String name;
  private UUID companyId;
  private UUID serviceId;
  private UUID roleId;  
}
