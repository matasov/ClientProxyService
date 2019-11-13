package com.matas.liteconstruct.db.models.accessfiltersrecord.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.accessfiltersrecord.abstractmodel.AccessFiltersRecordModelAbstract;

public interface AccessFiltersRecordRepository {
	public void addFiltersRecord(AccessFiltersRecordModelAbstract filterRecord);

	/**
	 * Remove field's data from user data structure collections table
	 * 
	 * @param field field's data
	 */
	public void removeFiltersRecord(AccessFiltersRecordModelAbstract filterRecord);

	/**
	 * Update field's data in user data structure collections table
	 * 
	 * @param field field's data
	 */
	public void updateFiltersRecord(AccessFiltersRecordModelAbstract filterRecord);

	public AccessFiltersRecordModelAbstract getFiltersRecordById(UUID filterRecordId);

	public List<AccessFiltersRecordModelAbstract> getFiltersRecordsByCompanyId(UUID companyId, short editAccess);
	
	public Map<UUID, AccessFiltersRecordModelAbstract> getMapFiltersRecordsByListId(List<Object> listUUID);
}
