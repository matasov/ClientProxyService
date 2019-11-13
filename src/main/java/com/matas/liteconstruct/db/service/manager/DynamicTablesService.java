package com.matas.liteconstruct.db.service.manager;

import java.util.UUID;

public interface DynamicTablesService {

  void addTables(UUID classId, UUID idFieldId, UUID ownerFieldId, UUID createFieldId, UUID changeFieldId);
  
  void removeTables(UUID classId);
  
  void addNewField(UUID classId, UUID fieldId, String typeField, String defaultValue, boolean uniqueToken);
  
  void updateFieldType(UUID classId, UUID fieldId, String newTypeField, String defaultValue, boolean uniqueToken);
  
  void removeFieldType(UUID classId, UUID fieldId);
}
