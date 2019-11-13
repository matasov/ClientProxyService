package com.matas.liteconstruct.db.service.request.backend;

import java.util.Map;
import com.matas.liteconstruct.db.service.request.APIRequest;

public abstract class BackendRequest implements APIRequest {

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

}
