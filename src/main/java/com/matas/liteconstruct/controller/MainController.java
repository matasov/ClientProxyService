package com.matas.liteconstruct.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.glassfish.jersey.internal.util.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactory;
import com.matas.liteconstruct.db.models.accessrules.factory.AccessRuleQueryFactoryImplemented;
import com.matas.liteconstruct.db.models.collections.abstractmodel.StructureCollectionTreeFactoryImplemented;
import com.matas.liteconstruct.db.models.companydomain.abstractmodel.CompanyDomainAbstract;
import com.matas.liteconstruct.db.models.companydomain.repos.CompanyDomainRepository;
import com.matas.liteconstruct.db.models.dynamicclass.queryfactory.DynamicClassesQueryFactory;
import com.matas.liteconstruct.db.models.dynamicclass.repos.DynamicClassesRepository;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepository;
import com.matas.liteconstruct.db.models.faststructure.abstractmodel.FastStructureModelAbstract;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepository;
import com.matas.liteconstruct.db.models.recordowner.repos.RecordsOwnerClassSettingsRepository;
import com.matas.liteconstruct.db.models.security.abstractmodel.SystemRoles;
import com.matas.liteconstruct.db.models.serviceauthorized.model.AuthorozedContact.MapKey;
import com.matas.liteconstruct.db.models.serviceauthorized.repos.AuthorizedContactRepository;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import com.matas.liteconstruct.service.HttpReqRespUtils;
import com.matas.liteconstruct.service.business.LiveLineBusinessService;
import com.matas.liteconstruct.service.dynamic.CacheMainParams;
import com.matas.liteconstruct.service.dynamic.DynamicClassPutData;
import com.matas.liteconstruct.service.management.ManagementAccessService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/")
public class MainController {

  private ManagementAccessService managementAccessService;

  @Autowired
  void setManagementAccessService(ManagementAccessService managementAccessService) {
    this.managementAccessService = managementAccessService;
  }

  private CacheMainParams cacheMainParams;

  @Autowired
  public void setCacheMainParams(CacheMainParams cacheMainParams) {
    this.cacheMainParams = cacheMainParams;
  }

  private DynamicRoleRepository dynamicRoleRepositoryImplemented;

  @Autowired
  public void setDynamicRoleRepositoryImplemented(
      DynamicRoleRepository dynamicRoleRepositoryImplemented) {
    this.dynamicRoleRepositoryImplemented = dynamicRoleRepositoryImplemented;
  }

  private AuthorizedContactRepository authorizedContactRepository;

  @Autowired
  void setAuthorozedContactRepository(AuthorizedContactRepository authorozedContactRepository) {
    this.authorizedContactRepository = authorozedContactRepository;
  }

  private AccessRuleQueryFactory accessRuleQueryFactoryImplemented;

  @Autowired
  void setAccessRuleQueryFactoryImplemented(
      AccessRuleQueryFactoryImplemented accessRuleQueryFactoryImplemented) {
    this.accessRuleQueryFactoryImplemented = accessRuleQueryFactoryImplemented;
  }

  private StructureCollectionTreeFactoryImplemented factoryImplemented;

  @Autowired
  public void setStructureCollectionTreeFactoryImplemented(
      StructureCollectionTreeFactoryImplemented factoryImplemented) {
    this.factoryImplemented = factoryImplemented;
  }

  private FastStructureRepository fastStructureRepository;

  @Autowired
  public void setFastStructureRepositoryImplemented(
      FastStructureRepository fastStructureRepository) {
    this.fastStructureRepository = fastStructureRepository;
  }

  private DynamicClassPutData dynamicClassPutData;

  @Autowired
  public void setDynamicClassPutData(DynamicClassPutData dynamicClassPutData) {
    this.dynamicClassPutData = dynamicClassPutData;
  }

  private DynamicClassesQueryFactory dynamicClassesQueryFactory;

  @Autowired
  public void setDynamicClassesQueryFactory(DynamicClassesQueryFactory dynamicClassesQueryFactory) {
    this.dynamicClassesQueryFactory = dynamicClassesQueryFactory;
  }

