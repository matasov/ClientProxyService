package com.matas.liteconstruct.db.models.streamfiltersrecord.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.streamfiltersrecord.abstractmodel.FiltersRecordModelAbstract;
import com.matas.liteconstruct.db.models.streamliterals.abstractmodel.LiteralModelAbstract;
import lombok.Data;

@Data
public class FiltersRecordModel implements FiltersRecordModelAbstract {

  private UUID id;

  private String name;

  private UUID companyId;

  private UUID structureLiteralId;

  private short operator;

  private String complexDataValue;

  private short editAccess;

  private LiteralModelAbstract structureLiteralModel;

  private LiteralModelAbstract valueLiteralModel;

  public FiltersRecordModel(UUID id, String name, UUID companyId,
      UUID structureLiteralId, short operatorIndex, String valueLiteralId, short editAccessIndex,
      LiteralModelAbstract structureLiteralModel, LiteralModelAbstract valueLiteralModel) {
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
