package com.matas.liteconstruct.db.service.request;

import java.util.Map;

public interface APIRequest {

  Map<String, Object> getSettings();
  
  Map<String, Object> getInit();
  
  Map<String, Object> getWork();
  
  Map<String, Object> getStructure();
  
  Map<String, Object> getData();
  
}
