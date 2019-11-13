package com.matas.liteconstruct.db.models.sortfactory.abstractmodel;

import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

public interface SortingNodeAbstract {

	UUID getClassId();
	
	UUID getFieldId();
	
	UUID getParentclassId();
	
	UUID getParentFieldId();
	
	String getFieldForSortingSubquery(UUID requestclassId, UUID parentclassId, UUID parentFieldId);
	
	String getTransitFieldNameForSortingSubquery(UUID requestclassId, UUID parentclassId, UUID parentFieldId);
	
	String getFastKey();
	
	String getDirectionString();
	
	String[] getOnlyFieldForSortingSubquery(UUID requestclassId, UUID parentclassId, UUID parentFieldId);
	
	String[] getGeneralParamsFieldForSubquery();
}
