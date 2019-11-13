package com.matas.liteconstruct.db.models.classowndynamic.repos;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.classowndynamic.abstractmodel.ClassOwnDynamicAbstract;

public interface ClassOwnDynamicRepository {
  public void addClassOwnDynamic(ClassOwnDynamicAbstract classownrecord);

  public void removeClassOwnDynamic(ClassOwnDynamicAbstract classownrecord);

  public void updateClassOwnDynamic(ClassOwnDynamicAbstract classownrecord);

  public ClassOwnDynamicAbstract getClassOwnDynamicById(UUID classownrecordId);

  public List<ClassOwnDynamicAbstract> getClassOwnDynamicForCompanyClassByPermission(UUID classId, int editAccess);
}
