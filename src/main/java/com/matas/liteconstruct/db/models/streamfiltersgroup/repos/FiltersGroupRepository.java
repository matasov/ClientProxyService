package com.matas.liteconstruct.db.models.streamfiltersgroup.repos;

import java.util.List;
import java.util.UUID;

import com.matas.liteconstruct.db.models.streamfiltersgroup.abstractmodel.FiltersGroupModelAbstract;

public interface FiltersGroupRepository {
	void addFiltersGroup(FiltersGroupModelAbstract filterGroup);

	void removeFiltersGroup(FiltersGroupModelAbstract filterGroup);

	void updateFiltersGroup(FiltersGroupModelAbstract filterGroup);

	FiltersGroupModelAbstract getFiltersGroupById(UUID filterGroupId);

	List<FiltersGroupModelAbstract> getFiltersGroupsByCompanyIdForData(UUID companyId);
	
	List<FiltersGroupModelAbstract> getFiltersGroupsByCompanyIdForEdit(UUID companyId, short forAccess, short editAccess);
}
