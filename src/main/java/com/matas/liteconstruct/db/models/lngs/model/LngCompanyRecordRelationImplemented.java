package com.matas.liteconstruct.db.models.lngs.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.lngs.abstractmodel.LngCompanyRecordRelation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class LngCompanyRecordRelationImplemented implements LngCompanyRecordRelation {

  UUID classId;

  UUID companyId;

  UUID lngId;

  UUID recordId;

  UUID fieldId;

  String value;

  @Override
  public String toString() {
    return String.format(
        "LngCompanyRecordRelation {class: %s, company: %s, lng: %s, record: %s, field: %s, value: %s }",
        classId, companyId, lngId, recordId, fieldId, value);
  }

}
