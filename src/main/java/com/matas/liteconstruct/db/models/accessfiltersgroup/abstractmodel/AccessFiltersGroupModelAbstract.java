package com.matas.liteconstruct.db.models.accessfiltersgroup.abstractmodel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.accessfiltersrecord.abstractmodel.AccessFiltersRecordModelAbstract;

public interface AccessFiltersGroupModelAbstract {
	
	UUID getId();
	
	String getName();
	
	UUID getCompanyId();
	
	Map<String, Object> getMapJsonFilters(); 
	
	short getEditAccess();
	
	HashMap<UUID, AccessFiltersRecordModelAbstract> getAllPresentFilters();
}
