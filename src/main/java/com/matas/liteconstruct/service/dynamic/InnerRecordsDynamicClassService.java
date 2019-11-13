package com.matas.liteconstruct.service.dynamic;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matas.liteconstruct.db.models.innerrecords.abstractmodel.BelongRecord;
import com.matas.liteconstruct.db.models.innerrecords.abstractmodel.InnerRecord;
import com.matas.liteconstruct.db.models.innerrecords.model.BelongRecordImplemented;
import com.matas.liteconstruct.db.models.innerrecords.model.InnerRecordImplemented;
import com.matas.liteconstruct.db.models.innerrecords.repos.InnerRecordsDynamicClassRepository;
import com.matas.liteconstruct.db.tools.permissions.PermissionHandler;
import com.matas.liteconstruct.service.security.AuthorizeDataOperationsService;

@Service
public class InnerRecordsDynamicClassService {

  private InnerRecordsDynamicClassRepository innerRecordsDynamicClassRepository;

  @Autowired
  public void setInnerRecordsDynamicClassRepository(
      InnerRecordsDynamicClassRepository innerRecordsDynamicClassRepository) {
    this.innerRecordsDynamicClassRepository = innerRecordsDynamicClassRepository;
  }

  private AuthorizeDataOperationsService authorizeDataOperationsService;

  @Autowired
  public void setAuthorizeDataOperationsService(
      AuthorizeDataOperationsService authorizeDataOperationsService) {
    this.authorizeDataOperationsService = authorizeDataOperationsService;
  }

  public void insertRecordToInnerClass(UUID systemContactId, UUID parentClassId, UUID innerClassId,
      UUID parentFieldId, UUID innerRecordId, UUID implementedId, int parentTurn)
      throws NullPointerException {
    if (!authorizeDataOperationsService.isHasPermission(PermissionHandler.PermissionField.EDIT,
        systemContactId, parentClassId, parentFieldId)) {
      throw new NullPointerException("Access to field is denied.");
    }
    UUID realImplementedId;
    BelongRecord presentRecord;
    if (implementedId == null) {
      realImplementedId = UUID.randomUUID();
    } else {
      realImplementedId = implementedId;
    }
    presentRecord = innerRecordsDynamicClassRepository.getBelongRecordForImplemented(innerClassId,
        realImplementedId);

    if (presentRecord == null) {
      // insert
      presentRecord = new BelongRecordImplemented(innerClassId, realImplementedId, parentClassId,
          parentFieldId);
      innerRecordsDynamicClassRepository.addBelongRecord(presentRecord);
    }
    checkAndAddInnerRecord(innerClassId, realImplementedId, innerRecordId, parentTurn);
  }

  public void checkAndAddInnerRecord(UUID innerClassId, UUID implementedId, UUID recordId,
      int parentTurn) {
    List<InnerRecord> allRecords = innerRecordsDynamicClassRepository
        .getInnerRecordsForImplemented(innerClassId, implementedId);
    InnerRecord newRecord = new InnerRecordImplemented(innerClassId, implementedId, recordId, 0);

    if (allRecords == null || allRecords.isEmpty()) {
      // insert new with index 0
      innerRecordsDynamicClassRepository.addInnerRecord(newRecord);
    } else {
      // update with check index
      InnerRecord presentOnes = allRecords.stream()
          .filter(inner -> inner.getRecordId().equals(recordId)).findAny().orElse(null);
      if (presentOnes == null) {
        newRecord.setTurn(allRecords.size());
        innerRecordsDynamicClassRepository.addInnerRecord(newRecord);
      } else {
        newRecord.setTurn(presentOnes.getTurn());
        allRecords.remove(presentOnes);
      }
      if (parentTurn < 0 || parentTurn >= allRecords.size()) {
        allRecords.add(newRecord);
      } else {
        allRecords.add(parentTurn, newRecord);
      }
      int[] index = {0};
      allRecords.forEach(record -> {
        record.setTurn(index[0]++);
        innerRecordsDynamicClassRepository.updateInnerRecord(record);
      });
    }
  }

  public void removeInnerRecord(UUID parentClassId, UUID innerClassId, UUID innerRecordId,
      UUID implementedId) {
    List<InnerRecord> allRecords = innerRecordsDynamicClassRepository
        .getInnerRecordsForImplemented(innerClassId, implementedId);
    if (allRecords == null || allRecords.isEmpty()) {
      // do nothing
      return;
    } else {
      innerRecordsDynamicClassRepository.deleteInnerRecord(innerClassId, implementedId,
          innerRecordId);
      // update with check index
      List<InnerRecord> presentOnes = allRecords.stream()
          .filter(inner -> !inner.getRecordId().equals(innerRecordId)).collect(Collectors.toList());
      int[] index = {0};
      presentOnes.forEach(record -> {
        record.setTurn(index[0]++);
        innerRecordsDynamicClassRepository.updateInnerRecord(record);
      });
    }
  }

  public void deleteAllInnerRecordsByImplemented(UUID parentClassId, UUID innerClassId,
      UUID implementedId) {
    innerRecordsDynamicClassRepository.deleteBelongRecord(implementedId);
    innerRecordsDynamicClassRepository.clearAllInnerRecordsByImplementedId(innerClassId,
        implementedId);
  }

  public void deleteAllInnerRecordsBeforeDeleteField(UUID parentClassId, UUID innerClassId,
      UUID fieldId) {
    innerRecordsDynamicClassRepository.clearAllInnerRecordsWhileDeleteFieldId(parentClassId,
        innerClassId, fieldId);
    innerRecordsDynamicClassRepository.clearAllBelongRecordsWhileDeleteFieldId(parentClassId,
        innerClassId, fieldId);
  }
}
