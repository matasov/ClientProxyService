package com.matas.liteconstruct.service.signup;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.config.security.SignupService;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.companycontactclass.abstractmodel.CompanyContactRelationAbstract;
import com.matas.liteconstruct.db.models.companycontactclass.repos.CompanyContactRelationRepository;
import com.matas.liteconstruct.db.models.companydomain.abstractmodel.CompanyDomainAbstract;
import com.matas.liteconstruct.db.models.companydomain.repos.CompanyDomainRepository;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepository;
import com.matas.liteconstruct.db.models.security.abstractmodel.SystemRoles;
import com.matas.liteconstruct.db.models.security.model.RegisterDTO;
import com.matas.liteconstruct.db.models.security.repos.ContactAuthRepository;
import com.matas.liteconstruct.db.models.signupfields.abstractmodel.SignupFieldsAbstract;
import com.matas.liteconstruct.db.models.signupfields.repos.SignupFieldsRepository;
import com.matas.liteconstruct.db.models.systemdictionary.abstractmodel.SystemDictionaryItemAbstract;
import com.matas.liteconstruct.db.models.systemdictionary.repos.SystemDictionaryItemRepository;
import com.matas.liteconstruct.service.SQLProtection;
import com.matas.liteconstruct.service.dynamic.CacheMainParams;
import com.matas.liteconstruct.service.dynamic.CacheSudoParams;
import com.matas.liteconstruct.service.dynamic.DynamicClassPutData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SignupServiceHelper {

  private String lang = "en";

  private DynamicClassPutData dynamicClassPutData;

  @Autowired
  public void setDynamicClassPutData(DynamicClassPutData dynamicClassPutData) {
    this.dynamicClassPutData = dynamicClassPutData;
  }

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private DynamicRoleRepository dynamicRoleRepositoryImplemented;

  @Autowired
  public void setDynamicRoleRepositoryImplemented(
      DynamicRoleRepository dynamicRoleRepositoryImplemented) {
    this.dynamicRoleRepositoryImplemented = dynamicRoleRepositoryImplemented;
  }

  private JavaMailSenderImpl emailSender;

  @Autowired
  public void setJavaMailSender(JavaMailSenderImpl emailSender) {
    this.emailSender = emailSender;
  }

  private SignupFieldsRepository signupFieldsRepository;

  @Autowired
  public void setSignupFieldsRepository(SignupFieldsRepository signupFieldsRepository) {
    this.signupFieldsRepository = signupFieldsRepository;
  }

  private SignupService signupService;

  @Autowired
  public void setSignupService(SignupService signupService) {
    this.signupService = signupService;
  }

  private ContactAuthRepository userRepository;

  @Autowired
  public void setContactAuthRepositoryImplemented(ContactAuthRepository userRepository) {
    this.userRepository = userRepository;
  }

  private ObjectMapper objectMapper;

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private PasswordEncoder passwordEncoder;

  @Autowired
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  private SystemDictionaryItemRepository systemDictionaryItemRepository;

  @Autowired
  public void setSystemDictionaryItemRepository(
      SystemDictionaryItemRepository systemDictionaryItemRepository) {
    this.systemDictionaryItemRepository = systemDictionaryItemRepository;
  }

  private CompanyDomainRepository companyDomainRepositoryImplemented;

  @Autowired
  public void setCompanyDomainRepositoryImplemented(
      CompanyDomainRepository companyDomainRepositoryImplemented) {
    this.companyDomainRepositoryImplemented = companyDomainRepositoryImplemented;
  }

  private CacheSudoParams cacheSudoParams;

  @Autowired
  public void setCacheSudoParams(CacheSudoParams cacheSudoParams) {
    this.cacheSudoParams = cacheSudoParams;
  }

  private CacheMainParams cacheMainParams;

  @Autowired
  public void setCacheMainParams(CacheMainParams cacheMainParams) {
    this.cacheMainParams = cacheMainParams;
  }

  private CompanyContactRelationRepository companyContactRelationRepository;

  @Autowired
  public void setCompanyContactRelationRepository(
      CompanyContactRelationRepository companyContactRelationRepository) {
    this.companyContactRelationRepository = companyContactRelationRepository;
  }

  private final String keyCollectionCase = null;

  public String signupRequestProcessor(String request) throws NullPointerException {
    log.info("signupRequestProcessor request: {}", request);
    Map<String, Object> entityContactRegistered = null;
    try {
      String workString = URLDecoder.decode(request, StandardCharsets.UTF_8.toString());
      workString = workString.substring(workString.indexOf("data=") + 5);
      if (workString.lastIndexOf("}") < workString.length() - 1) {
        log.info("try trim {}, {}", workString.lastIndexOf("}"), workString.length());
        workString = workString.substring(0, workString.lastIndexOf("}") + 1);
      }
      entityContactRegistered =
          objectMapper.readValue(workString, new TypeReference<Map<String, Object>>() {});
      log.info("log map: {}", entityContactRegistered);
    } catch (NullPointerException | IOException ex) {
      ex.printStackTrace();
      entityContactRegistered = null;
    }

    // check injection
    Map<String, Object> checkedInput = SQLProtection.protectMap(entityContactRegistered);
    String login = checkStringValueParams((String) checkedInput.get("login"), 4, true, true);
    if (login.indexOf("\\") >= 0 || login.indexOf("'") >= 0)
      throw new NullPointerException("Unexpected symbol.");
    String insertedEmail =
        checkStringValueParams((String) checkedInput.get("email"), 4, true, true);
    if (insertedEmail == null)
      throw new NullPointerException("Unexpected email.");
    String domainValue;
    if (checkedInput.get("domain") == null) {
      domainValue = "liteconstruct.com";
    } else {
      domainValue = (String) checkedInput.get("domain");
    }
    CompanyDomainAbstract domainObject =
        companyDomainRepositoryImplemented.getCompanyDomainByValue(domainValue);
    if (domainObject == null)
      throw new NullPointerException("wrong domain.");

    CompanyContactRelationAbstract companyClassRelation = companyContactRelationRepository
        .getCompanyContactRelationByCompanyId(domainObject.getCompanyId());
    if (companyClassRelation == null)
      throw new NullPointerException("wrong relation.");
    UUID companyContactclassId = companyClassRelation.getClassId();
    SignupFieldsAbstract signupFieldsGeneral =
        signupFieldsRepository.getSignupFieldsByClass(UUID.fromString(DBConstants.CONTACT_ID));
    SignupFieldsAbstract signupFields =
        signupFieldsRepository.getSignupFieldsByClass(companyContactclassId);
    if (signupFields == null)
      throw new NullPointerException("Not found signup fields.");
    cacheSudoParams.initSudoUser(keyCollectionCase);
    Map<String, Object> companyRecord = getCompanyRecordById(domainObject.getCompanyId());
    if (companyRecord == null)
      throw new NullPointerException("not found company by domain.");

    // search name
    int indexName = cacheMainParams.getIndexFieldInStructure(
        UUID.fromString("6235200f-5c07-4aa3-8ead-ff37c2317a4b"),
        cacheSudoParams.getSUDOFastStructureForclassId(UUID.fromString(DBConstants.COMPANY_ID),
            keyCollectionCase));
    String companyName = (String) companyRecord.get(Integer.toString(indexName));
    // search email
    int indexAdminEmail = cacheMainParams.getIndexFieldInStructure(
        UUID.fromString("05711c05-ba59-386e-7464-76458250c2cf"),
        cacheSudoParams.getSUDOFastStructureForclassId(UUID.fromString(DBConstants.COMPANY_ID),
            keyCollectionCase));

    String emailAdmin = (String) companyRecord.get(Integer.toString(indexAdminEmail));

    String passwordToSave =
        passwordProcessor(login, emailAdmin, insertedEmail, (String) checkedInput.get("password"),
            (String) checkedInput.get("repeatPassword"), domainObject.getCompanyId(), companyName);
    if (passwordToSave == null) {
      throw new NullPointerException("Bad password.");
    }
    Map<String, Object> presentedLogin = getContactRecordByLogin(login);
    if (presentedLogin != null)
      throw new NullPointerException("Login is present yet.");
    // sponsor block
    String sponsor;
    if (checkedInput.get("sponsor") != null) {
      sponsor = checkStringValueParams((String) checkedInput.get("sponsor"), 4, true, true);
      Map<String, Object> presentedSponsor = getContactRecordByLogin(sponsor);
      if (presentedSponsor == null)
        throw new NullPointerException("Sponsor not found.");
    } else {
      sponsor = null;
    }
    // get role id
    UUID roleId;
    if (checkedInput.get("role_id") != null) {
      try {
        roleId = UUID.fromString((String) checkedInput.get("role_id"));
      } catch (Exception ex) {
        log.error("error in parse login: {}", ex.getMessage());
        roleId = SystemRoles.SYSTEM_USER.getUUID();
      }
    } else {
      roleId = SystemRoles.SYSTEM_USER.getUUID();
    }
    UUID newContactId = UUID.randomUUID();
    UUID newContactOwnerId = UUID.randomUUID();
    long timeStamp = System.currentTimeMillis();
    Map<String, Object> sudoMapStructure = cacheSudoParams
        .getSUDOFastStructureForclassId(UUID.fromString(DBConstants.CONTACT_ID), keyCollectionCase);
    // insert to contact
    Map<String, Object> contactData = new HashMap() {
      {
        put("d2a47321-e0da-4ee5-bc76-110a4e67090c", newContactId);
        put("80ca0790-c30b-41ba-b74d-868943a3b9cd", newContactOwnerId);
        put("060f16c7-7573-413f-8f38-fe8d4bf177aa", login);
        put("53e81f50-1465-408e-b777-4b81961e4e6e", insertedEmail);
        put("28765a7e-fd96-47eb-851f-19f54f149789", passwordEncoder.encode(passwordToSave));
      }
    };
    try {
      if (signupFieldsGeneral != null)
        signupFieldsGeneral.getFieldsRelations().entrySet().parallelStream()
            .filter(item -> checkStringValueParams((String) checkedInput.get(item.getKey()), 4,
                true, false) != null && !contactData.containsKey(item.getValue().toString())
                && cacheMainParams.getIndexFieldInStructure(item.getValue(), sudoMapStructure) > 3)
            .forEach(item -> {
              contactData.put(item.getValue().toString(),
                  checkStringValueParams((String) checkedInput.get(item.getKey()), 4, true, false));
            });
    } catch (ClassCastException cex) {
      log.error("cast exception: {}", cex.getLocalizedMessage());
    }
    // adds
    // TODO
    dynamicClassPutData.insertNewRecord(UUID.fromString(DBConstants.CONTACT_ID), newContactId,
        sudoMapStructure, sudoMapStructure, contactData, cacheSudoParams.getSUDOPermissions(),
        timeStamp);
    // insert to company contact
    UUID newContactCompanyId = UUID.randomUUID();
    Map<String, Object> companyContactSudoMapStructure =
        cacheSudoParams.getSUDOFastStructureForclassId(companyContactclassId, keyCollectionCase);
    Map<String, Object> companyContactData = new HashMap() {
      {
        put(((Map<String, Object>) companyContactSudoMapStructure.get("0")).get("id").toString(),
            newContactCompanyId);
        put(((Map<String, Object>) companyContactSudoMapStructure.get("1")).get("id").toString(),
            newContactOwnerId);
        put(signupFields.getFieldByKey("login").toString(), login);
        put(signupFields.getFieldByKey("email").toString(), insertedEmail);
      }
    };
    // adds
    if (sponsor != null && signupFields.getFieldByKey("sponsor") != null) {
      companyContactData.put(signupFields.getFieldByKey("sponsor").toString(), sponsor);
    }
    try {
      signupFields.getFieldsRelations().entrySet().parallelStream()
          // .peek(item -> log.info("signup field: {}", item))
          .filter(item -> checkStringValueParams((String) checkedInput.get(item.getKey()), 4, true,
              false) != null && !companyContactData.containsKey(item.getValue().toString())
              && cacheMainParams.getIndexFieldInStructure(item.getValue(),
                  companyContactSudoMapStructure) > 3)
          .forEach(item -> {
            log.info("try insert to values list: {}", item);
            companyContactData.put(item.getValue().toString(),
                checkStringValueParams((String) checkedInput.get(item.getKey()), 4, true, false));
          });
    } catch (ClassCastException cex) {
      log.error("cast exception: {}", cex.getLocalizedMessage());
    }
    // log.info("before add data to companycontact: {}", companyContactData);
    // dynamicClassPutData.insertNewRecord(companyContactclassId, newContactCompanyId,
    // companyContactSudoMapStructure, companyContactSudoMapStructure, companyContactData,
    // cacheSudoParams.getSUDOPermissions(), timeStamp);
    signupToLocalAfterChecked(companyContactclassId, companyContactData,
        companyContactSudoMapStructure);
    // insert to owner
    String sqlRecordOwner =
        String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s')",
            DBConstants.TBL_RECORD_OWNER, newContactOwnerId, newContactId,
            domainObject.getCompanyId(), domainObject.getServiceId(), roleId);
    jdbcTemplate.update(sqlRecordOwner);
    // dynamic role
    DynamicRoleModelAbstract presentDynamicRole =
        dynamicRoleRepositoryImplemented.getDynamicRoleByCompanyServiceRole(
            domainObject.getCompanyId(), domainObject.getServiceId(), roleId);

    if (presentDynamicRole == null) {
      throw new NullPointerException("Not found value for dynamic role.");
    }
    String sqlDynamicRoleContact = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s')",
        DBConstants.REGISTRY_DYNAMIC_ROLE_CONTACT, UUID.randomUUID(), presentDynamicRole.getId(),
        newContactId);
    jdbcTemplate.update(sqlDynamicRoleContact);
    return null;
  }

  public void signupToLocalAfterChecked(UUID companyContactclassId,
      Map<String, Object> checkedAndParsedData,
      Map<String, Object> companyContactSudoMapStructure) {
    log.info("before add data to companycontact: {}", checkedAndParsedData);
    Object companyContactRecordId = checkedAndParsedData
        .get(((Map<String, Object>) companyContactSudoMapStructure.get("0")).get("id").toString());
    dynamicClassPutData.insertNewRecord(companyContactclassId,
        companyContactRecordId instanceof String ? UUID.fromString((String) companyContactRecordId)
            : (UUID) companyContactRecordId,
        companyContactSudoMapStructure, companyContactSudoMapStructure, checkedAndParsedData,
        cacheSudoParams.getSUDOPermissions(), System.currentTimeMillis());
  }

  public Map<String, Object> getCompanyRecordById(UUID companyId) throws NullPointerException {
    List<Map<String, Object>> dynamicFilters = null;
    Map<String, Object> sudoMapStructure = cacheSudoParams
        .getSUDOFastStructureForclassId(UUID.fromString(DBConstants.COMPANY_ID), keyCollectionCase);
    try {
      dynamicFilters = objectMapper.readValue(
          "[{\"null.null." + DBConstants.COMPANY_ID + "."
              + ((Map<String, Object>) sudoMapStructure.get("0")).get("id") + "\" : {\"value\": \""
              + companyId + "\", \"operator\":\"0\"}}]",
          new TypeReference<List<Map<String, Object>>>() {});
    } catch (IOException e) {
      dynamicFilters = null;
      throw new NullPointerException("Not found structure.");
    }
    List<Map<String, Object>> result = cacheMainParams.getRecordsByExternalMapStructure(
        CacheMainParams.SUDO_CONTACT_ID, UUID.fromString(DBConstants.COMPANY_ID), sudoMapStructure,
        dynamicFilters, null, null, null);
    return result == null ? null : result.get(0);
  }

  private Map<String, Object> getContactRecordByLogin(String login) {
    List<Map<String, Object>> dynamicFilters = null;
    Map<String, Object> sudoMapStructure = cacheSudoParams
        .getSUDOFastStructureForclassId(UUID.fromString(DBConstants.CONTACT_ID), keyCollectionCase);
    int indexName = cacheMainParams.getIndexFieldInStructure(
        UUID.fromString("060f16c7-7573-413f-8f38-fe8d4bf177aa"), sudoMapStructure);
    try {
      dynamicFilters =
          objectMapper.readValue(
              "[{\"null.null." + DBConstants.CONTACT_ID + "."
                  + ((Map<String, Object>) sudoMapStructure.get(Integer.toString(indexName)))
                      .get("id")
                  + "\" : {\"value\": \"" + login + "\", \"operator\":\"0\"}}]",
              new TypeReference<List<Map<String, Object>>>() {});
    } catch (IOException e) {
      dynamicFilters = null;
      throw new NullPointerException("Not found structure.");
    }
    List<Map<String, Object>> result = cacheMainParams.getRecordsByExternalMapStructure(
        CacheMainParams.SUDO_CONTACT_ID, UUID.fromString(DBConstants.CONTACT_ID), sudoMapStructure,
        dynamicFilters, null, null, null);
    return result == null || result.isEmpty() || result.get(0) == null ? null : result.get(0);
  }

  private String passwordProcessor(String login, String email, String adminMail, String password,
      String repeatPassword, UUID companyId, String companyName) throws NullPointerException {
    String resultPassword;
    if (password == null || password.equals("")) {
      resultPassword = generateCommonLangPassword();
      // create new password and send to email
      SystemDictionaryItemAbstract letterTitle = systemDictionaryItemRepository
          .getSystemDictionaryItemByParamsLongWay(companyId, lang, "registration_password_title");
      SystemDictionaryItemAbstract letterText = systemDictionaryItemRepository
          .getSystemDictionaryItemByParamsLongWay(companyId, lang, "registration_password_letter");
      if (letterTitle == null || letterText == null)
        throw new NullPointerException("Not found dictionary settings for company. " + companyName);
      SimpleMailMessage message = new SimpleMailMessage();
      if (email == null || !email.contains("@")) {
        throw new NullPointerException("wrong email!");
      }
      message.setFrom(adminMail);
      message.setTo(email);

      message.setSubject(String.format(letterTitle.getValue(), companyName));
      // Map<String, Object> values = new HashMap<>(8);

      message.setText(String.format(letterTitle.getValue(), login, resultPassword));
      emailSender.send(message);
    } else {
      resultPassword = checkStringValueParams(password, 6, true, false);
      log.info("password: {}, repeat: {}", checkStringValueParams(password, 6, true, false),
          checkStringValueParams(repeatPassword, 6, true, false));
      if (!resultPassword.equals(checkStringValueParams(repeatPassword, 6, true, false)))
        throw new NullPointerException("Passwords is not equals!");
    }
    return resultPassword;
  }

  private String checkStringValueParams(String value, int minLength, boolean trim,
      boolean lowerCase) throws NullPointerException {
    if (value == null)
      return null;
    String workString = trim ? value.trim() : value;
    return workString.length() >= minLength ? workString : null;
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

  public UUID insertNewUserToSystem(RegisterDTO contactRegistered, UUID companyId, UUID serviceId)
      throws NullPointerException {
    boolean isPresentContact = userRepository.checkContactAuthByIdOrLogin(
        contactRegistered.getId().toString(), contactRegistered.getLogin());
    if (isPresentContact) {
      throw new NullPointerException("User " + contactRegistered.getLogin() + " is presented.");
    } else {
      // update any role to customer in register!!!!! For security...
      contactRegistered.setRoleId("1c3bf8ff-7235-4400-974e-d7a3b58de566");

      try {
        return signupService.addUser(contactRegistered.getContactAuth(companyId, serviceId,
            UUID.fromString(contactRegistered.getRoleId())));
      } catch (NullPointerException nex) {
        nex.printStackTrace();
        throw new NullPointerException("Error in query to DB!");
      }
    }
  }

  private UUID getUUIDFromStringChecked(String uuid) {
    try {
      return UUID.fromString(uuid);
    } catch (Exception ex) {
      return null;
    }
  }
}
