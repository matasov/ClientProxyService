package com.matas.liteconstruct.db.models.accessfiltersgroup.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.accessfiltersgroup.abstractmodel.AccessFiltersGroupModelAbstract;
import com.matas.liteconstruct.db.models.accessfiltersrecord.abstractmodel.AccessFiltersRecordModelAbstract;

import lombok.Data;
import lombok.Value;

@Data
public class AccessFiltersGroupModel implements AccessFiltersGroupModelAbstract {

	private UUID id;

	private String name;

	private UUID companyId;
	
	private Map<String, Object> mapJsonFilters;

	private short editAccess;

	private HashMap<UUID, AccessFiltersRecordModelAbstract> allPresentFilters;

	public AccessFiltersGroupModel(UUID id, String name, UUID companyId, Map<String, Object> jsonFiltersMap,
			short editAccessIndex, HashMap<UUID, AccessFiltersRecordModelAbstract> allFiltersInMap) {
		this.id = id;
		this.name = name;
		this.companyId = companyId;
		this.mapJsonFilters = jsonFiltersMap;
		this.editAccess = editAccessIndex;
		this.allPresentFilters = allFiltersInMap;
	}
  
}
