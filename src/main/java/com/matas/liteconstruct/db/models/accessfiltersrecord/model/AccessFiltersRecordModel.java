package com.matas.liteconstruct.db.models.accessfiltersrecord.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.accessfiltersrecord.abstractmodel.AccessFiltersRecordModelAbstract;
import com.matas.liteconstruct.db.models.accessliteral.abstractmodel.AccessLiteralModelAbstract;
import lombok.Data;

@Data
public class AccessFiltersRecordModel implements AccessFiltersRecordModelAbstract {

  private UUID id;

  private String name;

  private UUID companyId;

  private UUID structureLiteralId;

  private short operator;

  private String complexDataValue;

  private short editAccess;

  private AccessLiteralModelAbstract structureLiteralModel;

  private AccessLiteralModelAbstract valueLiteralModel;

  public AccessFiltersRecordModel(UUID id, String name, UUID companyId, UUID structureLiteralId,
      short operatorIndex, String valueLiteralId, short editAccessIndex,
      AccessLiteralModelAbstract structureLiteralModel,
      AccessLiteralModelAbstract valueLiteralModel) {
    this.id = id;
    this.name = name;
    this.companyId = companyId;
    this.structureLiteralId = structureLiteralId;
    this.operator = operatorIndex;
    this.complexDataValue = valueLiteralId;
    this.editAccess = editAccessIndex;
    this.structureLiteralModel = structureLiteralModel;
    this.valueLiteralModel = valueLiteralModel;
  }

}
