package com.matas.liteconstruct.db.models.recordowner.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.recordowner.abstractmodel.RecordsOwnerClassSettingsAbstract;
import lombok.Data;

@Data
public class RecordsOwnerClassSettings implements RecordsOwnerClassSettingsAbstract {

  private UUID id;

  private UUID classId;

  private short editAccess;

  private int priority;

  private short typeRecordAccess;

  private UUID ownerFieldId;

  public RecordsOwnerClassSettings(UUID id, UUID classId, short editAccess, int priority,
      short typeRecordAccess, UUID ownerFieldId) {
    this.id = id;
    this.classId = classId;
    this.editAccess = editAccess;
    this.priority = priority;
    this.typeRecordAccess = typeRecordAccess;
    this.ownerFieldId = ownerFieldId;
  }

}
