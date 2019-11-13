package com.matas.liteconstruct.db.models.companycontactclass.repos;

import java.util.UUID;
import com.matas.liteconstruct.db.models.companycontactclass.abstractmodel.CompanyContactRelationAbstract;

public interface CompanyContactRelationRepository {

  void addCompanyContactRelation(CompanyContactRelationAbstract companyContactRelationItem);

  void removeCompanyContactRelation(
      CompanyContactRelationAbstract companyContactRelationItem);

  void updateCompanyContactRelation(
      CompanyContactRelationAbstract companyContactRelationItem);

  CompanyContactRelationAbstract getCompanyContactRelationByCompanyId(UUID companyId);
}
