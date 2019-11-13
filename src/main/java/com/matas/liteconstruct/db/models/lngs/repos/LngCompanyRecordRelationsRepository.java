package com.matas.liteconstruct.db.models.lngs.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.lngs.abstractmodel.LngCompanyRecordRelation;

public interface LngCompanyRecordRelationsRepository {

  void addLngRecord(LngCompanyRecordRelation lng);

  void removeLngRecordField(UUID classId, UUID lngId, UUID recordId, UUID fieldId);

  void removeLngFullRecordFieldByCompanyAndId(UUID classId, UUID lngId, UUID recordId);

  void removeLngRecordsByCompanyId(UUID companyId);

  void removeLngRecordsByClassId(UUID classId);

  void updateLngRecord(LngCompanyRecordRelation lng);

  LngCompanyRecordRelation getLngRecordByLngRecordField(UUID lngId, UUID recordId, UUID fieldId);

  List<LngCompanyRecordRelation> getLngRecordsByCompanyLngClass(UUID classId, UUID companyId, UUID lngId);

  List<LngCompanyRecordRelation> getLngRecordsByField(UUID fieldId);
  
  List<LngCompanyRecordRelation> getLngRecordsByArray(List<String> recordsList);

}
