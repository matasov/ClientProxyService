package com.matas.liteconstruct.db.models.descriptions.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.descriptions.abstractmodel.DescriptionClassAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DescriptionClass implements DescriptionClassAbstract {
  private UUID fieldId;
  private UUID classId;
  private String description;
  private String table;

}
