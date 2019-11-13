package com.matas.liteconstruct.db.models.stafflog.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.stafflog.abstractmodel.ClassLogAbstract;

public interface ClassLogRepository {
  
  void addClassLog(ClassLogAbstract filterGroup);
  
  void addClassLogWithOldValue(ClassLogAbstract logObject);

  void removeClassLog(ClassLogAbstract filterGroup);

  void updateClassLog(ClassLogAbstract filterGroup);

  ClassLogAbstract getClassLogById(UUID filterGroupId);
  
  ClassLogAbstract getClassLogByclassIdAndFieldId(UUID classId, UUID recordId, UUID fieldId);

  List<ClassLogAbstract> getClassLogForclassIdAndContactId(UUID classId, UUID contactId);
  
  List<ClassLogAbstract> getClassLogForclassIdFieldIdAndContactId(UUID classId, UUID contactId, UUID fieldId);
  
  List<ClassLogAbstract> getClassLogByCompanyIdAndContactId(UUID classId, UUID companyId);
  
  List<ClassLogAbstract> getClassLogByCompanyIdFieldIdAndContactId(UUID classId, UUID companyId, UUID fieldId);
}
