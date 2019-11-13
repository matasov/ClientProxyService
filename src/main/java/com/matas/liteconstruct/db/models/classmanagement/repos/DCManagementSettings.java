package com.matas.liteconstruct.db.models.classmanagement.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.classmanagement.abstractmodel.DynamicClassManagementSettingsAbstract;

public interface DCManagementSettings {
  void addDCManagementSettings(DynamicClassManagementSettingsAbstract dcManagement);

  void removeDCManagementSettings(DynamicClassManagementSettingsAbstract dcManagement);

  void updateDCManagementSettings(DynamicClassManagementSettingsAbstract dcManagement);
  
  void addMetaValueDCManagementSettings(DynamicClassManagementSettingsAbstract dcManagement,
      String metaKey, String metaValue);

  void updateMetaValueDCManagementSettings(DynamicClassManagementSettingsAbstract dcManagement,
      String metaKey, String metaValue);

  DynamicClassManagementSettingsAbstract getDCManagementSettingsById(UUID dcManagementId);

  DynamicClassManagementSettingsAbstract getDCManagementSettingsByDynamicRoleId(UUID dynamicRoleId, UUID classId);

  String getMetaValueForDCManagementSettings(DynamicClassManagementSettingsAbstract dcManagement,
      String metaKey);
  
  Map<String, String> getAllMetaValuesForDCManagementSettings(UUID dcManagementId);

  List<DynamicClassManagementSettingsAbstract> getAllDCManagementSettings();

  List<DynamicClassManagementSettingsAbstract> getAllDCManagementSettingsByCompanyId(
      UUID companyId);
}
