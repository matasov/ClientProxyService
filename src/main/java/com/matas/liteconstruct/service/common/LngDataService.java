package com.matas.liteconstruct.service.common;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matas.liteconstruct.db.models.lngs.abstractmodel.LngCompanyAccess;
import com.matas.liteconstruct.db.models.lngs.model.LngCompanyAccessImplemented;
import com.matas.liteconstruct.db.models.lngs.repos.LngCompanyAccessRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LngDataService {

  private String DEFAULT_LNG = "3d62bf3a-8190-4c31-9ec7-d5b41afab25f";

  private LngCompanyAccessRepository lngCompanyRelationsRepository;

  @Autowired
  public void setLngCompanyRelationsRepository(
      LngCompanyAccessRepository lngCompanyRelationsRepository) {
    this.lngCompanyRelationsRepository = lngCompanyRelationsRepository;
  }

  public LngCompanyAccess getDefaultLng(UUID companyId) {
    LngCompanyAccess result = lngCompanyRelationsRepository.getMainLngByCompanyId(companyId);
    if (result == null) {
      result = new LngCompanyAccessImplemented(companyId, UUID.fromString(DEFAULT_LNG),
          "Default system lng.", true);
    }
    return result;
  }

  public LngCompanyAccess getLngByToken(UUID companyId, String token) {
    LngCompanyAccess result = null;
    if (companyId != null && token != null)
      try {
        lngCompanyRelationsRepository.getLngByTokenCompanyId(companyId, token);
      } catch (Exception ex) {
        log.error("{}", ex);
      }
    if (result == null) {
      result = getDefaultLng(companyId);
    }
    return result;
  }
}
