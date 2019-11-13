package com.matas.liteconstruct.db.models.systemdictionary.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.systemdictionary.abstractmodel.SystemDictionaryItemAbstract;

public interface SystemDictionaryItemRepository {

  void addSystemDictionaryItem(SystemDictionaryItemAbstract dictionaryItem);


  void updateSystemDictionaryItem(SystemDictionaryItemAbstract dictionaryItem);


  SystemDictionaryItemAbstract getSystemDictionaryItemByParams(UUID companyId, String lang,
      String metaKey);
  
  SystemDictionaryItemAbstract getSystemDictionaryItemByParamsLongWay(UUID companyId, String lang,
      String metaKey);


  void removeItemByParams(UUID companyId, String lang, String metaKey);


  void removeItemsByCompanyId(UUID companyId);


  List<SystemDictionaryItemAbstract> getSystemDictionaryItemsByCompanyId(UUID companyId);
  
  
}
