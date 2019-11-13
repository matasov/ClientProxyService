package com.matas.liteconstruct.service.business;

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
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.db.models.dynamicclass.abstractmodel.DynamicClassRecordLineAbstract;
import com.matas.liteconstruct.db.models.dynamicclass.queryfactory.DynamicClassesQueryFactory;
import com.matas.liteconstruct.db.models.dynamicclass.repos.DynamicClassesRepository;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepository;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepository;
import com.matas.liteconstruct.db.models.recordowner.abstractmodel.RecordsOwnerModelAbstract;
import com.matas.liteconstruct.db.models.recordowner.repos.RecordsOwnerClassSettingsRepository;
import com.matas.liteconstruct.db.models.recordowner.repos.RecordsOwnerRepository;
import com.matas.liteconstruct.service.dynamic.DynamicClassPutData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LiveLineBusinessService {

  PasswordEncoder passwordEncoder;

  @Autowired
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  private DynamicClassPutData dynamicClassPutData;

  @Autowired
  public void setDynamicClassPutData(DynamicClassPutData dynamicClassPutData) {
    this.dynamicClassPutData = dynamicClassPutData;
  }

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

  DynamicRoleRepository dynamicRoleRepository;

  @Autowired
  public void setDynamicRoleRepositoryImplemented(
      DynamicRoleRepository dynamicRoleRepository) {
    this.dynamicRoleRepository = dynamicRoleRepository;
  }

  RecordsOwnerRepository recordsOwnerRepositoryImplemented;

  @Autowired
  public void setRecordsOwnerRepositoryImplemented(
      RecordsOwnerRepository recordsOwnerRepositoryImplemented) {
    this.recordsOwnerRepositoryImplemented = recordsOwnerRepositoryImplemented;
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

  private JavaMailSenderImpl emailSender;

  @Autowired
  public void setJavaMailSender(JavaMailSenderImpl emailSender) {
    this.emailSender = emailSender;
  }

  @Value("${project.admin.mail}")
  private String adminMail;

  private List<String> getUserIdByLogin(UUID userValuesClass,
      Map<String, Object> liveLineContactMapStructure, String userName) {
    String success = null;
    String error = null;
    Map<String, Object> conditions = new HashMap<>(1);
    conditions.put("81613045-4bb7-4576-9752-12dc08689b7d", userName.toLowerCase());

    DynamicClassRecordLineAbstract forUserCustomRecord = dynamicClassPutData
        .getRealUserIdByLogin(userValuesClass, liveLineContactMapStructure, conditions);
    if (forUserCustomRecord == null) {
      error = "Not found company user for login: " + userName;
    } else {
      RecordsOwnerModelAbstract forRecordOwner = recordsOwnerRepositoryImplemented
          .getRecordsOwnerById(forUserCustomRecord.getOwnerRecordId());
      if (forRecordOwner == null) {
        error = "Not found registered user for login: " + userName;
      } else {
        UUID forLiveLineUserId = forRecordOwner.getContactId();
        System.out.println("found recordOwner: " + forRecordOwner);
        success = forLiveLineUserId.toString();
      }
    }
    List<String> result = new ArrayList<>();
    result.add(success);
    result.add(error);
    return result;
  }

  private Map<String, Object> createNewStatisticsRecord(UUID forLiveLineUserId,
      UUID byLiveLineUserId, UUID recordOwner, double oldValue, double newValue, String description,
      UUID forField, int type) {
    Map<String, Object> values = new HashMap<>(8);
    values.put("68abc071-958c-4d9e-b54f-0e608bf05cb3", recordOwner.toString());
    values.put("69dc525a-aa0c-4717-9b5b-c4d18b5716c2", byLiveLineUserId.toString());
    values.put("5f15318c-abfb-40ac-b5a9-63a2b684237e", forLiveLineUserId.toString());
    values.put("ceff3761-022c-4814-82d6-5bf3991033c2", forField.toString());
    values.put("df3cd742-f9db-4280-ae51-f3fd1304f415", Double.toString(oldValue));
    values.put("1ff929b6-6ce2-45a7-8dab-d53249082eac", Double.toString(newValue));
    values.put("e4472832-0093-4575-af5f-33fb869355ef", description.toString());
    values.put("c59522be-f5aa-43ad-96d7-cb316561f189", Integer.toString(type));

    return values;
  }

  private Map<String, Object> createNewMatrixRecord(UUID liveLineUserId, UUID recordOwner, int type,
      String firstString, String secondString, String thirdString, UUID parentLevel,
      boolean isEnabled) {
    Map<String, Object> values = new HashMap<>(8);
    values.put("00e64bfb-c0b4-4782-92cf-6de68ba94157", recordOwner.toString());
    values.put("9bcd4349-8427-4597-8522-68c928893374", liveLineUserId.toString());
    values.put("9bb33f86-b83b-4419-b532-c910600afc47", Integer.toString(type));
    values.put("2a2aaf1b-52f1-4612-a18e-2ebc6964df46", firstString);
    values.put("112bf798-63c9-46af-88c5-29d19abfdff8", secondString);
    values.put("f76f3920-13b3-4193-b767-17e61a46c609", thirdString);
    if (parentLevel != null)
      values.put("c9c8beb5-99ad-4816-a78b-72376d0d9090", parentLevel.toString());
    values.put("ce98a775-5dfe-437f-bad6-65f4793d8567", isEnabled ? "true" : "false");

    return values;
  }

  public String transferToLogin(String forLiveLineUserLogin, UUID byLiveLineUserId,
      double addtionalValue, UUID forField, boolean isSubZero, int type) {
    return null;
    // long timeStamp = System.currentTimeMillis();
    // List<String> loginForResults = getUserIdByLogin(SUDOPermissions.USER_VALUES_CLASS,
    // sudoPermissions.getLiveLineContactMapStructure(), forLiveLineUserLogin);
    // if (loginForResults == null || loginForResults.get(1) != null) {
    // return "Can't find for user. " + loginForResults.get(1);
    // }
    //
    // UUID forLiveLineUserId = UUID.fromString(loginForResults.get(0));
    // log.info("from: {} to: {}", forLiveLineUserId, byLiveLineUserId);
    //
    // if (forLiveLineUserId.equals(byLiveLineUserId)) {
    // return "transfer to same login. exit.";
    // }
    // // record value transmitter
    // DynamicClassRecordLineAbstract oldRecordValueTransmitter =
    // dynamicClassPutData.getDynamicClassesRecordByOverOwnerId(SUDOPermissions.USER_VALUES_CLASS,
    // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // byLiveLineUserId, sudoPermissions.getCustomerDynamicRole()),
    // sudoPermissions.getLiveLineContactMapStructure());
    //
    // if (oldRecordValueTransmitter == null)
    // return "Not found user transmitter. exit.";
    //
    // double oldValueTransmitter;
    // try {
    // oldValueTransmitter =
    // (double) oldRecordValueTransmitter.getFieldValues().get(forField.toString());
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // oldValueTransmitter = 0;
    // }
    //
    // // record value receiver
    // DynamicClassRecordLineAbstract oldRecordValueReceiver =
    // dynamicClassPutData.getDynamicClassesRecordByOverOwnerId(SUDOPermissions.USER_VALUES_CLASS,
    // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // forLiveLineUserId, sudoPermissions.getCustomerDynamicRole()),
    // sudoPermissions.getLiveLineContactMapStructure());
    // if (oldRecordValueReceiver == null)
    // return "Not found user receiver. exit.";
    // double oldValueReceiver;
    // try {
    // oldValueReceiver = (double) oldRecordValueReceiver.getFieldValues().get(forField.toString());
    // } catch (Exception ex) {
    // oldValueReceiver = 0;
    // }
    //
    // if (!isSubZero && (addtionalValue <= 0 || oldValueTransmitter - addtionalValue < 0))
    // return "Result less then zero. exit.";
    // RecordsOwnerModelAbstract realOwnerTransmitter = dynamicClassPutData.getRecordsOwner(
    // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // byLiveLineUserId, sudoPermissions.getCustomerDynamicRole()));
    // RecordsOwnerModelAbstract realOwnerReceiver = dynamicClassPutData.getRecordsOwner(
    // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // forLiveLineUserId, sudoPermissions.getCustomerDynamicRole()));
    //
    // dynamicClassPutData.insertNewRecord(SUDOPermissions.USER_STATISTIC_CLASS, UUID.randomUUID(),
    // sudoPermissions.getLiveLineStatisticMapStructure(),
    // createNewStatisticsRecord(oldRecordValueTransmitter.getRecordId(),
    // oldRecordValueTransmitter.getRecordId(), realOwnerReceiver.getId(), oldValueTransmitter,
    // getCurrencyCeilForResult(oldValueTransmitter - addtionalValue),
    // getDescriptionFormatString("transferOut", Double.toString(addtionalValue),
    // forLiveLineUserLogin.toLowerCase()),
    // forField, type),
    // sudoPermissions.getActualSudoPermissions(), timeStamp);
    // dynamicClassPutData.insertNewRecord(SUDOPermissions.USER_STATISTIC_CLASS, UUID.randomUUID(),
    // sudoPermissions.getLiveLineStatisticMapStructure(),
    // createNewStatisticsRecord(oldRecordValueReceiver.getRecordId(),
    // oldRecordValueReceiver.getRecordId(), realOwnerTransmitter.getId(), oldValueReceiver,
    // getCurrencyCeilForResult(oldValueReceiver + addtionalValue),
    // getDescriptionFormatString("transferIn", Double.toString(addtionalValue),
    // ((UserDetails) SecurityContextHolder.getContext().getAuthentication()
    // .getPrincipal()).getUsername()),
    // forField, type),
    // sudoPermissions.getActualSudoPermissions(), timeStamp);
    // Map<String, Object> values = new HashMap<>(2);
    // values.put(forField.toString(),
    // Double.toString(getCurrencyCeilForResult(oldValueTransmitter - addtionalValue)));
    // values.put(
    // (String) ((Map<String, Object>) sudoPermissions.getLiveLineContactMapStructure().get("1"))
    // .get("id"),
    // realOwnerTransmitter.getId().toString());
    // Map<String, Object> fieldValues = new HashMap<>(1);
    // fieldValues.put("fields", values);
    // dynamicClassPutData.updateDbStaffLog(SUDOPermissions.USER_VALUES_CLASS,
    // oldRecordValueTransmitter.getRecordId(), timeStamp,
    // sudoPermissions.getActualSudoPermissions(), fieldValues,
    // sudoPermissions.getLiveLineContactMapStructure());
    // dynamicClassPutData.updateRecord(SUDOPermissions.USER_VALUES_CLASS,
    // oldRecordValueTransmitter.getRecordId(), sudoPermissions.getLiveLineContactMapStructure(),
    // values, sudoPermissions.getActualSudoPermissions(), timeStamp);
    //
    // values.put(forField.toString(),
    // Double.toString(getCurrencyCeilForResult(oldValueReceiver + addtionalValue)));
    // values.put(
    // (String) ((Map<String, Object>) sudoPermissions.getLiveLineContactMapStructure().get("1"))
    // .get("id"),
    // realOwnerReceiver.getId().toString());
    // fieldValues.put("fields", values);
    // dynamicClassPutData.updateDbStaffLog(SUDOPermissions.USER_VALUES_CLASS,
    // oldRecordValueReceiver.getRecordId(), timeStamp, sudoPermissions.getActualSudoPermissions(),
    // fieldValues, sudoPermissions.getLiveLineContactMapStructure());
    // dynamicClassPutData.updateRecord(SUDOPermissions.USER_VALUES_CLASS,
    // oldRecordValueReceiver.getRecordId(), sudoPermissions.getLiveLineContactMapStructure(),
    // values, sudoPermissions.getActualSudoPermissions(), timeStamp);
    // return null;
  }

  public String singleDebit(UUID byLiveLineUserId, double additionalValue, UUID forField,
      boolean isSubZero, int type, String... debitArgs) {

    // if (!isSubZero && additionalValue < 0) {
    // return "Result less then zero. exit.";
    // }
    //
    // long timeStamp = System.currentTimeMillis();
    // // system permissions
    // RecordsOwnerModelAbstract realOwnerBuyer = dynamicClassPutData.getRecordsOwner(
    // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // byLiveLineUserId, sudoPermissions.getCustomerDynamicRole()));
    //
    // // record value receiver
    // DynamicClassRecordLineAbstract oldRecordValueBuyer =
    // dynamicClassPutData.getDynamicClassesRecordByOverOwnerId(SUDOPermissions.USER_VALUES_CLASS,
    // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // byLiveLineUserId, sudoPermissions.getCustomerDynamicRole()),
    // sudoPermissions.getLiveLineContactMapStructure());
    // if (oldRecordValueBuyer == null)
    // return "Not found user receiver. exit.";
    // double oldValueBuyer;
    // try {
    // oldValueBuyer = (double) oldRecordValueBuyer.getFieldValues().get(forField.toString());
    // } catch (Exception ex) {
    // oldValueBuyer = 0;
    // }
    // if (!isSubZero && (additionalValue < 0 || oldValueBuyer - additionalValue < 0))
    // return "Result less then zero. exit.";
    // String descriptionValue = "";
    // switch (debitArgs[0]) {
    // case "payout":
    // descriptionValue = getDescriptionFormatString(debitArgs[0],
    // Double.toString(additionalValue), getPaySystemById(debitArgs[1]),
    // (String) oldRecordValueBuyer.getFieldValues().get(debitArgs[1]));
    // try {
    // SimpleMailMessage message = new SimpleMailMessage();
    // message.setFrom(adminMail);
    // message.setTo("alikatoy@gmail.com");
    // message.setSubject("Live Line вывод средств. "
    // + oldRecordValueBuyer.getFieldValues().get("81613045-4bb7-4576-9752-12dc08689b7d"));
    // message.setText("Пользователь: "
    // + oldRecordValueBuyer.getFieldValues().get("81613045-4bb7-4576-9752-12dc08689b7d")
    // + ". " + descriptionValue);
    // emailSender.send(message);
    // } catch (Exception ex) {
    // log.error("Error in messendger. exit. {}", ex.getLocalizedMessage());
    // }
    // break;
    // case "buy":
    // case "start":
    // descriptionValue = getDescriptionFormatString(debitArgs[0], debitArgs[1]);
    // break;
    // case "tech":
    // descriptionValue = getDescriptionFormatString(debitArgs[0]);
    // break;
    // }
    // Map<String, Object> values = new HashMap<>(2);
    // Map<String, Object> fieldValues = new HashMap<>(1);
    // values.put(forField.toString(),
    // Double.toString(getCurrencyCeilForResult(oldValueBuyer - additionalValue)));
    // values.put(
    // (String) ((Map<String, Object>) sudoPermissions.getLiveLineContactMapStructure().get("1"))
    // .get("id"),
    // realOwnerBuyer.getId().toString());
    // fieldValues.put("fields", values);
    // dynamicClassPutData.insertNewRecord(SUDOPermissions.USER_STATISTIC_CLASS, UUID.randomUUID(),
    // sudoPermissions.getLiveLineStatisticMapStructure(),
    // createNewStatisticsRecord(oldRecordValueBuyer.getRecordId(),
    // oldRecordValueBuyer.getRecordId(), realOwnerBuyer.getId(), oldValueBuyer,
    // getCurrencyCeilForResult(oldValueBuyer - additionalValue), descriptionValue, forField,
    // type),
    // sudoPermissions.getActualSudoPermissions(), timeStamp);
    // dynamicClassPutData.updateDbStaffLog(SUDOPermissions.USER_VALUES_CLASS,
    // oldRecordValueBuyer.getRecordId(), timeStamp, sudoPermissions.getActualSudoPermissions(),
    // fieldValues, sudoPermissions.getLiveLineContactMapStructure());
    // dynamicClassPutData.updateRecord(SUDOPermissions.USER_VALUES_CLASS,
    // oldRecordValueBuyer.getRecordId(), sudoPermissions.getLiveLineContactMapStructure(), values,
    // sudoPermissions.getActualSudoPermissions(), timeStamp);
    return null;
  }

  public String singleCredit(UUID byLiveLineUserId, double additionalValue, UUID forField,
      boolean isSubZero, int type, String... creditArgs) {
    return singleCredit(null, byLiveLineUserId, additionalValue, forField, isSubZero, type,
        creditArgs);
  }

  public String singleCredit(String name, UUID byLiveLineUserId, double additionalValue,
      UUID forField, boolean isSubZero, int type, String... creditArgs) {

    // if (!isSubZero && additionalValue <= 0) {
    // return "Result less then zero. exit.";
    // }
    // long timeStamp = System.currentTimeMillis();
    // // system permissions
    // RecordsOwnerModelAbstract realOwnerContact = dynamicClassPutData.getRecordsOwner(
    // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // byLiveLineUserId, sudoPermissions.getCustomerDynamicRole()));
    //
    // // record value receiver
    // DynamicClassRecordLineAbstract oldRecordValueContact =
    // dynamicClassPutData.getDynamicClassesRecordByOverOwnerId(SUDOPermissions.USER_VALUES_CLASS,
    // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // byLiveLineUserId, sudoPermissions.getCustomerDynamicRole()),
    // sudoPermissions.getLiveLineContactMapStructure());
    // if (oldRecordValueContact == null)
    // return "Not found user receiver. exit.";
    // double oldValueBuyer;
    // try {
    // oldValueBuyer = (double) oldRecordValueContact.getFieldValues().get(forField.toString());
    // } catch (Exception ex) {
    // oldValueBuyer = 0;
    // }
    // if (!isSubZero && (additionalValue <= 0 || oldValueBuyer + additionalValue < 0))
    // return "Result less then zero. exit.";
    // String descriptionValue = "";
    // switch (creditArgs[0]) {
    // case "payment":
    // descriptionValue = getDescriptionFormatString(creditArgs[0],
    // Double.toString(additionalValue), creditArgs[1], getPaySystemById(creditArgs[2]));
    // break;
    // case "salary":
    // descriptionValue = getDescriptionFormatString(creditArgs[0],
    // Double.toString(additionalValue), creditArgs[1]);
    // break;
    // case "referral":
    // descriptionValue = getDescriptionFormatString(creditArgs[0],
    // Double.toString(additionalValue), creditArgs[1], creditArgs[2]);
    // break;
    // }
    // Map<String, Object> values = new HashMap<>(2);
    // Map<String, Object> fieldValues = new HashMap<>(1);
    // values.put(forField.toString(),
    // Double.toString(getCurrencyCeilForResult(oldValueBuyer + additionalValue)));
    // values.put(
    // (String) ((Map<String, Object>) sudoPermissions.getLiveLineContactMapStructure().get("1"))
    // .get("id"),
    // realOwnerContact.getId().toString());
    // fieldValues.put("fields", values);
    // dynamicClassPutData.insertNewRecord(SUDOPermissions.USER_STATISTIC_CLASS, UUID.randomUUID(),
    // sudoPermissions.getLiveLineStatisticMapStructure(),
    // createNewStatisticsRecord(oldRecordValueContact.getRecordId(),
    // oldRecordValueContact.getRecordId(), realOwnerContact.getId(), oldValueBuyer,
    // getCurrencyCeilForResult(oldValueBuyer + additionalValue), descriptionValue, forField,
    // type),
    // sudoPermissions.getActualSudoPermissions(), timeStamp);
    // dynamicClassPutData.updateDbStaffLog(SUDOPermissions.USER_VALUES_CLASS,
    // oldRecordValueContact.getRecordId(), timeStamp, sudoPermissions.getActualSudoPermissions(),
    // fieldValues, sudoPermissions.getLiveLineContactMapStructure());
    // dynamicClassPutData.updateRecord(SUDOPermissions.USER_VALUES_CLASS,
    // oldRecordValueContact.getRecordId(), sudoPermissions.getLiveLineContactMapStructure(),
    // values, sudoPermissions.getActualSudoPermissions(), timeStamp);
    return null;
  }

  public String updateErrorSymbols(String extValue) {
    return extValue.replace("\"", "\\\"").replace("'", "\'");
  }

  public String getDescriptionFormatString(String type, String... tokens) {
    log.info("getDescriptionFormatString from: {}", tokens);
    switch (type) {
      case "transferOut":
        return String.format("Средства %1$s отправлены пользователю %2$s", tokens[0], tokens[1]);
      case "transferIn":
        return String.format("Средства %1$s получены от пользователя %2$s", tokens[0], tokens[1]);
      case "payment":
        return String.format(
            "Зачисление %1$s от пользователя с логином %2$s через платежную систему %3$s",
            tokens[0], tokens[1], tokens[2]);
      case "payout":
        return String.format("Вывод %1$s через платежную систему %2$s на кошелек %3$s", tokens[0],
            tokens[1], tokens[2]);
      case "buy":
        return String.format("Покупка места в уровне %1$s", tokens[0]);
      case "salary":
        return String.format("Начисление %1$s за закрытие уровня %2$s", tokens[0], tokens[1]);
      case "referral":
        return String.format("Реферальные начисление %1$s (логин %2$s  уровень %3$s)", tokens[0],
            tokens[1], tokens[2]);
      case "tech":
        return String.format("Покупка тех. акк.");
      case "start":
        return String.format("Административный сбор.");
      default:
        return null;
    }
  }

  public String updateMatrix(UUID insertedUserId, String level, UUID fromMatrix) {
    String errorMessage = singleDebit(insertedUserId, 10,
        UUID.fromString("fb2a7511-99e4-4418-8e18-18deb93f5e72"), false, 2, "buy", "1");
    if (errorMessage == null || errorMessage.equals("null")) {
      errorMessage = null;
    }
    errorMessage += singleDebit(insertedUserId, 1,
        UUID.fromString("fb2a7511-99e4-4418-8e18-18deb93f5e72"), false, 2, "start", "1");
    if (errorMessage == null || errorMessage.equals("null")) {
      errorMessage = null;
    }
    errorMessage += insertNewCustomerToLevel(insertedUserId, level, false, null);
    if (errorMessage == null || errorMessage.equals("null")
        || errorMessage.equals("nullnullnull")) {
      errorMessage = null;
    }

    return errorMessage;
  }

  private Double getCurrencyCeilForResult(double startDouble) {
    return Math.ceil(startDouble * 100) / 100;
  }

  private String getPaySystemById(String systemId) {
    switch (systemId) {
      case "b0caea61-600a-4b24-8f9f-5f6e70dd8a8f":
        return "Perfect";
      case "3ca41c65-0d98-415b-9cfa-cdbc86d223bb":
        return "Payeer";
      default:
        return "Unknown";
    }
  }

  // matrix
  public String insertNewCustomerToLevel(UUID insertedUserId, String level, boolean isTech,
      UUID fromMatrix) {
    // if (isTech && !level.equals("0")) {
    // return "Tying insert tech to not first level.";
    // }
    //
    // int levelInt = Integer.parseInt(level);
    // long timeStamp = System.currentTimeMillis();
    // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // String dateString = format.format(new Date(timeStamp));
    //
    // // system permissions
    // RecordsOwnerModelAbstract realOwnerContact = dynamicClassPutData.getRecordsOwner(
    // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // insertedUserId, sudoPermissions.getCustomerDynamicRole()));
    //
    // Map<String, Object> contactConditions = new HashMap<>(2);
    // contactConditions.put("d9cc94f7-d719-4353-957f-0a513d58f957",
    // realOwnerContact.getId().toString());
    //
    // DynamicClassRecordLineAbstract forUserCustomRecord =
    // dynamicClassPutData.getRealUserIdByLogin(SUDOPermissions.USER_VALUES_CLASS,
    // sudoPermissions.getLiveLineContactMapStructure(), contactConditions);
    // log.info("found contact by owner: {}", forUserCustomRecord.getFieldValues());
    // if (forUserCustomRecord == null) {
    // return "Not found user.";
    // }
    //
    // String customerLogin =
    // (String) forUserCustomRecord.getFieldValues().get("81613045-4bb7-4576-9752-12dc08689b7d");
    // log.info("found login by owner: {}", customerLogin);
    // // // record value receiver
    // // DynamicClassRecordLineAbstract oldRecordValueMatrix =
    // //
    // dynamicClassPutData.getDynamicClassesRecordByOverOwnerId(SUDOPermissions.USER_MATRIX_CLASS,
    // // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // // insertedUserId, sudoPermissions.getCustomerDynamicRole()),
    // // sudoPermissions.getLiveLineMatrixMapStructure());
    // Map<String, Object> conditions = new HashMap<>(2);
    //
    // if (!isTech) {
    // // check this level
    // // conditions.put("ce98a775-5dfe-437f-bad6-65f4793d8567", "true");
    // // conditions.put("9bcd4349-8427-4597-8522-68c928893374", insertedUserId.toString());
    // conditions.put("00e64bfb-c0b4-4782-92cf-6de68ba94157",
    // forUserCustomRecord.getOwnerRecordId().toString());
    // // check level
    // conditions.put("9bb33f86-b83b-4419-b532-c910600afc47", level);
    //
    // DynamicClassRecordLineAbstract matrixRecord =
    // dynamicClassPutData.getRealUserIdByLogin(SUDOPermissions.USER_MATRIX_CLASS,
    // sudoPermissions.getLiveLineMatrixMapStructure(), conditions);
    // if (matrixRecord != null) {
    // return "user is present yet!";
    // }
    // conditions.clear();
    // }
    // conditions.put("9bb33f86-b83b-4419-b532-c910600afc47", level);
    // conditions.put("ce98a775-5dfe-437f-bad6-65f4793d8567", "true");
    //
    // DynamicClassRecordLineAbstract anyMatrixRecord =
    // dynamicClassPutData.getAdminDynamicClassesRecordByOverOwnerId(
    // SUDOPermissions.USER_MATRIX_CLASS, sudoPermissions.getLiveLineMatrixMapStructure(),
    // conditions, UUID.fromString("346ea6af-3b32-42bd-ba4d-eb79b85478c8"), true);
    // String myselfValue = "{\"login\":\"" + customerLogin + "\",\"date\":\"" + dateString
    // + "\",\"tech\":\"" + isTech + "\"}";
    // boolean isFool = false;
    // UUID nextUUID = null;
    // UUID nextParent = null;
    // UUID matrixOwnerId = null;
    // if (anyMatrixRecord != null) {
    //
    // if (!anyMatrixRecord.getFieldValues().get("112bf798-63c9-46af-88c5-29d19abfdff8")
    // .equals("{}")) {
    // isFool = true;
    // nextUUID = UUID.fromString(
    // (String) anyMatrixRecord.getFieldValues().get("9bcd4349-8427-4597-8522-68c928893374"));
    // nextParent =
    // (UUID) anyMatrixRecord.getFieldValues().get("1f40f431-e731-4ccc-8b77-ed0a6ec2d302");
    // }
    // String parentMatrixId =
    // (String) anyMatrixRecord.getFieldValues().get("c9c8beb5-99ad-4816-a78b-72376d0d9090");
    // if (parentMatrixId == null || parentMatrixId.equals("") || parentMatrixId.equals("null")) {
    // parentMatrixId = null;
    // }
    // matrixOwnerId =
    // (UUID) anyMatrixRecord.getFieldValues().get("00e64bfb-c0b4-4782-92cf-6de68ba94157");
    // Map<String, Object> updateValues = createNewMatrixRecord(UUID.fromString(
    // (String) anyMatrixRecord.getFieldValues().get("9bcd4349-8427-4597-8522-68c928893374")),
    // matrixOwnerId, levelInt,
    // (String) anyMatrixRecord.getFieldValues().get("2a2aaf1b-52f1-4612-a18e-2ebc6964df46"),
    // isFool
    // ? (String) anyMatrixRecord.getFieldValues()
    // .get("112bf798-63c9-46af-88c5-29d19abfdff8")
    // : myselfValue,
    // isFool ? myselfValue : "{}",
    // parentMatrixId == null ? null : UUID.fromString(parentMatrixId), !isFool);
    // Map<String, Object> fieldUpdateValues = new HashMap() {
    // {
    // put("fields", updateValues);
    // }
    // };
    // dynamicClassPutData.updateDbStaffLog(SUDOPermissions.USER_MATRIX_CLASS,
    // anyMatrixRecord.getRecordId(), timeStamp, sudoPermissions.getActualSudoPermissions(),
    // fieldUpdateValues, sudoPermissions.getLiveLineMatrixMapStructure());
    // dynamicClassPutData.updateRecord(SUDOPermissions.USER_MATRIX_CLASS,
    // anyMatrixRecord.getRecordId(), sudoPermissions.getLiveLineMatrixMapStructure(),
    // updateValues, sudoPermissions.getActualSudoPermissions(), timeStamp);
    // }
    // if (!isTech) {
    // Map<String, Object> insertValues = createNewMatrixRecord(insertedUserId,
    // realOwnerContact.getId(), levelInt, myselfValue, "{}", "{}", fromMatrix, true);
    // Map<String, Object> fieldInsertValues = new HashMap() {
    // {
    // put("fields", insertValues);
    // }
    // };
    //
    // UUID newMatrixRecordId = UUID.randomUUID();
    // dynamicClassPutData.insertNewRecord(SUDOPermissions.USER_MATRIX_CLASS, newMatrixRecordId,
    // sudoPermissions.getLiveLineMatrixMapStructure(), insertValues,
    // sudoPermissions.getActualSudoPermissions(), timeStamp);
    // dynamicClassPutData.updateDbStaffLog(SUDOPermissions.USER_MATRIX_CLASS, newMatrixRecordId,
    // timeStamp, sudoPermissions.getActualSudoPermissions(), fieldInsertValues,
    // sudoPermissions.getLiveLineMatrixMapStructure());
    // }
    // if (isFool) {
    // conditions.clear();
    // conditions.put("d9cc94f7-d719-4353-957f-0a513d58f957",
    // anyMatrixRecord.getFieldValues().get("00e64bfb-c0b4-4782-92cf-6de68ba94157"));
    // DynamicClassRecordLineAbstract ownerFoolMatrixRecord =
    // dynamicClassPutData.getRealUserIdByLogin(SUDOPermissions.USER_VALUES_CLASS,
    // sudoPermissions.getLiveLineContactMapStructure(), conditions);
    // if (LiveLineLevelsConstants.PAYOUT_REFERRALS.get(levelInt) > 0) {
    // // pay for referral
    // String sponsorLogin = null;
    // try {
    // sponsorLogin = (String) ownerFoolMatrixRecord.getFieldValues()
    // .get("ad3bfe4c-1ee0-478c-9689-4a95dd23db8f");
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    // Map<String, Object> fillUserConditions = new HashMap<>(1);
    // fillUserConditions.put("060f16c7-7573-413f-8f38-fe8d4bf177aa", sponsorLogin.toLowerCase());
    //
    // DynamicClassRecordLineAbstract sponsorAuths =
    // dynamicClassPutData.getRealUserIdByLogin(
    // SUDOPermissions.CONTACT_VALUES_CLASS, sudoPermissions
    // .getSUDOFastStructureMapForObject(SUDOPermissions.CONTACT_VALUES_CLASS),
    // fillUserConditions);
    // if (sponsorAuths != null)
    // singleCredit(sponsorAuths.getRecordId(),
    // LiveLineLevelsConstants.PAYOUT_REFERRALS.get(levelInt),
    // UUID.fromString("fb2a7511-99e4-4418-8e18-18deb93f5e72"), false, 2, "referral",
    // (String) ownerFoolMatrixRecord.getFieldValues()
    // .get("81613045-4bb7-4576-9752-12dc08689b7d"),
    // Integer.toString(levelInt + 1));
    // }
    // if (LiveLineLevelsConstants.PAYOUTS.get(levelInt) > 0) {
    // // pay for exit
    // Map<String, Object> fillUserConditions = new HashMap<>(1);
    // fillUserConditions.put("060f16c7-7573-413f-8f38-fe8d4bf177aa",
    // ((String) ownerFoolMatrixRecord.getFieldValues()
    // .get("81613045-4bb7-4576-9752-12dc08689b7d")).toLowerCase());
    //
    // DynamicClassRecordLineAbstract ownerAuths =
    // dynamicClassPutData.getRealUserIdByLogin(
    // SUDOPermissions.CONTACT_VALUES_CLASS, sudoPermissions
    // .getSUDOFastStructureMapForObject(SUDOPermissions.CONTACT_VALUES_CLASS),
    // fillUserConditions);
    // if (ownerAuths != null)
    // singleCredit(ownerAuths.getRecordId(), LiveLineLevelsConstants.PAYOUTS.get(levelInt),
    // UUID.fromString("fb2a7511-99e4-4418-8e18-18deb93f5e72"), false, 2, "salary",
    // Integer.toString(levelInt + 1));
    // }
    //
    // insertNewCustomerToLevel(nextUUID, Integer.toString(levelInt + 1), false, nextParent);
    // log.info("result single debit: {}",
    // singleDebit(nextUUID, 0, UUID.fromString("fb2a7511-99e4-4418-8e18-18deb93f5e72"), false,
    // 2, "buy", Integer.toString(levelInt + 2)));
    // for (int i = 0; i < LiveLineLevelsConstants.TECHS_COUNT.get(levelInt); i++) {
    // singleDebit(nextUUID, 0, UUID.fromString("fb2a7511-99e4-4418-8e18-18deb93f5e72"), false, 2,
    // "tech", "1");
    // insertNewCustomerToLevel(nextUUID, "0", true, nextUUID);
    // }
    // } else if (matrixOwnerId != null)
    //
    // {
    // if (levelInt == 0) {
    // // pay for referral
    // RecordsOwnerModelAbstract recordsOwner =
    // recordsOwnerRepositoryImplemented.getRecordsOwnerById(matrixOwnerId);
    // log.info("referral single credit result: {}",
    // singleCredit(recordsOwner.getContactId(), 10.0,
    // UUID.fromString("fb2a7511-99e4-4418-8e18-18deb93f5e72"), false, 2, "referral",
    // customerLogin, "1"));
    // } else {
    // log.info("levelInt is not 0 : {}", levelInt);
    // }
    // } else {
    // log.info("matrixOwnerId is null!");
    // }

    return null;
  }

  public String setCommonDataValues(UUID byLiveLineUserId, UUID currentclassId,
      Map<String, Object> extUpdateValues, boolean isAdmin) {

    // long timeStamp = System.currentTimeMillis();
    // Map<String, Object> updateValues = extUpdateValues.entrySet().stream().collect(Collectors
    // .toMap(key -> key.getKey(), value -> SQLProtection.protectRequestObject(value.getValue())));
    // log.info("try update values: {}", updateValues);
    // // system permissions
    // RecordsOwnerModelAbstract realOwnerContact = dynamicClassPutData.getRecordsOwner(
    // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // byLiveLineUserId, sudoPermissions.getCustomerDynamicRole()));
    // Map<String, Object> currentMapStructure =
    // sudoPermissions.getSUDOFastStructureMapForObject(currentclassId);
    // // record value receiver
    // Map<String, Object> conditions = new HashMap<>(2);
    // if (updateValues.get("id") == null) {
    // return "not found record. exit.";
    // }
    // conditions.put((String) ((Map<String, Object>) currentMapStructure.get("0")).get("id"),
    // updateValues.get("id"));
    // DynamicClassRecordLineAbstract oldRecordValueContact =
    // dynamicClassPutData.getAdminDynamicClassesRecordByOverOwnerId(currentclassId,
    // currentMapStructure, conditions, UUID.fromString(
    // (String) ((Map<String, Object>) currentMapStructure.get("3")).get("id")),
    // true);
    //
    //
    // if (oldRecordValueContact == null)
    // return "Not found record. exit.";
    // if (!isAdmin && !oldRecordValueContact.getOwnerRecordId().equals(realOwnerContact.getId())) {
    // return "this record is not yours. exit.";
    // }
    // Map<String, Object> recordValues = (Map<String, Object>) updateValues.get("fields");
    // if (!checkConditions(recordValues, oldRecordValueContact.getFieldValues())) {
    // return "not found fin pass. exit.";
    // }
    // Map<String, Object> realMapStructure = null;
    // if (!isAdmin)
    // realMapStructure = sudoPermissions.getRealFastStructureMapForObject(currentclassId,
    // UUID.fromString((String) sudoPermissions.getActualSudoPermissions()
    // .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.DYNAMIC_ROLE_ID))));
    //
    //
    // recordValues.put((String) ((Map<String, Object>) currentMapStructure.get("1")).get("id"),
    // realOwnerContact.getId().toString());
    // Map<String, Object> fieldValues = new HashMap<>(1);
    // fieldValues.put("fields", recordValues);
    // dynamicClassPutData.updateDbStaffLog(SUDOPermissions.USER_VALUES_CLASS,
    // oldRecordValueContact.getRecordId(), timeStamp, sudoPermissions.getActualSudoPermissions(),
    // fieldValues, isAdmin ? currentMapStructure : realMapStructure);
    // dynamicClassPutData.updateRecord(SUDOPermissions.USER_VALUES_CLASS,
    // oldRecordValueContact.getRecordId(), isAdmin ? currentMapStructure : realMapStructure,
    // recordValues, sudoPermissions.getActualSudoPermissions(), timeStamp);
    return null;
  }

  private boolean checkConditions(Map<String, Object> changedValues,
      Map<String, Object> recordValues) {
    if ((changedValues.containsKey("amount") && changedValues.containsKey("wallet"))
        || changedValues.containsKey("3ca41c65-0d98-415b-9cfa-cdbc86d223bb")
        || changedValues.containsKey("b0caea61-600a-4b24-8f9f-5f6e70dd8a8f")) {
      if (!changedValues.containsKey("1be6e27e-a98d-41d8-8717-95b4396bef43")
          || !changedValues.get("1be6e27e-a98d-41d8-8717-95b4396bef43")
              .equals(recordValues.get("1be6e27e-a98d-41d8-8717-95b4396bef43"))) {
        return false;
      } else
        return true;
    }
    return true;
  }

  public String updatePassword(Map<String, Object> userValues) {
    // Map<String, Object> checkedParams = new HashMap<>(3);
    // checkedParams = userValues.entrySet().parallelStream()
    // .collect(Collectors.toMap(x -> x.getKey(), x -> updateErrorSymbols((String) x.getValue())));
    // if (checkedParams.get("oldPass").equals("") || checkedParams.get("newPass").equals("")) {
    // return "Values is empty. exit.";
    // }
    // if (!checkedParams.get("newPass").equals(checkedParams.get("repeatNewPass"))
    // || checkedParams.get("newPass").equals("")) {
    // return "Passwords is not equals. exit.";
    // }
    //
    // UUID currentClass = SUDOPermissions.CONTACT_VALUES_CLASS;
    // long timeStamp = System.currentTimeMillis();
    //
    // List<Map<String, Object>> dynamicFilters = null;
    // try {
    // dynamicFilters = objectMapper.readValue(
    // "[{\"null.null." + currentClass
    // + ".060f16c7-7573-413f-8f38-fe8d4bf177aa\" : {\"value\": \""
    // + ((UserDetails) SecurityContextHolder.getContext().getAuthentication()
    // .getPrincipal()).getUsername()
    // + "\", \"operator\":\"0\"}}]",
    // new TypeReference<List<Map<String, Object>>>() {});
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // }
    // Map<String, Object> mapStructure =
    // sudoPermissions.getSUDOFastStructureMapForObject(currentClass);
    // String result = dynamicClassesQueryFactory.getLineSubquery(currentClass,
    // sudoPermissions.getActualSudoPermissions(), mapStructure, null, dynamicFilters,
    // recordsOwnerClassSettingsRepositoryImplemented
    // .getRecordsOwnerClassSettingsByclassId(currentClass),
    // null);
    // List<String> contactQueryResult =
    // dynamicClassesRepositoryImplemented.getDynamicSpaceShapeClassBySubquery(result);
    // if (contactQueryResult == null || contactQueryResult.isEmpty()) {
    // return "Not found contact values. exit.";
    // }
    // Map<String, Object> oldRecordMap;
    // try {
    // oldRecordMap = objectMapper.readValue(contactQueryResult.get(0),
    // new TypeReference<Map<String, Object>>() {});
    // } catch (
    //
    // IOException e) {
    // e.printStackTrace();
    // return "Error in old value mapping. exit.";
    // }
    // // passwordEncoder
    // String oldPassValue = (String) getValueByFieldId(mapStructure, oldRecordMap,
    // "28765a7e-fd96-47eb-851f-19f54f149789");
    // if (!passwordEncoder.matches((CharSequence) checkedParams.get("oldPass"), oldPassValue)) {
    // return "Password in db is not equals. exit.";
    // }
    // Map<String, Object> values = new HashMap<>(8);
    // values.put("80ca0790-c30b-41ba-b74d-868943a3b9cd", (String) oldRecordMap.get("1"));
    // values.put("28765a7e-fd96-47eb-851f-19f54f149789",
    // passwordEncoder.encode((CharSequence) checkedParams.get("newPass")));
    // dynamicClassPutData.updateRecord(SUDOPermissions.CONTACT_VALUES_CLASS,
    // UUID.fromString((String) oldRecordMap.get("0")), mapStructure, values,
    // sudoPermissions.getActualSudoPermissions(), timeStamp);
    // return null;
    // }
    //
    // public String newFinPassword() {
    //
    // if (!sudoPermissions.instance()) {
    // return sudoPermissions.getIsAllRight();
    // }
    //
    // UUID currentClass = SUDOPermissions.USER_VALUES_CLASS;
    // long timeStamp = System.currentTimeMillis();
    // String login =
    // ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
    // .getUsername();
    // List<Map<String, Object>> dynamicFilters = null;
    // try {
    // dynamicFilters = objectMapper.readValue("[{\"null.null." + currentClass
    // + ".81613045-4bb7-4576-9752-12dc08689b7d\" : {\"value\": \"" + login
    // + "\", \"operator\":\"0\"}}]", new TypeReference<List<Map<String, Object>>>() {});
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // }
    // Map<String, Object> mapStructure =
    // sudoPermissions.getSUDOFastStructureMapForObject(currentClass);
    // String result = dynamicClassesQueryFactory.getLineSubquery(currentClass,
    // sudoPermissions.getActualSudoPermissions(), mapStructure, null, dynamicFilters,
    // recordsOwnerClassSettingsRepositoryImplemented
    // .getRecordsOwnerClassSettingsByclassId(currentClass),
    // null);
    // List<String> contactQueryResult =
    // dynamicClassesRepositoryImplemented.getDynamicSpaceShapeClassBySubquery(result);
    // if (contactQueryResult == null || contactQueryResult.isEmpty()) {
    // return "Not found contact values. exit.";
    // }
    // Map<String, Object> oldRecordMap;
    // try {
    // oldRecordMap = objectMapper.readValue(contactQueryResult.get(0),
    // new TypeReference<Map<String, Object>>() {});
    // } catch (
    //
    // IOException e) {
    // e.printStackTrace();
    // return "Error in old value mapping. exit.";
    // }
    // // passwordEncoder
    // String mail = (String) getValueByFieldId(mapStructure, oldRecordMap,
    // "9941fd7b-8272-4913-9731-8d5f90368791");
    // SimpleMailMessage message = new SimpleMailMessage();
    // if (mail == null || !mail.contains("@")) {
    // return "This is not email!";
    // }
    // message.setFrom(adminMail);
    // message.setTo(mail);
    // message.setSubject("password recovery.");
    // String generatedString = generateCommonLangPassword();
    // Map<String, Object> values = new HashMap<>(8);
    // values.put("d9cc94f7-d719-4353-957f-0a513d58f957", (String) oldRecordMap.get("1"));
    // values.put("1be6e27e-a98d-41d8-8717-95b4396bef43", (generatedString));
    // dynamicClassPutData.updateRecord(currentClass, UUID.fromString((String)
    // oldRecordMap.get("0")),
    // mapStructure, values, sudoPermissions.getActualSudoPermissions(), timeStamp);
    // message
    // .setText("Ваш новый финансовый пароль для пользователя " + login + ": " + generatedString);
    // emailSender.send(message);
    return null;
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

  public String generateCommonLangPassword() {
    String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
    String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
    String numbers = RandomStringUtils.randomNumeric(2);
    // String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
    // String totalChars = RandomStringUtils.randomAlphanumeric(2);
    String combinedChars = upperCaseLetters.concat(lowerCaseLetters).concat(numbers)
    // .concat(specialChar).concat(totalChars)
    ;
    List<Character> pwdChars =
        combinedChars.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
    Collections.shuffle(pwdChars);
    String password = pwdChars.stream()
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    return password;
  }

  public String setNextUserMigration() {
    String sql =
        "select (SELECT \"d2a47321-e0da-4ee5-bc76-110a4e67090c\" from \"cc_7052a1e5-8d00-43fd-8f57-f2e4de0c8b24_data_use\" where \"80ca0790-c30b-41ba-b74d-868943a3b9cd\" = \"d9cc94f7-d719-4353-957f-0a513d58f957\") as \"d2a47321-e0da-4ee5-bc76-110a4e67090c\", \"cc_7a38bfb3-7874-4eb4-b981-b38e5ade2df8_data_use\".* from \"cc_455681f9-13be-4168-a5a0-e6575133b7aa_data_use\" join \"cc_7a38bfb3-7874-4eb4-b981-b38e5ade2df8_data_use\" on \"cc_455681f9-13be-4168-a5a0-e6575133b7aa_data_use\".\"5f15318c-abfb-40ac-b5a9-63a2b684237e\" like \"cc_7a38bfb3-7874-4eb4-b981-b38e5ade2df8_data_use\".\"3b60aef1-acbf-49e3-a34f-4b19d4427626\"::character varying where \"1ff929b6-6ce2-45a7-8dab-d53249082eac\" - \"df3cd742-f9db-4280-ae51-f3fd1304f415\" >= 1 and \"c59522be-f5aa-43ad-96d7-cb316561f189\" < 2 and \"fb2a7511-99e4-4418-8e18-18deb93f5e72\" >= 11 and (SELECT \"00e64bfb-c0b4-4782-92cf-6de68ba94157\" FROM \"cc_93b0df3a-4a69-4278-abc6-153351b68d6c_data_use\" where \"00e64bfb-c0b4-4782-92cf-6de68ba94157\" = \"d9cc94f7-d719-4353-957f-0a513d58f957\" limit 1) is null order by \"4f81183f-087e-43ac-b5ea-96931bc89ae0\" limit 1";
    log.info("sql: {}", sql);
    System.out.println("setNextUserMigration sql: " + sql);
    Map<String, Object> fastQuery = dynamicClassesRepositoryImplemented.getDynamicGarbadge(sql);
    if (fastQuery != null && !fastQuery.isEmpty())
      return updateMatrix((UUID) fastQuery.get("d2a47321-e0da-4ee5-bc76-110a4e67090c"), "0", null);
    else
      return "not found any matrix!";
  }

  public String paymentCredit(String name, UUID byLiveLineUserId, double additionalValue,
      UUID forField, boolean isSubZero, int type, String timeStampStr, String... creditArgs) {

    // if (!isSubZero && additionalValue <= 0) {
    // return "Result less then zero. exit.";
    // }
    //
    // long timeStamp = Long.parseLong(timeStampStr);
    // if (timeStamp < 1000000000000l) {
    // timeStamp = timeStamp * 1000;
    // }
    //
    // // system permissions
    // RecordsOwnerModelAbstract realOwnerContact = dynamicClassPutData.getRecordsOwner(
    // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // byLiveLineUserId, sudoPermissions.getCustomerDynamicRole()));
    //
    // // record value receiver
    // DynamicClassRecordLineAbstract oldRecordValueContact =
    // dynamicClassPutData.getDynamicClassesRecordByOverOwnerId(SUDOPermissions.USER_VALUES_CLASS,
    // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // byLiveLineUserId, sudoPermissions.getCustomerDynamicRole()),
    // sudoPermissions.getLiveLineContactMapStructure());
    // if (oldRecordValueContact == null)
    // return "Not found user receiver. exit.";
    // double oldValueBuyer;
    // try {
    // oldValueBuyer = (double) oldRecordValueContact.getFieldValues().get(forField.toString());
    // } catch (Exception ex) {
    // oldValueBuyer = 0;
    // }
    // Map<String, Object> conditions = new HashMap<>(2);
    // conditions.put("1fbefc3d-5103-4656-bdf0-776fa06e9b90", timeStamp);
    // conditions.put("68abc071-958c-4d9e-b54f-0e608bf05cb3",
    // oldRecordValueContact.getOwnerRecordId());
    //
    // DynamicClassRecordLineAbstract anyMatrixRecord = dynamicClassPutData
    // .getAdminDynamicClassesRecordByOverOwnerId(SUDOPermissions.USER_STATISTIC_CLASS,
    // sudoPermissions.getLiveLineStatisticMapStructure(), conditions,
    // UUID.fromString("1fbefc3d-5103-4656-bdf0-776fa06e9b90"), true);
    // if (anyMatrixRecord != null) {
    // return "Record is present yet!";
    // }
    // if (!isSubZero && (additionalValue <= 0 || oldValueBuyer + additionalValue < 0))
    // return "Result less then zero. exit.";
    // String descriptionValue = "";
    // switch (creditArgs[0]) {
    // case "payment":
    // descriptionValue = getDescriptionFormatString(creditArgs[0],
    // Double.toString(additionalValue), creditArgs[1], getPaySystemById(creditArgs[2]));
    // break;
    // case "salary":
    // descriptionValue = getDescriptionFormatString(creditArgs[0],
    // Double.toString(additionalValue), creditArgs[1]);
    // break;
    // case "referral":
    // descriptionValue = getDescriptionFormatString(creditArgs[0],
    // Double.toString(additionalValue), creditArgs[1], creditArgs[2]);
    // break;
    // }
    // Map<String, Object> values = new HashMap<>(2);
    // Map<String, Object> fieldValues = new HashMap<>(1);
    // values.put(forField.toString(),
    // Double.toString(getCurrencyCeilForResult(oldValueBuyer + additionalValue)));
    // values.put(
    // (String) ((Map<String, Object>) sudoPermissions.getLiveLineContactMapStructure().get("1"))
    // .get("id"),
    // realOwnerContact.getId().toString());
    // fieldValues.put("fields", values);
    // dynamicClassPutData.insertNewRecord(SUDOPermissions.USER_STATISTIC_CLASS, UUID.randomUUID(),
    // sudoPermissions.getLiveLineStatisticMapStructure(),
    // createNewStatisticsRecord(oldRecordValueContact.getRecordId(),
    // oldRecordValueContact.getRecordId(), realOwnerContact.getId(), oldValueBuyer,
    // getCurrencyCeilForResult(oldValueBuyer + additionalValue), descriptionValue, forField,
    // type),
    // sudoPermissions.getActualSudoPermissions(), timeStamp);
    // dynamicClassPutData.updateDbStaffLog(SUDOPermissions.USER_VALUES_CLASS,
    // oldRecordValueContact.getRecordId(), timeStamp, sudoPermissions.getActualSudoPermissions(),
    // fieldValues, sudoPermissions.getLiveLineContactMapStructure());
    // dynamicClassPutData.updateRecord(SUDOPermissions.USER_VALUES_CLASS,
    // oldRecordValueContact.getRecordId(), sudoPermissions.getLiveLineContactMapStructure(),
    // values, sudoPermissions.getActualSudoPermissions(), timeStamp);
    return null;
  }

  public String payOutQuery(UUID byLiveLineUserId, UUID currentclassId,
      Map<String, Object> extUpdateValues, boolean isAdmin) {
    return null;
    // long timeStamp = System.currentTimeMillis();
    // Map<String, Object> updateValues = extUpdateValues.entrySet().stream().collect(Collectors
    // .toMap(key -> key.getKey(), value -> SQLProtection.protectRequestObject(value.getValue())));
    // log.info("try update values: {}", updateValues);
    // // system permissions
    // RecordsOwnerModelAbstract realOwnerContact = dynamicClassPutData.getRecordsOwner(
    // sudoPermissions.updatePermissionsToCustomer(sudoPermissions.getActualSudoPermissions(),
    // byLiveLineUserId, sudoPermissions.getCustomerDynamicRole()));
    // Map<String, Object> currentMapStructure =
    // sudoPermissions.getSUDOFastStructureMapForObject(currentclassId);
    // // record value receiver
    // Map<String, Object> conditions = new HashMap<>(2);
    // if (updateValues.get("id") == null) {
    // return "not found record. exit.";
    // }
    // conditions.put((String) ((Map<String, Object>) currentMapStructure.get("0")).get("id"),
    // updateValues.get("id"));
    // DynamicClassRecordLineAbstract oldRecordValueContact =
    // dynamicClassPutData.getAdminDynamicClassesRecordByOverOwnerId(currentclassId,
    // currentMapStructure, conditions, UUID.fromString(
    // (String) ((Map<String, Object>) currentMapStructure.get("3")).get("id")),
    // true);
    //
    //
    // if (oldRecordValueContact == null)
    // return "Not found record. exit.";
    // if (!isAdmin && !oldRecordValueContact.getOwnerRecordId().equals(realOwnerContact.getId())) {
    // return "this record is not yours. exit.";
    // }
    // Map<String, Object> recordValues = (Map<String, Object>) updateValues.get("fields");
    // if (!checkConditions(recordValues, oldRecordValueContact.getFieldValues())) {
    // return "not found fin pass. exit.";
    // }
    // double amount = -1;
    // try {
    // if (recordValues.get("amount") instanceof Integer) {
    // amount = (Integer) recordValues.get("amount");
    // } else
    // amount = Double.parseDouble((String) recordValues.get("amount"));
    // } catch (NumberFormatException ex) {
    // return "error with amount";
    // }
    // return singleDebit(byLiveLineUserId, amount,
    // UUID.fromString("fb2a7511-99e4-4418-8e18-18deb93f5e72"), false, 0, "payout",
    // (String) recordValues.get("wallet"));
  }

}
