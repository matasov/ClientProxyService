package com.matas.liteconstruct.db.service.request.frontend;

import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.service.request.APIRequest;
import com.matas.liteconstruct.db.service.respond.APIRespond;

public abstract class FrontendRequest implements APIRequest {

  @Override
  public abstract Map<String, Object> getSettings();

  @Override
  public abstract Map<String, Object> getInit();

  @Override
  public abstract Map<String, Object> getWork();

  @Override
  public abstract Map<String, Object> getStructure();

  @Override
  public abstract Map<String, Object> getData();
  
  public abstract UUID getWorkValue(String key);

  public abstract UUID getInitValue(String key);

  public abstract UUID getStructureValue(String key);

  public abstract UUID getSettingsValue(String key);

  public abstract UUID getDataValue(String key);
}
