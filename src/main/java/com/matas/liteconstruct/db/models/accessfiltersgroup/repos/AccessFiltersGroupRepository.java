package com.matas.liteconstruct.db.models.accessfiltersgroup.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.accessfiltersgroup.abstractmodel.AccessFiltersGroupModelAbstract;

public interface AccessFiltersGroupRepository {
	void addFiltersGroup(AccessFiltersGroupModelAbstract filterGroup);

	void removeFiltersGroup(AccessFiltersGroupModelAbstract filterGroup);

	void updateFiltersGroup(AccessFiltersGroupModelAbstract filterGroup);

	AccessFiltersGroupModelAbstract getFiltersGroupById(UUID filterGroupId);

	List<AccessFiltersGroupModelAbstract> getFiltersGroupsByCompanyIdForData(UUID companyId);
	
	List<AccessFiltersGroupModelAbstract> getFiltersGroupsByCompanyIdForEdit(UUID companyId, short editAccess);
}
