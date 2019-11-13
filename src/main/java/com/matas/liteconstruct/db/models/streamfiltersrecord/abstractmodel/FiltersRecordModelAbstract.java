package com.matas.liteconstruct.db.models.streamfiltersrecord.abstractmodel;

import java.util.UUID;

import com.matas.liteconstruct.db.models.streamliterals.abstractmodel.LiteralModelAbstract;

public interface FiltersRecordModelAbstract {

	UUID getId();

	String getName();

	UUID getCompanyId();

	UUID getStructureLiteralId();

	short getOperator();

	String getComplexDataValue();

	short getEditAccess();

	LiteralModelAbstract getStructureLiteralModel();

	LiteralModelAbstract getValueLiteralModel();

}
