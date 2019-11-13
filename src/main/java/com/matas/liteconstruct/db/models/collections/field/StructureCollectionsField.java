package com.matas.liteconstruct.db.models.collections.field;

import com.matas.liteconstruct.db.tools.field.TableFieldInterface;

public class StructureCollectionsField implements TableFieldInterface {

	private String fieldName;

	public StructureCollectionsField(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

}
