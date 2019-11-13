package com.matas.liteconstruct.controller.security;

import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepositoryImplemented;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepositoryImplemented;
import com.matas.liteconstruct.service.dynamic.DynamicClassPutData;
import com.matas.liteconstruct.service.signup.SignupServiceHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class SignupController {

  private SignupServiceHelper signupHelper;

  @Autowired
  public void setSignupServiceHelper(SignupServiceHelper signupHelper) {
    this.signupHelper = signupHelper;
  }

  @RequestMapping(value = "signup", method = RequestMethod.POST)
  public Response signupMap(HttpEntity<String> httpEntity) {
    if (httpEntity.getBody() == null)
      return null;
    log.info("start work with: {}", httpEntity);
    try {
      signupHelper.signupRequestProcessor(httpEntity.getBody());
    } catch (NullPointerException ex) {
      ex.printStackTrace();
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(String.format("{\"result\":\"%1$s\"}", ex.getMessage())).build();
    }
    return Response.status(Response.Status.OK).entity("{\"message\": \"User is added!\"}").build();
  }

}
