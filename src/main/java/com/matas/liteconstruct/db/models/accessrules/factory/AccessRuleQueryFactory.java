package com.matas.liteconstruct.db.models.accessrules.factory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.accessrules.abstractmodel.AccessRuleAbstract;

public interface AccessRuleQueryFactory {
  List<UUID> getListFiltersGroupForSettings(UUID classId,
      Map<String, Object> permissions) throws NullPointerException;

  Map<String, Object> getResultPermissionForAccess(UUID classId, Map<String, Object> permissions);
}
