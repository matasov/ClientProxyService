package com.matas.liteconstruct.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  private String[] URLExceptions = {"/oauth/token", "/about", "/recovery", "/signup",
      "/static/favicon.ico", "/resources/**", "/perfect/payment", "/payeer/key", "/payeer/payment"};

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // -- define URL patterns to enable OAuth2 security
    http.authorizeRequests().antMatchers(URLExceptions)
        .permitAll().and().authorizeRequests().
        antMatchers("/**").access("hasAnyRole('USER','ADMIN')").and().exceptionHandling()
        .accessDeniedHandler(new OAuth2AccessDeniedHandler());
  }

}
