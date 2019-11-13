package com.matas.liteconstruct.db.models.dynamicclass.abstractmodel;

import java.util.UUID;

public interface WhereClauseDynamicClassAbstract {
	
	public UUID getClassId();
	
	public String getWhereClause();
}
