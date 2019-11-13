package com.matas.liteconstruct.db.models.dynamicclass.model;

import java.util.UUID;

import com.matas.liteconstruct.db.models.dynamicclass.abstractmodel.WhereClauseDynamicClassAbstract;

public class WhereClauseDynamicClass implements WhereClauseDynamicClassAbstract {

	private UUID classId;

	private String whereClause;

	public WhereClauseDynamicClass(UUID classId, String whereClause) {
		this.classId = classId;
		this.whereClause = whereClause;
	}

	@Override
	public UUID getClassId() {
		return classId;
	}

	@Override
	public String getWhereClause() {
		return whereClause;
	}

}
