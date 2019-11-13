package com.matas.liteconstruct.db.models.companydomain.abstractmodel;

import java.util.UUID;

public interface RecaptchaByDomainAbstract {
  
  UUID getId();
  
  UUID getDomainId();
  
  String getPublicKey();
  
  String getPrivateKey();  
  
}
