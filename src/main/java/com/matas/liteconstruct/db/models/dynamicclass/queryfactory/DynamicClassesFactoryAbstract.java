package com.matas.liteconstruct.db.models.dynamicclass.queryfactory;

import java.util.Map;
import java.util.UUID;

public interface DynamicClassesFactoryAbstract {
	public void setDynamicClassNewFieldData(UUID classId, UUID dynamicRoleId, UUID collectionId, UUID recordId, UUID fieldId, Object data);
	
	public void getDynamicClassRecordById(UUID classId, UUID dynamicRoleId, UUID collectionId, UUID recordId);
	
	public void removeDynamicClassRecordById(UUID classId, UUID dynamicRoleId, UUID collectionId, UUID recordId);
	
	public void getDynamicClassesByCustomMapQuery(Map<String, Object> request);
}
