package com.matas.liteconstruct.db.models.accessliteral.abstractmodel;

import java.util.UUID;

public interface AccessLiteralModelAbstract {

  UUID getId();

  UUID getPermissionclassId();

  UUID getClassId();

  UUID getFieldId();

  UUID getRelationFieldId();

  UUID getRecordFieldId();

  UUID getRecordFieldValue();

  UUID getCompanyId();

  String getName();

  short getEditAccess();

  short getTypeUse();

}
