package com.matas.liteconstruct.db.models.companydomain.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.companydomain.abstractmodel.CompanyDomainAbstract;
import com.matas.liteconstruct.db.models.companydomain.abstractmodel.RecaptchaByDomainAbstract;

public interface CompanyDomainRepository {

  void addCompanyDomain(CompanyDomainAbstract companyDomain);

  void removeCompanyDomain(CompanyDomainAbstract companyDomain);

  void updateCompanyDomain(CompanyDomainAbstract companyDomain);

  CompanyDomainAbstract getCompanyDomainById(UUID companyDomainId);

  CompanyDomainAbstract getCompanyDomainByCompanyService(UUID company, UUID service);

  List<CompanyDomainAbstract> getCompanyDomainsByCompany(UUID company);
  
  CompanyDomainAbstract getCompanyDomainByValue(String value);
  
  RecaptchaByDomainAbstract getRecaptchaByValue(String value);
  
}
