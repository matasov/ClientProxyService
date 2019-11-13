package com.matas.liteconstruct.db.models.recordowner.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.recordowner.abstractmodel.RecordsOwnerModelAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RecordsOwnerModel implements RecordsOwnerModelAbstract {

  private UUID id;

  private UUID contactId;

  private UUID companyId;

  private UUID serviceId;

  private UUID roleId;

  public RecordsOwnerModel(UUID id, UUID contactId, UUID companyId, UUID serviceId, UUID roleId) {
    this.id = id;
    this.contactId = contactId;
    this.companyId = companyId;
    this.serviceId = serviceId;
    this.roleId = roleId;
  }

}
