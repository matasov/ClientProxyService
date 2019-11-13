package com.matas.liteconstruct.service.business;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

public class LiveLineLevelsService {

  private LiveLineBusinessService liveLineBusinessService;
  
  @Autowired
  public void setLiveLineBusinessService(LiveLineBusinessService liveLineBusinessService) {
    this.liveLineBusinessService = liveLineBusinessService;
  }
  
  private String buyPlace(UUID liveLineUserId, int level, boolean isClone) {
    return null;
  }

}
