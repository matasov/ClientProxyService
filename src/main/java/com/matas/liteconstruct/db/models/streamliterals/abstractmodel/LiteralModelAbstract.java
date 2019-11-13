package com.matas.liteconstruct.db.models.streamliterals.abstractmodel;

import java.util.UUID;

public interface LiteralModelAbstract {

	public UUID getId();
	
	public UUID getClassId();
	
	public UUID getFieldId();
	
	public UUID getRecordFieldId();
	
	public String getRecordFieldValue();
	
	public UUID getCompanyId();
	
	public String getName();
	
	public short getEditAccess();
	
	public short getTypeUse();
	
	public short getTypeData();
	
	public UUID getParentclassId();
	
	public UUID getParentFieldId();
}
