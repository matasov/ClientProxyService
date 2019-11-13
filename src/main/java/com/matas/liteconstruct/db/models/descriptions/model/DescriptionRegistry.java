package com.matas.liteconstruct.db.models.descriptions.model;

import com.matas.liteconstruct.db.models.descriptions.abstractmodel.DescriptionRegistryAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DescriptionRegistry implements DescriptionRegistryAbstract {
  private String table;
  private String description;
}
