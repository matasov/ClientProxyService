package com.matas.liteconstruct.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.matas.liteconstruct.config.security.model.ContactAuthDetails;
import com.matas.liteconstruct.db.models.security.model.ContactAuth;
import com.matas.liteconstruct.db.models.security.repos.ContactAuthRepository;

@Service
public class ContactAuthDetailsService implements UserDetailsService {

  private ContactAuthRepository userRepository;

  @Autowired
  public void setContactAuthRepositoryImplemented(ContactAuthRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    if (userName == null || userName.equals("")) {
      new UsernameNotFoundException("UserName is empty.");
    }
    if (userName.indexOf("'") >= 0 || userName.indexOf("\\") >= 0) {
      new UsernameNotFoundException("Unexpected symbol in username.");
    }
    ContactAuth user = userRepository.getContactAuthByLogin(userName.toLowerCase());
    if (user == null) {
      throw new UsernameNotFoundException("UserName " + userName + " not found.");
    }
    return new ContactAuthDetails(user);
  }

}
