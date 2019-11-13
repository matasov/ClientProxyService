package com.matas.liteconstruct.db.models.systemdictionary.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.systemdictionary.abstractmodel.SystemDictionaryItemAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemDictionaryItem implements SystemDictionaryItemAbstract {
  
  private UUID companyId;

  private String lang;
  
  private String metaKey;

  private String value;
}
