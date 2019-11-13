package com.matas.liteconstruct.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HttpReqRespUtils {
  private static final String[] IP_HEADER_CANDIDATES = {"X-Forwarded-For", "Proxy-Client-IP",
      "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP",
      "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"};

  public static String getClientIpAddressIfServletRequestExist() {

    if (RequestContextHolder.getRequestAttributes() == null) {
      return "0.0.0.0";
    }

    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    String ipList = Stream.of(IP_HEADER_CANDIDATES).filter(x -> (request.getHeader(x) != null
        && request.getHeader(x).length() != 0 && !"unknown".equalsIgnoreCase(request.getHeader(x))))
        .findAny().orElse(null);
    if (ipList != null) {
      String ip = ipList.split(",")[0];
      String regexp =
          "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
      Pattern pattern = Pattern.compile(regexp);

      Matcher matcher = pattern.matcher(ip);
      if (matcher.matches())
        return ip;
    }
    return request.getRemoteAddr();
  }
}
