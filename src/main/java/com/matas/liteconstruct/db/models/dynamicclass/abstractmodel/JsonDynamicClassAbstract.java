package com.matas.liteconstruct.db.models.dynamicclass.abstractmodel;

import java.util.Map;
import java.util.UUID;

public interface JsonDynamicClassAbstract {

	public Map<String, Object> getStructureFields();

	public Map<String, Object> getStructureFieldByIndex(int fieldIndex);
	
	public Map<String, Object> getStructureFieldByFieldId(UUID fieldId);
}
