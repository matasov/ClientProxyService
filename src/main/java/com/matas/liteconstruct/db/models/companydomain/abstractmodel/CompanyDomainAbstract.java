package com.matas.liteconstruct.db.models.companydomain.abstractmodel;

import java.util.UUID;

public interface CompanyDomainAbstract {
  
  UUID getId();

  String getValue();

  Boolean getSsl();

  UUID getCompanyId();

  UUID getServiceId();
}
