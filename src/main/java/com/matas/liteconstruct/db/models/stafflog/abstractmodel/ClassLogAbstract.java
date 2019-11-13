package com.matas.liteconstruct.db.models.stafflog.abstractmodel;

import java.util.UUID;

public interface ClassLogAbstract {

  void setId(UUID id);

  UUID getId();

  UUID getClassId();

  String getTableName();

  UUID getRecordId();

  void setFieldId(UUID fieldId);

  UUID getFieldId();

  String getValueOld();

  void setValueNew(String valueNew);

  void setValueOld(String valueOld);

  String getValueNew();

  long getDateChange();

  UUID getDynamicRoleId();

  UUID getDispatcherId();

  String getRemoteAddress();
}
