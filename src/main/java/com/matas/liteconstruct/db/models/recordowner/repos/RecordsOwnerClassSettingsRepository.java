package com.matas.liteconstruct.db.models.recordowner.repos;

import java.util.UUID;
import com.matas.liteconstruct.db.models.recordowner.abstractmodel.RecordsOwnerClassSettingsAbstract;

public interface RecordsOwnerClassSettingsRepository {
  
  public void addRecordsOwnerClassSettings(RecordsOwnerClassSettingsAbstract recordsOwner);

  public void removeRecordsOwnerClassSettings(RecordsOwnerClassSettingsAbstract recordsOwner);

  public void updateRecordsOwnerClassSettings(RecordsOwnerClassSettingsAbstract recordsOwner);

  public RecordsOwnerClassSettingsAbstract getRecordsOwnerClassSettingsById(UUID recordsOwnerId);

  public RecordsOwnerClassSettingsAbstract getRecordsOwnerClassSettingsByclassId(UUID classId, UUID ownerFieldId);
  
  public UUID getFieldIdOwnerInDataStructure(UUID classId);
  
}
