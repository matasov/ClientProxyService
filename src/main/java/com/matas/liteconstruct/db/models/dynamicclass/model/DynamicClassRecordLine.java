package com.matas.liteconstruct.db.models.dynamicclass.model;

import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.dynamicclass.abstractmodel.DynamicClassRecordLineAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicClassRecordLine implements DynamicClassRecordLineAbstract {

  private UUID rootclassId;

  private UUID recordId;
  
  private UUID ownerFieldId;
  
  private UUID ownerRecordId;
  
  private UUID dateCreateFieldId;
  
  private UUID dateChangeFieldId;
  
  private long timeStamp;

  private UUID identificatorFieldId;

  private Map<String, Object> fieldValues;

  @Override
  public String getTblClassDataUse() {
    return "cc_" + rootclassId + "_data_use";
  }

}
