package com.matas.liteconstruct.db.models.accessliteral.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.accessliteral.abstractmodel.AccessLiteralModelAbstract;

public interface AccessLiteralRepository {
  public void addLiteral(AccessLiteralModelAbstract literal);

  public void removeLiteral(AccessLiteralModelAbstract literal);

  public void updateLiteral(AccessLiteralModelAbstract literal);

  public AccessLiteralModelAbstract getLiteralById(UUID literalId);

  public List<AccessLiteralModelAbstract> getLiteralsByCompanyId(UUID companyId);

  public List<AccessLiteralModelAbstract> getLiteralsByCompanyIdclassId(UUID companyId,
      UUID classId);

  public String getValueFromDataByLiteral(AccessLiteralModelAbstract literal);
  
  public String getValueFromStructureByLiteral(AccessLiteralModelAbstract literal);
}
