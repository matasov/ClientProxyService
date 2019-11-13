package com.matas.liteconstruct.db.models.accessfiltersrecord.abstractmodel;

import java.util.UUID;
import com.matas.liteconstruct.db.models.accessliteral.abstractmodel.AccessLiteralModelAbstract;

public interface AccessFiltersRecordModelAbstract {

	UUID getId();

	String getName();

	UUID getCompanyId();

	UUID getStructureLiteralId();

	short getOperator();

	String getComplexDataValue();

	short getEditAccess();

	AccessLiteralModelAbstract getStructureLiteralModel();

	AccessLiteralModelAbstract getValueLiteralModel();

}
