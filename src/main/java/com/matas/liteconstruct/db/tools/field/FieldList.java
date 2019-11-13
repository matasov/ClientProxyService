package com.matas.liteconstruct.db.tools.field;

import java.util.ArrayList;
import java.util.List;

public abstract class FieldList {

	private List<TableFieldInterface> fields;

	public FieldList() {
		fields = new ArrayList<>();
	}

	public FieldList addNewField(TableFieldInterface newField) {
		fields.add(newField);
		return this;
	}

	public TableFieldInterface getField(int index) {
		if (isEmpty() || fields.size() <= index) {
			return null;
		}
		return fields.get(index);
	}
	
	public boolean isEmpty() {
		if (fields == null || fields.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public List<TableFieldInterface> getFields(){
		return fields;
	}

}
