package com.matas.liteconstruct.service.common;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class HttpRequestService {

  public String getKeyToken(String externalKeyCollectionCase) {
    if (externalKeyCollectionCase == null || externalKeyCollectionCase.equals("")) {
      return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
          .getRequest().getServerName();
    } else
      return externalKeyCollectionCase;
  }
}
