package com.matas.liteconstruct.db.models.accessfiltersgroup.abstractmodel;

import java.util.UUID;

public interface AccessFastKeyItemAbstract {

	UUID getParentclassId();
	
	UUID getParentFieldId();
	
	UUID getClassId();
	
	UUID getFieldId();
	
	UUID getRecordId();
	
	String getShortKey();
	
	boolean isEquals(String shortKey);
}
