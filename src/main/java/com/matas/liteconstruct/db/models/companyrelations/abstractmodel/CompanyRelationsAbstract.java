package com.matas.liteconstruct.db.models.companyrelations.abstractmodel;

import java.util.UUID;

public interface CompanyRelationsAbstract {

  UUID getMasterCompanyId();

  UUID getSlaveCompanyId();

  UUID getMasterCompanyRoleId();
}
