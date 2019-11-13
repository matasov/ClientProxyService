package com.matas.liteconstruct.config.security;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.matas.liteconstruct.db.models.security.model.ContactAuth;
import com.matas.liteconstruct.db.models.security.repos.ContactAuthRepository;

@Service
@Transactional
public class SignupService {

  private ContactAuthRepository userRepository;

  @Autowired
  public void setContactAuthRepositoryImplemented(ContactAuthRepository userRepository) {
    this.userRepository = userRepository;
  }

  PasswordEncoder passwordEncoder;

  @Autowired
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  public UUID addUser(ContactAuth user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.addContactAuth(user);
  }

}