  private RecordsOwnerClassSettingsRepository recordsOwnerClassSettingsRepositoryImplemented;

  @Autowired
  public void setRecordsOwnerClassSettingsRepositoryImplemented(
      RecordsOwnerClassSettingsRepository recordsOwnerClassSettingsRepositoryImplemented) {
    this.recordsOwnerClassSettingsRepositoryImplemented =
        recordsOwnerClassSettingsRepositoryImplemented;
  }

  private DynamicClassesRepository dynamicClassesRepositoryImplemented;

  @Autowired
  public void setDynamicClassesRepositoryImplemented(
      DynamicClassesRepository dynamicClassesRepositoryImplemented) {
    this.dynamicClassesRepositoryImplemented = dynamicClassesRepositoryImplemented;
  }

  private LiveLineBusinessService liveLineBusinessService;

  @Autowired
  public void setLiveLineBusinessService(LiveLineBusinessService liveLineBusinessService) {
    this.liveLineBusinessService = liveLineBusinessService;
  }


  private JavaMailSenderImpl emailSender;

  @Autowired
  public void setJavaMailSender(JavaMailSenderImpl emailSender) {
    this.emailSender = emailSender;
  }

  PasswordEncoder passwordEncoder;

  @Autowired
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  private ObjectMapper objectMapper;

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private CompanyDomainRepository companyDomainRepositoryImplemented;

  @Autowired
  public void setCompanyDomainRepositoryImplemented(
      CompanyDomainRepository companyDomainRepositoryImplemented) {
    this.companyDomainRepositoryImplemented = companyDomainRepositoryImplemented;
  }

  @Value("${project.admin.mail}")
  private String adminMail;

  /**
   * 
   * this method maps the following URL & http method URL:
   * http://hostname:port/crm-oauth2/api/customers HTTP method: GET
   * 
   */
  @RequestMapping(value = "/customers", method = RequestMethod.POST)
  public ResponseEntity<?> getCustomers() {
    // Iterable<Customer> customerList = customerService.getCustomers();
    return new ResponseEntity<>("{}", HttpStatus.OK);
  }

  /**
   * 
   * this method maps the following URL & http method URL:
   * http://hostname:port/crm-oauth2/api/customers/{customerId} HTTP method: GET
   * 
   */
  @RequestMapping(value = "/sponsors/{sponsorLogin}", method = RequestMethod.GET)
  public ResponseEntity<?> getCustomer(@PathVariable String sponsorLogin) {
    // Customer customer = customerService.getCustomer(customerId);
    return new ResponseEntity<>("{}", HttpStatus.OK);
  }

  /**
   * 
   * this method maps the following URL & http method URL:
   * http://hostname:port/crm-oauth2/api/customers HTTP method: POST
   * 
   */
  // @RequestMapping(value = "/customers", method = RequestMethod.POST)
  // public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
  // //Customer newCustomer = customerService.addCustomer(customer);
  // return new ResponseEntity<>("{}", HttpStatus.CREATED);
  // }

  /**
   * 
   * this method maps the following URL & http method URL:
   * http://hostname:port/crm-oauth2/api/customers/customerId HTTP method: PUT
   * 
   */
  // @RequestMapping(value = "/customers/{customerId}", method = RequestMethod.PUT)
  // public ResponseEntity<?> updateCustomer(@PathVariable long customerId,
  // @RequestBody Customer customer) {
  // Customer updatedCustomer = customerService.updateCustomer(customerId, customer);
  // return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
  // }

  /**
   * 
   * this method maps the following URL & http method URL:
   * http://hostname:port/crm-oauth2/api/customers/customerId HTTP method: DELETE
   * 
   */
  // @RequestMapping(value = "/customers/{customerId}", method = RequestMethod.DELETE)
  // public ResponseEntity<?> deleteCustomer(@PathVariable long customerId) {
  // Customer customer = customerService.getCustomer(customerId);
  // customerService.deleteCustomer(customer);
  // return new ResponseEntity<>(HttpStatus.OK);
  // }

