package com.matas.liteconstruct.service.dynamic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepositoryImplemented;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepository;
import com.matas.liteconstruct.db.models.security.abstractmodel.SystemRoles;
import com.matas.liteconstruct.db.models.serviceauthorized.abstractmodel.AuthorizedContactAbstract;
import com.matas.liteconstruct.db.models.serviceauthorized.model.AuthorozedContact;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;

@Service
public class CacheSudoParams {

  private FastStructureRepository fastStructureRepository;

  @Autowired
  public void setFastStructureRepositoryImplemented(
      FastStructureRepository fastStructureRepository) {
    this.fastStructureRepository = fastStructureRepository;
  }

  private ObjectMapper objectMapper;

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private CacheMainParams cacheMainParams;

  @Autowired
  public void setCacheMainParams(CacheMainParams cacheMainParams) {
    this.cacheMainParams = cacheMainParams;
  }

  private DynamicRoleRepositoryImplemented dynamicRoleRepository;

  @Autowired
  public void setDynamicRoleRepositoryImplemented(
      DynamicRoleRepositoryImplemented dynamicRoleRepository) {
    this.dynamicRoleRepository = dynamicRoleRepository;
  }

  public String initSudoUser(String keyCollectionCase) {
    AuthorizedContactAbstract authorizedSudo =
        new AuthorozedContact(UUID.fromString("7d82bde3-7740-41d7-9610-8d1fc75db803"),
            UUID.fromString("f949174a-ccf3-4a5b-8d82-898fc38246d5"), "sudo",
            UUID.fromString("62900a19-88a9-4655-a7ac-71488070b659"),
            UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b"),
            UUID.fromString("b56b99b6-2c6f-4103-849a-e914e8594869"),
            SystemRoles.SUPERADMIN_ROLE.getUUID(),
            UUID.fromString("73370021-4d3d-471f-baf8-9bcc010a5733"), null);
    return cacheMainParams.initContact(authorizedSudo, keyCollectionCase, null);
  }

  public Map<String, Object> getSUDOPermissions() {
    Map<String, Object> actualPermissions = new HashMap<>(5);
    // super user
    actualPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID),
        "7d82bde3-7740-41d7-9610-8d1fc75db803");
    actualPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID),
        "af09ea17-d47c-452d-93de-2c89157b9d5b");
    actualPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.SERVICE_ID),
        "b56b99b6-2c6f-4103-849a-e914e8594869");
    actualPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID),
        SystemRoles.SUPERADMIN_ROLE.getId());
    actualPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID),
        "62900a19-88a9-4655-a7ac-71488070b659");
    return actualPermissions;
  }

  public Map<String, Object> getSUDOFastStructureForclassId(UUID classId, String keyCollectionCase)
      throws NullPointerException {
    FastStructureModelAbstract fastStructure =
        fastStructureRepository.getActiveFastStructuresForClassByDynamicRole(
            UUID.fromString("62900a19-88a9-4655-a7ac-71488070b659"), classId, keyCollectionCase);
    if (fastStructure == null)
      throw new NullPointerException("Not found fast structure.");
    Map<String, Object> fastStructureMap;

    try {
      fastStructureMap = objectMapper.readValue(fastStructure.getFastStructureJSON(),
          new TypeReference<Map<String, Object>>() {});
    } catch (IOException e) {
      fastStructureMap = null;
      throw new NullPointerException("Can't parse company structure.");
    }
    return fastStructureMap;
  }
}
