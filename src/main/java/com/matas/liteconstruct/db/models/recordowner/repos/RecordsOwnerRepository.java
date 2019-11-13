package com.matas.liteconstruct.db.models.recordowner.repos;

import java.util.UUID;
import com.matas.liteconstruct.db.models.recordowner.abstractmodel.RecordsOwnerModelAbstract;

public interface RecordsOwnerRepository {

  public void addRecordsOwner(RecordsOwnerModelAbstract recordsOwner);

  public void removeRecordsOwner(RecordsOwnerModelAbstract recordsOwner);

  public void updateRecordsOwner(RecordsOwnerModelAbstract recordsOwner);

  public RecordsOwnerModelAbstract getRecordsOwnerById(UUID recordsOwnerId);

  public RecordsOwnerModelAbstract getRecordsOwnerByDynamicRoleData(UUID contactId, UUID companyId,
      UUID serviceId, UUID roleId);

}
