package com.matas.liteconstruct.controller.migration;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactory;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactoryImplemented;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepository;
import com.matas.liteconstruct.db.models.recordowner.abstractmodel.RecordsOwnerModelAbstract;
import com.matas.liteconstruct.db.models.recordowner.model.RecordsOwnerModel;
import com.matas.liteconstruct.db.models.security.model.RegisterDTO;
import com.matas.liteconstruct.db.models.serviceauthorized.model.AuthorozedContact.MapKey;
import com.matas.liteconstruct.db.models.serviceauthorized.repos.AuthorizedContactRepository;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import com.matas.liteconstruct.service.dynamic.DynamicClassPutData;
import com.matas.liteconstruct.service.signup.SignupServiceHelper;

@RestController
@RequestMapping("/migration")
public class MigrationController {

  private SignupServiceHelper serviceHelper;

  @Autowired
  public void setSignupServiceHelper(SignupServiceHelper serviceHelper) {
    this.serviceHelper = serviceHelper;
  }

  PasswordEncoder passwordEncoder;

  @Autowired
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  DynamicClassPutData dynamicPut;

  @Autowired
  public void setDynamicClassPutData(DynamicClassPutData dynamicPut) {
    this.dynamicPut = dynamicPut;
  }

  private FastStructureRepository fastStructureRepository;

  @Autowired
  public void setFastStructureRepositoryImplemented(
      FastStructureRepository fastStructureRepository) {
    this.fastStructureRepository = fastStructureRepository;
  }

  private AccessRuleQueryFactory accessRuleQueryFactoryImplemented;

  @Autowired
  void setAccessRuleQueryFactoryImplemented(
      AccessRuleQueryFactoryImplemented accessRuleQueryFactoryImplemented) {
    this.accessRuleQueryFactoryImplemented = accessRuleQueryFactoryImplemented;
  }

  private AuthorizedContactRepository authorizedContactRepository;

  @Autowired
  void setAuthorozedContactRepository(AuthorizedContactRepository authorozedContactRepository) {
    this.authorizedContactRepository = authorozedContactRepository;
  }

  private ObjectMapper objectMapper;

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @RequestMapping(value = "sample", method = RequestMethod.PUT)
  public Response signupValues(@RequestBody Map<String, Object> contactRegistered) {
    if (contactRegistered != null && !contactRegistered.isEmpty()
        && contactRegistered.containsKey("data")) {
      UUID[] userIdArray = new UUID[2];
      // Map<String, Object> mapElement =
      Optional.ofNullable(((List<Object>) contactRegistered.get("data"))).map(Collection::stream)
          .orElseGet(Stream::empty).forEach(mapElement -> {
            if (mapElement != null) {
              UUID classId =
                  UUID.fromString((String) ((Map<String, Object>) mapElement).get("cclass"));
              List<Object> objectRecords =
                  (List<Object>) ((Map<String, Object>) mapElement).get("records");
              if (objectRecords != null) {
                final UUID defaultCompanyId =
                    UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b");
                final UUID defaultServiceId =
                    UUID.fromString("b56b99b6-2c6f-4103-849a-e914e8594869");
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                UserDetails userDetail = (UserDetails) auth.getPrincipal();
                if (userDetail != null && userDetail.getUsername() != null
                    && userDetail.getUsername().length() > 2) {
                  Map<String, Object> actualPermissions =
                      accessRulesByUserPermissionsFilter(userDetail, classId);
                  System.out.println("migration actualPermissions: " + actualPermissions);
                  objectRecords.parallelStream().forEach(item -> {
                    UUID recordOwnerId = null;
                    if (classId.equals(UUID.fromString("7a38bfb3-7874-4eb4-b981-b38e5ade2df8"))) {
                      UUID newUserId = UUID.randomUUID();
                      userIdArray[0] = newUserId;
                      Map<String, Object> itemFields = (Map<String, Object>) item;
                      String encoderPassword = passwordEncoder.encode(UUID.randomUUID().toString());
                      RegisterDTO newUserValues = new RegisterDTO(newUserId.toString(),
                          (String) ((Map<String, Object>) itemFields.get("fields"))
                              .get("81613045-4bb7-4576-9752-12dc08689b7d"),
                          encoderPassword, encoderPassword, null, null, null);

                      try {
                        recordOwnerId = serviceHelper.insertNewUserToSystem(newUserValues,
                            defaultCompanyId, defaultServiceId);
                        userIdArray[1] = recordOwnerId;
                      } catch (NullPointerException ex) {
                        ex.printStackTrace();
                      }
                    } else {
                      System.out.println("Found saved ownerId: " + userIdArray[1]);
                      recordOwnerId = userIdArray[1];
                    }
                    System.out.println("permissions: " + actualPermissions);
                    if (recordOwnerId != null) {
                      actualPermissions.put(
                          FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID),
                          userIdArray[0].toString());
                      RecordsOwnerModelAbstract recordsOwner = new RecordsOwnerModel(recordOwnerId,
                          UUID.fromString((String) actualPermissions.get(
                              FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID))),
                          UUID.fromString((String) actualPermissions.get(
                              FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID))),
                          UUID.fromString((String) actualPermissions.get(
                              FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.SERVICE_ID))),
                          UUID.fromString((String) actualPermissions
                              .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID))));
                      // dynamicPut.getRecordsOwner(actualPermissions);
                      FastStructureModelAbstract fastStructure =
                          fastStructureRepository
                              .getActiveFastStructuresForClassByDynamicRole(
                                  UUID.fromString(
                                      (String) actualPermissions.get(FactoryGroupAbstract
                                          .getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID))),
                                  classId, null);
                      Map<String, Object> mapStructure = null;
                      try {
                        mapStructure = objectMapper.readValue(fastStructure.getFastStructureJSON(),
                            new TypeReference<Map<String, Object>>() {});
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                      if (mapStructure != null) {
                        if (((Map<String, Object>) item).get("id") != null
                            && !((Map<String, Object>) item).get("id").equals("null")) {
                          Map<String, Object> fields =
                              (Map<String, Object>) ((Map<String, Object>) item).get("fields");
                          fields.put(
                              (String) ((Map<String, Object>) mapStructure.get("0")).get("id"),
                              ((Map<String, Object>) item).get("id"));
                          ((Map<String, Object>) item).put("fields", fields);
                        }

//                        dynamicPut.setItemToDynamicTable(classId, (Map<String, Object>) item,
//                            mapStructure, recordsOwner, actualPermissions);
                      }
                    }
                  });
                }
              }
            }
          });
    }
    // FastStructureModelAbstract fastStructure =
    // fastStructureRepository.getActiveFastStructuresForClassByDynamicRole(
    // UUID.fromString((String) actualPermissions
    // .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID))),
    // currentClass);
    return null;
  }

  private Map<String, Object> accessRulesByUserPermissionsFilter(UserDetails userDetail,
      UUID classId) {
    if (classId == null) {
      return null;
    }
    return accessRuleQueryFactoryImplemented.getResultPermissionForAccess(classId,
        (Map<String, Object>) authorizedContactRepository
            .getAuthorizedContactByName(userDetail.getUsername()).getPermissions()
            .get(MapKey.PERMISSIONS));
  }
}
