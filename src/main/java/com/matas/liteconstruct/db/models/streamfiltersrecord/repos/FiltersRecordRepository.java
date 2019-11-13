package com.matas.liteconstruct.db.models.streamfiltersrecord.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.matas.liteconstruct.db.models.streamfiltersrecord.abstractmodel.FiltersRecordModelAbstract;

public interface FiltersRecordRepository {
	public void addFiltersRecord(FiltersRecordModelAbstract filterRecord);

	public void removeFiltersRecord(FiltersRecordModelAbstract filterRecord);

	public void updateFiltersRecord(FiltersRecordModelAbstract filterRecord);

	public FiltersRecordModelAbstract getFiltersRecordById(UUID filterRecordId);

	public List<FiltersRecordModelAbstract> getFiltersRecordsByCompanyId(UUID companyId, short editAccess);
	
	public Map<UUID, FiltersRecordModelAbstract> getMapFiltersRecordsByListId(List<Object> listUUID);
}
