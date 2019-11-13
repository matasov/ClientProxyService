package com.matas.liteconstruct.db.models.streamfiltersgroup.model;

import java.util.UUID;

import com.matas.liteconstruct.db.models.streamfiltersgroup.abstractmodel.FastKeyItemAbstract;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;

public class FastKeyItem implements FastKeyItemAbstract {

	private UUID parentclassId;

	private UUID parentFieldId;

	private UUID classId;

	private UUID fieldId;

	private UUID recordId;

	private String shortKey;

	public FastKeyItem(UUID parentclassId, UUID parentFieldId, UUID classId, UUID fieldId, UUID recordId) {
		this.parentclassId = parentclassId;
		this.parentFieldId = parentFieldId;
		this.classId = classId;
		this.fieldId = fieldId;
		this.recordId = recordId;
		shortKey = FactoryGroupAbstract.getFastKey(this.parentclassId, this.parentFieldId, this.classId);
	}

	@Override
	public UUID getParentclassId() {
		return parentclassId;
	}

	@Override
	public UUID getParentFieldId() {
		return parentFieldId;
	}

	@Override
	public UUID getClassId() {
		return classId;
	}

	@Override
	public UUID getFieldId() {
		return fieldId;
	}

	@Override
	public UUID getRecordId() {
		return recordId;
	}

	@Override
	public String getShortKey() {
		return shortKey;
	}

	@Override
	public boolean isEquals(String shortKey) {
		return this.shortKey.equals(shortKey);
	}

}
