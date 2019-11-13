package com.matas.liteconstruct.db.models.collections.repos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.matas.liteconstruct.db.models.collections.abstractmodel.StructureCollectionAbstract;
import com.matas.liteconstruct.db.tools.sqlselectfactory.FieldsFactoryInterface;

public interface StructureCollectionsFieldsRepository {
	
	void addStructureFields(StructureCollectionAbstract field);

	/**
	 * Remove field's data from user data structure collections table
	 * 
	 * @param field field's data
	 */
	void removeStructureField(UUID id, UUID classId, UUID fieldId);
	
	void removeStructureFieldsByclassId(UUID classId);

	/**
	 * Update field's data in user data structure collections table
	 * 
	 * @param field field's data
	 */
	void updateStructureFields(StructureCollectionAbstract field);
	
	StructureCollectionAbstract getStructureCollectionFieldByKey(UUID id, UUID classId, UUID fieldId);
	
	void updateDependsStructureFields(UUID classId, UUID fieldId, UUID companyId,
	      String fieldPermissionsName);
	
	List<StructureCollectionAbstract> selectOrderedFields(UUID id, UUID classId);

	List<StructureCollectionAbstract> queryByStructure(FieldsFactoryInterface fieldsInterface);
}
