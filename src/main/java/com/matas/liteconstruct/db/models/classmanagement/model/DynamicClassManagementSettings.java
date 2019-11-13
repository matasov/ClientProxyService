package com.matas.liteconstruct.db.models.classmanagement.model;

import java.beans.Transient;
import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.classmanagement.abstractmodel.DynamicClassManagementSettingsAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicClassManagementSettings implements DynamicClassManagementSettingsAbstract {

  private UUID managementId;
  private String name;
  private UUID classId;
  private UUID dynamicRoleId;

  private Map<String, String> values;

  @Transient
  public String getValueByMetaKey(String key) {
    String result = null;
    if (values != null) {
      result = values.get(key);
    }
    return result;
  }
  
}
