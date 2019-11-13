package com.matas.liteconstruct.db.models.recordowner.abstractmodel;

import java.util.UUID;

public interface RecordsOwnerModelAbstract {

  public UUID getId();

  public UUID getContactId();

  public UUID getCompanyId();

  public UUID getServiceId();

  public UUID getRoleId();

}
