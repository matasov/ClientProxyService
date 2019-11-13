package com.matas.liteconstruct.db.models.accessrules.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.accessrules.abstractmodel.AccessRuleAbstract;
import lombok.Data;

@Data
public class AccessRule implements AccessRuleAbstract {

  private UUID id;
  private String name;
  private UUID companyId;
  private UUID classId;
  private UUID accessFilterGroupId;
  private short editAccess;
  private int priority;
  private short levelAccess;
  private UUID levelValue;

  public AccessRule(UUID id, String name, UUID companyId, UUID classId, UUID accessFilterGroupId, short editAccess, int priority,
      short levelAccess, UUID levelValue) {
    this.id = id;
    this.name = name;
    this.companyId = companyId;
    this.classId = classId;
    this.accessFilterGroupId = accessFilterGroupId;
    this.editAccess = editAccess;
    this.priority = priority;
    this.levelAccess = levelAccess;
    this.levelValue = levelValue;
    this.id = id;
  }

}