  /**
   * 
   * this method maps the following URL & http method URL: http://hostname:port/appName HTTP method:
   * GET
   * 
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public ResponseEntity<?> home() {
    return new ResponseEntity<>("ERP REST API. OAuth2", HttpStatus.OK);
  }

  @RequestMapping(value = "/", method = RequestMethod.POST)
  public ResponseEntity<?> request() {
    return new ResponseEntity<>("ERP REST API. OAuth2", HttpStatus.OK);
  }

  @RequestMapping(value = "setvalue", method = RequestMethod.PUT)
  public ResponseEntity<?> setDynamicClassValue(@RequestBody Map<String, Object> request) {
    UUID systemContactId =
        managementAccessService.initUserByIncomingRequestForManagement(null, null);
    return dynamicClassPutData.setDynamicClassValue(systemContactId,
        HttpReqRespUtils.getClientIpAddressIfServletRequestExist(), request);
  }

  @RequestMapping(value = "/customer", method = RequestMethod.GET)
  public ResponseEntity<?> getCustomer() {
    UUID contactSystemId =
        managementAccessService.initUserByIncomingRequestForManagement(null, null);
    UUID classId = cacheMainParams.getSystemUserCache(contactSystemId).getContactCompanyClass();
    Map<String, Object> actualPermissions =
        cacheMainParams.getAccessPermissionsByClass(contactSystemId, classId);
    // accessRulesByUserPermissionsFilter(userDetail, currentClass);

    FastStructureModelAbstract fastStructure =
        cacheMainParams.getFastStructureCurrentClassForContact(contactSystemId, classId);
    System.out.println("actualPermissions: " + actualPermissions);
    List<Map<String, Object>> dynamicFilters = null;
    try {
      dynamicFilters = objectMapper.readValue(
          "[{\"null.null.7a38bfb3-7874-4eb4-b981-b38e5ade2df8.81613045-4bb7-4576-9752-12dc08689b7d\" : {\"value\": \""
              + cacheMainParams.getSystemUserCache(contactSystemId).getAuthorizedContact().getName()
                  .toLowerCase()
              + "\", \"operator\":\"0\"}}]",
          new TypeReference<List<Map<String, Object>>>() {});
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    List<Map<String, Object>> resultRecords = cacheMainParams.getRecordsByLocal(contactSystemId,
        classId, dynamicFilters, null, null, null);
    if (resultRecords != null && resultRecords.isEmpty()) {
      return new ResponseEntity<>(resultRecords.get(0), HttpStatus.OK);
    }
    return new ResponseEntity<>("{\"error\":\"Not found contact records.\"}",
        HttpStatus.EXPECTATION_FAILED);
  }

  @RequestMapping(value = "/recovery", method = RequestMethod.POST)
  public ResponseEntity<?> sendRecovery(HttpEntity<String> httpEntity) {
    String errorMessage = "";
    Map<String, Object> login = null;
    try {
      String workString =
          URLDecoder.decode(httpEntity.getBody(), StandardCharsets.UTF_8.toString());
      workString = workString.substring(workString.indexOf("data=") + 5);
      log.info("symbols {}: ", workString);
      login = objectMapper.readValue(workString, new TypeReference<Map<String, Object>>() {});

    } catch (NullPointerException | IOException ex) {
      log.error("erro while parsing url: {} error: {}", httpEntity.getBody(),
          ex.getLocalizedMessage());
      login = null;
    }
    if (login == null || !login.containsKey("login") || ((String) login.get("login")).length() < 4
        || ((String) login.get("login")).indexOf("'") >= 0
        || ((String) login.get("login")).indexOf("\\") >= 0) {
      errorMessage = "Unexpected symbol!";
      return new ResponseEntity<>("{\"message\":\"" + errorMessage + "\"}",
          HttpStatus.EXPECTATION_FAILED);
    }
    CompanyDomainAbstract companyDomain = companyDomainRepositoryImplemented
        .getCompanyDomainByValue(/* request.getRemoteAddr() */"liteconstruct.com");
    if (companyDomain == null)
      return new ResponseEntity<>("{\"message\":\"Domain not registered!\"}",
          HttpStatus.EXPECTATION_FAILED);
    //
    // ((List<Object>) request.get("data")).forEach(partBody -> {
    log.info("user permissions: {}", ((String) login.get("login")).toLowerCase());
    UUID currentClass = UUID.fromString("7a38bfb3-7874-4eb4-b981-b38e5ade2df8");

    UUID defaultCompanyId = companyDomain.getCompanyId();// UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b");
    UUID defaultServiceId = companyDomain.getServiceId();// UUID.fromString("b56b99b6-2c6f-4103-849a-e914e8594869");
    final UUID defaultRoleId = UUID.fromString("1c3bf8ff-7235-4400-974e-d7a3b58de566");
    final UUID defaultSudoId = UUID.fromString("7d82bde3-7740-41d7-9610-8d1fc75db803");
    DynamicRoleModelAbstract presentDynamicRole =
        dynamicRoleRepositoryImplemented.getDynamicRoleByCompanyServiceRole(defaultCompanyId,
            defaultServiceId, SystemRoles.SUPERADMIN_ROLE.getUUID());

    Map<String, Object> actualSUDOPermissions = new HashMap<>(5);
    actualSUDOPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID),
        defaultSudoId.toString());
    actualSUDOPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID),
        defaultCompanyId.toString());
    actualSUDOPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.SERVICE_ID),
        defaultServiceId.toString());
    actualSUDOPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID),
        SystemRoles.SUPERADMIN_ROLE.getId());

    actualSUDOPermissions.put(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID),
        presentDynamicRole.getId().toString());
    Map<String, Object> mapStructure = getMapStructure(currentClass, actualSUDOPermissions);

    try {
      System.out.println("actualPermissions: " + actualSUDOPermissions);
      List<Map<String, Object>> dynamicFilters = null;
      try {
        dynamicFilters = objectMapper.readValue(
            "[{\"null.null." + currentClass
                + ".81613045-4bb7-4576-9752-12dc08689b7d\" : {\"value\": \""
                + ((String) login.get("login")).toLowerCase() + "\", \"operator\":\"0\"}}]",
            new TypeReference<List<Map<String, Object>>>() {});
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      String result =
          dynamicClassesQueryFactory.getLineSubquery(currentClass, actualSUDOPermissions,
              mapStructure, null, dynamicFilters, recordsOwnerClassSettingsRepositoryImplemented
                  .getRecordsOwnerClassSettingsByclassId(defaultSudoId, currentClass),
              null, null, null);
      // System.out.println("sql: " + result);
      // dynamicClassesRepositoryImplemented.getDynamicSpaceShapeClassBySubquery(result);
      log.info("result subquery: {}", result);
      List<String> queryResult =
          dynamicClassesRepositoryImplemented.getDynamicSpaceShapeClassBySubquery(result);
      if (queryResult == null) {
        queryResult = new ArrayList<>();
        queryResult.add("Error in db.");
        return new ResponseEntity<>("{\"message\":\"" + "Error in db." + "\"}",
            HttpStatus.FORBIDDEN);
      }
      Map<String, Object> resultLine =
          objectMapper.readValue(queryResult.get(0), new TypeReference<Map<String, Object>>() {});
      Map<String, Object> contactMapStructure =
          getMapStructure(CacheMainParams.CONTACT_VALUES_CLASS, actualSUDOPermissions);
      try {
        dynamicFilters = objectMapper.readValue(
            "[{\"null.null." + CacheMainParams.CONTACT_VALUES_CLASS
                + ".060f16c7-7573-413f-8f38-fe8d4bf177aa\" : {\"value\": \""
                + ((String) login.get("login")).toLowerCase() + "\", \"operator\":\"0\"}}]",
            new TypeReference<List<Map<String, Object>>>() {});
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      String contactResult =
          dynamicClassesQueryFactory.getLineSubquery(CacheMainParams.CONTACT_VALUES_CLASS,
              actualSUDOPermissions, contactMapStructure, null, dynamicFilters,
              recordsOwnerClassSettingsRepositoryImplemented.getRecordsOwnerClassSettingsByclassId(
                  defaultSudoId, CacheMainParams.CONTACT_VALUES_CLASS),
              null, null, null);
      List<String> contactQueryResult =
          dynamicClassesRepositoryImplemented.getDynamicSpaceShapeClassBySubquery(contactResult);
      if (contactQueryResult == null) {
        contactQueryResult = new ArrayList<>();
        contactQueryResult.add("Error in db.");
        return new ResponseEntity<>("{\"message\":\"" + "Error in db." + "\"}",
            HttpStatus.FORBIDDEN);
      }
      Map<String, Object> contactResultLine = objectMapper.readValue(contactQueryResult.get(0),
          new TypeReference<Map<String, Object>>() {});


      String mail = (String) getValueByFieldId(mapStructure, resultLine,
          "9941fd7b-8272-4913-9731-8d5f90368791");
      SimpleMailMessage message = new SimpleMailMessage();
      System.out.println("try recovery password to mail: " + mail);
      if (mail == null || !mail.contains("@")) {
        return new ResponseEntity<>("{\"message\":\"This is not email!\"}", HttpStatus.BAD_REQUEST);
      }
      message.setFrom(adminMail);
      message.setTo(mail);
      message.setSubject("Password recovery.");
      String generatedString = generateCommonLangPassword();
      Map<String, Object> values = new HashMap<>(8);
      long timeStamp = System.currentTimeMillis();
      values.put("80ca0790-c30b-41ba-b74d-868943a3b9cd", (String) contactResultLine.get("1"));
      values.put("28765a7e-fd96-47eb-851f-19f54f149789", passwordEncoder.encode(generatedString));
      log.info("for password {} new bcrypt: {}", generatedString,
          values.get("28765a7e-fd96-47eb-851f-19f54f149789"));
      // dynamicClassPutData.updateRecord(CacheMainParams.CONTACT_VALUES_CLASS,
      // UUID.fromString((String) contactResultLine.get("0")), contactMapStructure, values,
      // actualSUDOPermissions, timeStamp);

      message.setText(
          "Ваш новый пароль для пользователя " + login.get("login") + ": " + generatedString);
      emailSender.send(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>("{\"message\":\"query was sended\"}", HttpStatus.OK);
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

  private Object getValueByFieldId(Map<String, Object> mapStructure, Map<String, Object> result,
      String fieldId) {
    try {
      return result.get(mapStructure.entrySet().parallelStream()
          .filter(x -> ((Map<String, Object>) x.getValue()).get("id").equals(fieldId))
          .map(x -> x.getKey()).findAny().orElse(null));
    } catch (NullPointerException ex) {
      log.error("error parsing result: {}", ExceptionUtils.exceptionStackTraceAsString(ex));
      return null;
    }
  }

  private Map<String, Object> getMapStructure(UUID currentClass,
      Map<String, Object> actualPermissions) {
    FastStructureModelAbstract fastStructure =
        fastStructureRepository.getActiveFastStructuresForClassByDynamicRole(
            UUID.fromString((String) actualPermissions
                .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID))),
            currentClass, null);
    Map<String, Object> mapStructure = null;
    try {
      mapStructure = objectMapper.readValue(fastStructure.getFastStructureJSON(),
          new TypeReference<Map<String, Object>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return mapStructure;
  }

  public String generateCommonLangPassword() {
    String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
    String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
    String numbers = RandomStringUtils.randomNumeric(2);
    // String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
    // String totalChars = RandomStringUtils.randomAlphanumeric(2);
    String combinedChars = upperCaseLetters.concat(lowerCaseLetters).concat(numbers)
    // .concat(specialChar)
    // .concat(totalChars)
    ;
    List<Character> pwdChars =
        combinedChars.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
    Collections.shuffle(pwdChars);
    String password = pwdChars.stream()
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    return password;
  }
}
