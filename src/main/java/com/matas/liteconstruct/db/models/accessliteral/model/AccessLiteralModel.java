package com.matas.liteconstruct.db.models.accessliteral.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.accessliteral.abstractmodel.AccessLiteralModelAbstract;
import lombok.Data;

@Data
public class AccessLiteralModel implements AccessLiteralModelAbstract {

  private UUID id;
  private String name;
  private UUID companyId;
  private UUID permissionclassId;
  private UUID classId;
  private UUID fieldId;
  private UUID relationFieldId;
  private UUID recordFieldId;
  private UUID recordFieldValue;
  private short editAccess;
  private short typeUse;

  public AccessLiteralModel(UUID id, String name, UUID companyId, UUID permissionclassId,
      UUID classId, UUID fieldId, UUID relationFieldId, UUID recordFieldId, UUID recordFieldValue, short editAccess,
      short typeUse) {
    this.id = id;
    this.permissionclassId = permissionclassId;
    this.classId = classId;
    this.fieldId = fieldId;
    this.relationFieldId = relationFieldId;
    this.recordFieldId = recordFieldId;
    this.recordFieldValue = recordFieldValue;
    this.name = name;
    this.companyId = companyId;
    this.editAccess = editAccess;
    this.typeUse = typeUse;
  }

}
