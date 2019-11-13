package com.matas.liteconstruct.db.models.classowndynamic.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.classowndynamic.abstractmodel.ClassOwnDynamicAbstract;
import lombok.Data;

@Data
public class ClassOwnDynamic implements ClassOwnDynamicAbstract {

  public ClassOwnDynamic(UUID id, UUID classId, Short editAccess, Integer priority,
      Short recordAccess) {
    this.id = id;
    this.classId = classId;
    this.editAccess = editAccess;
    this.priority = priority;
    this.recordAccess = recordAccess;
  }

  private UUID id;
  private UUID classId;
  private short editAccess;
  private int priority;
  private short recordAccess;


}
