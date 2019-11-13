package com.matas.liteconstruct.db.models.innerrecords.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.innerrecords.abstractmodel.InnerRecord;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@AllArgsConstructor
public class InnerRecordImplemented implements InnerRecord {
  
  UUID classId;
  
  UUID implementedId;
  
  UUID recordId;
  
  int turn;
}
