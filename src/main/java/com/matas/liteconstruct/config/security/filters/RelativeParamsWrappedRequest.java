package com.matas.liteconstruct.config.security.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RelativeParamsWrappedRequest extends HttpServletRequestWrapper {
  private final String correctUsername;

  /**
   * Create a new request wrapper that will merge additional parameters into the request object
   * without prematurely reading parameters from the original request.
   * 
   * @param request
   * @param additionalParams
   */
  public RelativeParamsWrappedRequest(final HttpServletRequest request, String correctUsername) {
    super(request);
    this.correctUsername = correctUsername;
  }

  @Override
  public String getParameter(String param) {
    if (param.equalsIgnoreCase("username")) {
      return correctUsername;
    } else
      return super.getParameter(param);
  }
}
