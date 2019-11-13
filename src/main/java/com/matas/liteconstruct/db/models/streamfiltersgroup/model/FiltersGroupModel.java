package com.matas.liteconstruct.db.models.streamfiltersgroup.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.matas.liteconstruct.db.models.streamfiltersgroup.abstractmodel.FiltersGroupModelAbstract;
import com.matas.liteconstruct.db.models.streamfiltersrecord.abstractmodel.FiltersRecordModelAbstract;

import lombok.Data;
import lombok.Value;

@Value
public class FiltersGroupModel implements FiltersGroupModelAbstract {

	private UUID id;

	private String name;

	private UUID companyId;
	
	private Map<String, Object> mapJsonFilters;

	private short editAccess;

	private HashMap<UUID, FiltersRecordModelAbstract> allPresentFilters;

	public FiltersGroupModel(UUID id, String name, UUID companyId, Map<String, Object> jsonFiltersMap,
			short editAccessIndex, HashMap<UUID, FiltersRecordModelAbstract> allFiltersInMap) {
		this.id = id;
		this.name = name;
		this.companyId = companyId;
		this.mapJsonFilters = jsonFiltersMap;
		this.editAccess = editAccessIndex;
		this.allPresentFilters = allFiltersInMap;
	}
  
}
