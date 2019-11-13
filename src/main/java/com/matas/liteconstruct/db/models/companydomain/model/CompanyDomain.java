package com.matas.liteconstruct.db.models.companydomain.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.companydomain.abstractmodel.CompanyDomainAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDomain implements CompanyDomainAbstract {
  private UUID id;
  
  private String value;
  
  private Boolean ssl;
  
  private UUID companyId;
  
  private UUID serviceId;
      
}
