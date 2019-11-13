package com.matas.liteconstruct.db.models.structure.repos;

import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.structure.abstractmodel.StructureFieldAbstract;
import com.matas.liteconstruct.db.tools.sqlselectfactory.FieldsFactoryInterface;

/**
 * Repository for defining custom data class fields.
 * 
 * @author engineer
 *
 */
public interface StructureFieldsRepository {
  /**
   * Add new field's data to user data structure table
   * 
   * @param field field's data
   */
  void addStructureFields(StructureFieldAbstract field);

  /**
   * Remove field's data from user data structure table
   * 
   * @param field field's data
   */
  void removeStructureField(UUID fieldId);
  
  void removeStructureFieldsByclassId(UUID classId);

  /**
   * Update field's data in user data structure table
   * 
   * @param field field's data
   */
  void updateStructureFields(StructureFieldAbstract field);

  StructureFieldAbstract getStructureFieldsById(UUID fieldId);
  
  StructureFieldAbstract getStructureFieldsByName(UUID classId, String name);

  Map<UUID, StructureFieldAbstract> queryByStructure(FieldsFactoryInterface fieldsInterface);
}
