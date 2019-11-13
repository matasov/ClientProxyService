package com.matas.liteconstruct.db.models.security.repos;

import java.util.UUID;
import com.matas.liteconstruct.db.models.security.model.ContactAuth;

public interface ContactAuthRepository {

  UUID addContactAuth(ContactAuth authContact);
  
  ContactAuth getContactAuthById(UUID authContactId);

  ContactAuth getContactAuthByLogin(String authContactLogin);
  
  boolean checkContactAuthByIdOrLogin(String uid, String authContactLogin);

}
