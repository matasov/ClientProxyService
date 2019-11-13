package com.matas.liteconstruct.db.models.innerrecords.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.innerrecords.abstractmodel.BelongRecord;
import com.matas.liteconstruct.db.models.innerrecords.abstractmodel.InnerRecord;

public interface InnerRecordsDynamicClassRepository {

  void addInnerRecord(InnerRecord nestedRecord);

  void addBelongRecord(BelongRecord belongRecord);

  void updateInnerRecord(InnerRecord nestedRecord);

  void updateBelongRecord(BelongRecord belongRecord);
  
  void deleteInnerRecord(UUID classId, UUID implementedId, UUID recordId);
  
  void deleteBelongRecord(UUID implementedId);
  
  List<InnerRecord> getInnerRecordsForImplemented(UUID classId, UUID implementedId);
  
  BelongRecord getBelongRecordForImplemented(UUID innerClassId, UUID implementedId);

  void deleteAllBelongRecordsByClass(UUID classId);
  
  void clearAllInnerRecordsByImplementedId(UUID classId, UUID implementedId);
  
  void clearAllInnerRecordsWhileDeleteFieldId(UUID belongClassId, UUID innerClassId, UUID fieldId);
  
  void clearAllBelongRecordsWhileDeleteFieldId(UUID belongClassId, UUID innerClassId, UUID fieldId);
}
