package com.matas.liteconstruct.db.models.streamfiltersgroup.abstractmodel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.matas.liteconstruct.db.models.streamfiltersrecord.abstractmodel.FiltersRecordModelAbstract;

public interface FiltersGroupModelAbstract {
	
	UUID getId();
	
	String getName();
	
	UUID getCompanyId();
	
	Map<String, Object> getMapJsonFilters(); 
	
	short getEditAccess();
	
	HashMap<UUID, FiltersRecordModelAbstract> getAllPresentFilters();
}
