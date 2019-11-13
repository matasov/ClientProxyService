package com.matas.liteconstruct.db.models.streamfiltersgroup.abstractmodel;

import java.util.UUID;

public interface FastKeyItemAbstract {

	UUID getParentclassId();
	
	UUID getParentFieldId();
	
	UUID getClassId();
	
	UUID getFieldId();
	
	UUID getRecordId();
	
	String getShortKey();
	
	boolean isEquals(String shortKey);
}
