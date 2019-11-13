package com.matas.liteconstruct.db.models.structure.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.structure.abstractmodel.StructureFieldAbstract;
import com.matas.liteconstruct.service.management.structure.PrimitiveCustomClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StructureFieldImplemented implements StructureFieldAbstract {

  private UUID id;
  private UUID classId;
  private String fieldName;
  private UUID dataClass;
  private byte innerType;
  private String fieldShowName;

}
