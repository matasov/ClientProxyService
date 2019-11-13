package com.matas.liteconstruct.db.models.lngs.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.lngs.abstractmodel.LngCompanyAccess;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class LngCompanyAccessImplemented implements LngCompanyAccess {

  UUID companyId;

  UUID lngId;

  String description;

  boolean main;

}
