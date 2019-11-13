package com.matas.liteconstruct.db.models.lngs.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.lngs.abstractmodel.LngCompanyAccess;

public interface LngCompanyAccessRepository {
  
  void addLng(LngCompanyAccess lng);

  void removeLngByCompanyAndId(UUID companyId, UUID lngId);

  void removeLngsByCompanyId(UUID companyId);

  void updateLng(LngCompanyAccess lng);

  LngCompanyAccess getMainLngByCompanyId(UUID companyId);
  
  LngCompanyAccess getLngByTokenCompanyId(UUID companyId, String token);

  List<LngCompanyAccess> getLngsByCompanyId(UUID companyId);
  
}
