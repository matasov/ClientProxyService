package com.matas.liteconstruct.db.service.request.frontend;

import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.DBConstants;

public class FrontendRequestBody extends FrontendRequest {

  private Map<String, Object> incomingBody;

  public FrontendRequestBody(Map<String, Object> incomingBody) {
    this.incomingBody = incomingBody;
  }

  @Override
  public Map<String, Object> getSettings() {
    return getBlockByName(DBConstants.SETTINGS);
  }

  @Override
  public Map<String, Object> getInit() {
    return getBlockByName(DBConstants.INIT);
  }

  @Override
  public Map<String, Object> getWork() {
    return getBlockByName(DBConstants.WORK);
  }

  @Override
  public Map<String, Object> getStructure() {
    return getBlockByName(DBConstants.STRUCTURE);
  }

  @Override
  public Map<String, Object> getData() {
    return getBlockByName(DBConstants.DATA);
  }

  private Map<String, Object> getBlockByName(String key) {
    return (Map<String, Object>) incomingBody.get(key);
  }

  private UUID getUUIDFromMap(Map<String, Object> block, String key) throws NullPointerException {
    try {
      return UUID.fromString((String) (block.get(key)));
    } catch (Exception ex) {
      throw new NullPointerException("Value for " + key + " not found.");
    }
  }

  @Override
  public UUID getWorkValue(String key) throws NullPointerException {
    return getUUIDFromMap(getBlockByName(DBConstants.WORK), key);
  }

  @Override
  public UUID getInitValue(String key) throws NullPointerException {
    return getUUIDFromMap(getBlockByName(DBConstants.INIT), key);
  }

  @Override
  public UUID getStructureValue(String key) throws NullPointerException {
    return getUUIDFromMap(getBlockByName(DBConstants.STRUCTURE), key);
  }

  @Override
  public UUID getSettingsValue(String key) throws NullPointerException {
    return getUUIDFromMap(getBlockByName(DBConstants.SETTINGS), key);
  }

  @Override
  public UUID getDataValue(String key) throws NullPointerException {
    return getUUIDFromMap(getBlockByName(DBConstants.DATA), key);
  }

}
