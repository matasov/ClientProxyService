package com.matas.liteconstruct.db.models.streamliterals.model;

import java.util.UUID;

import com.matas.liteconstruct.db.models.streamliterals.abstractmodel.LiteralModelAbstract;

public class LiteralModel implements LiteralModelAbstract {

	private UUID id;

	private UUID classId;

	private UUID fieldId;

	private UUID recordFieldId;
	
	private String recordFieldValue;

	private UUID companyId;

	private String name;

	private short editAccess;

	private short typeUse;
	
	private short typeData;

	private UUID parentclassId;

	private UUID parentFieldId;

	public LiteralModel(UUID id, String name, UUID companyId, UUID classId, UUID fieldId, UUID recordFieldId, String recordFieldValue,
			short typeUse, short typeData, UUID parentclassId, UUID parentFieldId, short editAccess) {
		this.id = id;
		this.classId = classId;
		this.fieldId = fieldId;
		this.recordFieldId = recordFieldId;
		this.recordFieldValue = recordFieldValue;
		this.companyId = companyId;
		this.name = name;
		this.editAccess = editAccess;
		this.typeUse = typeUse;
		this.typeData = typeData;
		this.parentclassId = parentclassId;
		this.parentFieldId = parentFieldId;
	}

	@Override
	public UUID getId() {
		return id;
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
	public UUID getRecordFieldId() {
		return recordFieldId;
	}
	
	@Override
	public String getRecordFieldValue() {
		return recordFieldValue;
	}

	@Override
	public UUID getCompanyId() {
		return companyId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public short getEditAccess() {
		return editAccess;
	}

	@Override
	public short getTypeUse() {
		return typeUse;
	}
	
	@Override
	public short getTypeData() {
		return typeData;
	}

	@Override
	public UUID getParentclassId() {
		return parentclassId;
	}

	@Override
	public UUID getParentFieldId() {
		return parentFieldId;
	}

}
