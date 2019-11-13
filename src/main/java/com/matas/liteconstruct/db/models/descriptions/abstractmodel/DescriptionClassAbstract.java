package com.matas.liteconstruct.db.models.descriptions.abstractmodel;

import java.util.UUID;

public interface DescriptionClassAbstract {
  UUID getFieldId();
  UUID getClassId();
  String getTable();
  String getDescription();
}
