package com.matas.liteconstruct.db.models.companycontactclass.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.companycontactclass.abstractmodel.CompanyContactRelationAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyContactRelation implements CompanyContactRelationAbstract{

  private UUID companyId;
  
  private UUID classId;
  
}
