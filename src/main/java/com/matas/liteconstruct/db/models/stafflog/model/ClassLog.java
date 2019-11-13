package com.matas.liteconstruct.db.models.stafflog.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.stafflog.abstractmodel.ClassLogAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClassLog implements ClassLogAbstract {

  private UUID id;

  private UUID classId;

  private String tableName;

  private UUID recordId;

  private UUID fieldId;

  private String valueOld;

  private String valueNew;

  private long dateChange;

  private UUID dynamicRoleId;

  private UUID dispatcherId;

  private String remoteAddress;

}
