package com.matas.liteconstruct.db.models.streamliterals.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.matas.liteconstruct.db.models.streamliterals.abstractmodel.LiteralModelAbstract;

public interface LiteralRepository {
	public void addLiteral(LiteralModelAbstract literal);

	public void removeLiteral(LiteralModelAbstract literal);

	public void updateLiteral(LiteralModelAbstract literal);

	public LiteralModelAbstract getLiteralById(UUID literalId);

	public List<LiteralModelAbstract> getLiteralsByCompanyId(UUID companyId);

	public List<LiteralModelAbstract> getLiteralsByCompanyIdclassId(UUID companyId, UUID classId);
	
	public String getValueFromDataByLiteral(LiteralModelAbstract literal);
}
