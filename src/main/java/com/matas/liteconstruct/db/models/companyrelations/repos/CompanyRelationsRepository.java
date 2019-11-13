package com.matas.liteconstruct.db.models.companyrelations.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.companyrelations.abstractmodel.CompanyRelationsAbstract;

public interface CompanyRelationsRepository {
  
  void addCompanyRelations(CompanyRelationsAbstract companyRelation);

  void removeCompanyRelationsBySlaveCompanyId(UUID slaveCompanyId);

  void removeCompanyRelationsByMasterCompanyRoleId(UUID slaveCompanyId, UUID masterCompanyRoleId);

  CompanyRelationsAbstract getCompanyRelationsByMasterSlaveRole(UUID masterCompanyId,
      UUID slaveCompanyId, UUID masterCompanyRoleId);

  List<CompanyRelationsAbstract> getCompanyRelationsByMaster(UUID masterCompanyId);

  List<CompanyRelationsAbstract> getCompanyRelationsBySlave(UUID slaveCompanyId);

  List<CompanyRelationsAbstract> getCompanyRelationsByMasterAndRole(UUID masterCompanyId,
      UUID masterCompanyRoleId);

}
