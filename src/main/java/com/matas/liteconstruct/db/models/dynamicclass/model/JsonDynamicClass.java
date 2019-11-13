package com.matas.liteconstruct.db.models.dynamicclass.model;

import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.dynamicclass.abstractmodel.JsonDynamicClassAbstract;
import com.matas.liteconstruct.db.tools.StructrueCollectionEnum;

public class JsonDynamicClass implements JsonDynamicClassAbstract {

  private Map<String, Object> structure;

  public JsonDynamicClass(Map<String, Object> structure) {
    this.structure = structure;
  }

  @Override
  public Map<String, Object> getStructureFields() {
    return structure;
  }

  @Override
  public Map<String, Object> getStructureFieldByIndex(int fieldIndex) {
    return (Map<String, Object>) structure.get(Integer.toString(fieldIndex));
  }

  @Override
  public Map<String, Object> getStructureFieldByFieldId(UUID fieldId) {
    return getStructureFieldByFieldIdRecursively(fieldId, structure);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getStructureFieldByFieldIdRecursively(UUID fieldId,
      Map<String, Object> localStructure) {
    for (Map.Entry<String, Object> entry : localStructure.entrySet()) {
      if (((Map<String, Object>) entry.getValue()).get(StructrueCollectionEnum.ID.toString())
          .equals(fieldId)) {
        return (Map<String, Object>) entry.getValue();
      }
      if (((Map<String, Object>) entry.getValue()).get(StructrueCollectionEnum.INNER.toString())
          .equals("3")
          && ((Map<String, Object>) entry.getValue())
              .containsKey(StructrueCollectionEnum.NESTED.toString())) {
        Map<String, Object> result = getStructureFieldByFieldIdRecursively(fieldId,
            (Map<String, Object>) ((Map<String, Object>) entry.getValue())
                .get(StructrueCollectionEnum.NESTED.toString()));
        if (result != null)
          return result;
      }
    }
    return null;
  }

}
