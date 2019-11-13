package com.matas.liteconstruct.db.service;

import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.matas.liteconstruct.db.service.request.APIRequest;
import com.matas.liteconstruct.db.service.respond.APIRespond;

@Service
public class DBDynamicProcessorImplemented implements DBDynamicProcessor {

  public Map<String, String> getValuesForDynamicRole(UUID dynamicRoleId){
    return null;
  }
  
  public boolean checkAccessForIncomingPermissions(APIRequest request) {
    return false;
  }
  
  public Map<String, Object> getPermissionsByAccessRules(){
    return null;
  }
  
  public APIRespond getDataResultForIncomingQuery(APIRequest request){
    checkAccessForIncomingPermissions(request);
    return null;
  }
}
