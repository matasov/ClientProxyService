package com.matas.liteconstruct.db.models.lngs.abstractmodel;

import java.util.UUID;

public interface LngCompanyAccess {

  UUID getCompanyId();
  
  UUID getLngId();
  
  String getDescription();
  
  boolean isMain();

}
