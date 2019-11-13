package com.matas.liteconstruct.db.models.companyrelations.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.companyrelations.abstractmodel.CompanyRelationsAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyRelations implements CompanyRelationsAbstract {
  private UUID masterCompanyId;

  private UUID slaveCompanyId;

  private UUID masterCompanyRoleId;
}
