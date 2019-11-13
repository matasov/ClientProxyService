package com.matas.liteconstruct.config.recaptcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.matas.liteconstruct.controller.security.SignupController;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReCaptchaResponseFilter implements Filter {

  @Autowired
  private ReCaptchaService reCaptchaService;

  private static final String RE_CAPTCHA_ALIAS = "reCaptchaResponse";
  private static final String RE_CAPTCHA_RESPONSE = "g-recaptcha-response";

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain chain) throws IOException, ServletException {
    System.out.println("start work with recaptcha.");
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    if (request.getRequestURI().equalsIgnoreCase("/oauth/token")
        && request.getParameter("grant_type") == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    if (request.getMethod().equalsIgnoreCase("POST")
        && ((request.getRequestURI().equalsIgnoreCase("/oauth/token")
            && request.getParameter("grant_type").equals("password"))
            || request.getRequestURI().equalsIgnoreCase("/auth/signup")
            || request.getRequestURI().equalsIgnoreCase("/auth/repair"))) {
      String recaptchaResponse = request.getHeader(RE_CAPTCHA_RESPONSE);
      log.info("recaptchaResponse original: {}", recaptchaResponse);
      log.info("recaptchaResponse parameter: {}", request.getParameter(RE_CAPTCHA_RESPONSE));
      if (recaptchaResponse == null)
        recaptchaResponse = request.getParameter(RE_CAPTCHA_RESPONSE);

      boolean isRightCaptcha = reCaptchaService.validate(recaptchaResponse);
      log.info("isRightCaptcha: {}", isRightCaptcha);
      if (recaptchaResponse == null || !isRightCaptcha) {
        log.info("recaptcha is wrong!!! {}, {}", request.getParameter(RE_CAPTCHA_RESPONSE),
            recaptchaResponse);
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
      } else {
        ReCaptchaHttpServletRequest reCaptchaRequest = new ReCaptchaHttpServletRequest(request);
        chain.doFilter(reCaptchaRequest, response);
      }
    } else {
      chain.doFilter(servletRequest, servletResponse);
    }
  }

  @Override
  public void destroy() {}

  private static class ReCaptchaHttpServletRequest extends HttpServletRequestWrapper {

    final Map<String, String[]> params;

    ReCaptchaHttpServletRequest(HttpServletRequest request) {
      super(request);
      params = new HashMap<>(request.getParameterMap());
      params.put(RE_CAPTCHA_ALIAS, request.getParameterValues(RE_CAPTCHA_RESPONSE));
    }

    @Override
    public String getParameter(String name) {
      return params.containsKey(name) ? params.get(name)[0] : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
      return params;
    }

    @Override
    public Enumeration<String> getParameterNames() {
      return Collections.enumeration(params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
      return params.get(name);
    }
  }

  private String getRecaptchaFromBodyLines(String body) {
    try {
      String workString = URLDecoder.decode(body, StandardCharsets.UTF_8.toString());
      workString = workString.substring(workString.indexOf("g-recaptcha-response=") + 21);
      if (workString.indexOf("&") != -1) {
        workString = workString.substring(0, workString.indexOf("&"));
      }
      if (workString.indexOf(System.lineSeparator()) != -1) {
        workString = workString.substring(0, workString.indexOf(System.lineSeparator()));
      }
      return workString;
    } catch (NullPointerException | IOException ex) {
      ex.printStackTrace();
      return null;
    }
  }
}
