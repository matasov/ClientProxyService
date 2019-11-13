package com.matas.liteconstruct.db.models.companydomain.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.companydomain.abstractmodel.RecaptchaByDomainAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecaptchaByDomain implements RecaptchaByDomainAbstract {

  private UUID id;
  
  private UUID domainId;
  
  private String publicKey;
  
  private String privateKey;

}
