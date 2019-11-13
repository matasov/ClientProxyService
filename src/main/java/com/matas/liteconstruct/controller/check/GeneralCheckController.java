package com.matas.liteconstruct.controller.check;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.service.SQLProtection;
import com.matas.liteconstruct.service.dynamic.CacheMainParams;
import com.matas.liteconstruct.service.dynamic.CacheSudoParams;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/check")
public class GeneralCheckController {

  private CacheSudoParams cacheSudoParams;

  @Autowired
  public void setCacheSudoParams(CacheSudoParams cacheSudoParams) {
    this.cacheSudoParams = cacheSudoParams;
  }

  private CacheMainParams cacheMainParams;

  @Autowired
  public void setCacheMainParams(CacheMainParams cacheMainParams) {
    this.cacheMainParams = cacheMainParams;
  }

  private ObjectMapper objectMapper;

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private final String keyCollectionCase = null;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<?> checkLoginInDB(@RequestBody Map<String, Object> request) {
    String message = null;
    cacheSudoParams.initSudoUser(keyCollectionCase);
    String login = checkStringValueParams(
        (String) SQLProtection.protectRequestObject(request.get("login")), 4, true, true);
    if (login == null) {
      message = "{\"result\":\"Incorrect login.\"}";
    } else {
      Map<String, Object> resultDbSearch = getContactRecordByLogin(login);
      if (resultDbSearch == null || resultDbSearch.isEmpty()) {
        message = "{\"result\":\"Login is correct.\", \"code\":\"200\"}";

      } else {
        message = "{\"result\":\"Login is present.\"}";
      }
    }
    return new ResponseEntity<>(message, HttpStatus.OK);
  }

  private Map<String, Object> getContactRecordByLogin(String login) {
    List<Map<String, Object>> dynamicFilters = null;
    Map<String, Object> sudoMapStructure = cacheSudoParams
        .getSUDOFastStructureForclassId(UUID.fromString(DBConstants.CONTACT_ID), keyCollectionCase);
    int indexName = cacheMainParams.getIndexFieldInStructure(
        UUID.fromString("060f16c7-7573-413f-8f38-fe8d4bf177aa"), sudoMapStructure);
    try {
      dynamicFilters =
          objectMapper.readValue(
              "[{\"null.null." + DBConstants.CONTACT_ID + "."
                  + ((Map<String, Object>) sudoMapStructure.get(Integer.toString(indexName)))
                      .get("id")
                  + "\" : {\"value\": \"" + login + "\", \"operator\":\"0\"}}]",
              new TypeReference<List<Map<String, Object>>>() {});
    } catch (IOException e) {
      dynamicFilters = null;
      throw new NullPointerException("Not found structure.");
    }
    List<Map<String, Object>> result =
        cacheMainParams.getRecordsByExternalMapStructure(CacheMainParams.SUDO_CONTACT_ID,
            UUID.fromString(DBConstants.CONTACT_ID), sudoMapStructure, dynamicFilters, null, null, null);
    return result == null || result.isEmpty() || result.get(0) == null ? null : result.get(0);
  }

  private String checkStringValueParams(String value, int minLength, boolean trim,
      boolean lowerCase) throws NullPointerException {
    if (value == null)
      return null;
    String workString = trim ? value.trim() : value;
    return workString.length() >= minLength ? workString : null;
  }
}
