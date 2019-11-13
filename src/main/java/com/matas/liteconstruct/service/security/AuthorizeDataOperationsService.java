package com.matas.liteconstruct.service.security;

import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matas.liteconstruct.service.dynamic.CacheMainParams;
import com.matas.liteconstruct.db.tools.StructrueCollectionEnum;
import com.matas.liteconstruct.db.tools.permissions.PermissionHandler;

@Service
public class AuthorizeDataOperationsService {

  private CacheMainParams cacheMainParams;

  @Autowired
  public void setCacheMainParams(CacheMainParams cacheMainParams) {
    this.cacheMainParams = cacheMainParams;
  }

  public boolean isHasPermission(PermissionHandler.PermissionField typePermission,
      UUID systemContactId, UUID classId, UUID fieldId) {
    Map<String, Object> fieldDescription =
        cacheMainParams.getFieldDescriptionByFieldIdRecursively(systemContactId, classId, fieldId);
    switch (typePermission) {
      case USEFUL:
        return PermissionHandler
            .isUseful(Integer.parseInt((String) ((Map<String, Object>) fieldDescription)
                .get(StructrueCollectionEnum.PERM.toString())));
      case VISIBLE:
        return PermissionHandler
            .isVisible(Integer.parseInt((String) ((Map<String, Object>) fieldDescription)
                .get(StructrueCollectionEnum.PERM.toString())));
      case EDIT:
        return PermissionHandler
            .isEdit(Integer.parseInt((String) ((Map<String, Object>) fieldDescription)
                .get(StructrueCollectionEnum.PERM.toString())));
      case DELETE:
        return PermissionHandler
            .isDelete(Integer.parseInt((String) ((Map<String, Object>) fieldDescription)
                .get(StructrueCollectionEnum.PERM.toString())));
      case INSERT:
        return PermissionHandler
            .isInsert(Integer.parseInt((String) ((Map<String, Object>) fieldDescription)
                .get(StructrueCollectionEnum.PERM.toString())));
      default:
        return false;
    }
  }
}
