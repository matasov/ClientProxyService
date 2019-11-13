package com.matas.liteconstruct.db.models.lngs.abstractmodel;

import java.util.UUID;

public interface LngCompanyRecordRelation {

  UUID getClassId();

  UUID getCompanyId();

  UUID getLngId();

  UUID getRecordId();

  UUID getFieldId();

  String getValue();

  @Override
  String toString();
}
