package com.matas.liteconstruct.config;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
  @Bean
  public JavaMailSenderImpl getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("live-line.biz");
    mailSender.setPort(25);

//    mailSender.setUsername("info@live-line.biz");
//    mailSender.setPassword("LiveLine!119522_");

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "false");
    props.put("mail.smtp.starttls.enable", "false");
    props.put("mail.debug", "false");

    return mailSender;
  }
}
