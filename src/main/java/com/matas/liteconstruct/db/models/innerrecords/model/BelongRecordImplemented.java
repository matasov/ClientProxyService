package com.matas.liteconstruct.db.models.innerrecords.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.innerrecords.abstractmodel.BelongRecord;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@AllArgsConstructor
public class BelongRecordImplemented implements BelongRecord {
  UUID innerClassId;
  UUID implementedId;
  UUID parentClassId;
  UUID fieldId;
}
