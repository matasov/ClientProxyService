package com.matas.liteconstruct.controller.liveline;

import java.util.Map;
import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.dynamicclass.repos.DynamicClassesRepository;
import com.matas.liteconstruct.db.models.serviceauthorized.model.AuthorozedContact.MapKey;
import com.matas.liteconstruct.db.models.serviceauthorized.repos.AuthorizedContactRepository;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import com.matas.liteconstruct.service.SQLProtection;
import com.matas.liteconstruct.service.business.LiveLineBusinessService;
import com.matas.liteconstruct.service.business.LiveLineQueuePoolExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/perfect")
public class PerfectController {
  
  private LiveLineQueuePoolExecutor liveLineQueuePoolExecutor;

  @Autowired
  public void setLiveLineQueuePoolExecutor(LiveLineQueuePoolExecutor liveLineQueuePoolExecutor) {
    this.liveLineQueuePoolExecutor = liveLineQueuePoolExecutor;
  }
  
  @Value("${perfect.password}")
  private String perfectPassword;
  
  private DynamicClassesRepository dynamicClassesRepositoryImplemented;

  @Autowired
  public void setDynamicClassesRepositoryImplemented(
      DynamicClassesRepository dynamicClassesRepositoryImplemented) {
    this.dynamicClassesRepositoryImplemented = dynamicClassesRepositoryImplemented;
  }

  private AuthorizedContactRepository authorizedContactRepository;

  @Autowired
  public void setAuthorozedContactRepository(
      AuthorizedContactRepository authorozedContactRepository) {
    this.authorizedContactRepository = authorozedContactRepository;
  }

  private LiveLineBusinessService liveLineBusinessService;

  @Autowired
  public void setLiveLineBusinessService(LiveLineBusinessService liveLineBusinessService) {
    this.liveLineBusinessService = liveLineBusinessService;
  }

  @RequestMapping(value = "/payment", method = RequestMethod.GET)
  public ResponseEntity<?> request() {
    return new ResponseEntity<>("<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n"
        + "<meta http-equiv=\"refresh\" content=\"0; url=http://liveline.invensio.com/payment?result=Record is present yet!\">\r\n"
        + "</head>\r\n" + "<body>\r\n" + "</body>\r\n" + "</html>", HttpStatus.OK);
  }

  @RequestMapping(value = "/payment", method = RequestMethod.POST)
  public ResponseEntity<?> setPayment(@RequestParam("PAYMENT_ID") String paymentId,
      @RequestParam("PAYEE_ACCOUNT") String payeeAccount,
      @RequestParam("PAYMENT_AMOUNT") Double paymentAmount,
      @RequestParam("PAYMENT_UNITS") String paymentUnits,
      @RequestParam("PAYMENT_BATCH_NUM") String paymentBatchNum,
      @RequestParam("PAYER_ACCOUNT") String payeerAccount,
      @RequestParam("TIMESTAMPGMT") String timeStamp, @RequestParam("V2_HASH") String v2Hash,
      @RequestParam("SUGGESTED_MEMO") String suggestedMemo) {
    // String redirectUrl = "liveline.invensio.com";
    String redirectUrl = "http://live-line.biz";
    String alternatePassPhrase = DigestUtils.md5Hex(perfectPassword).toUpperCase();
    log.info("{} updated value: {}", paymentId, SQLProtection.protectRequestObject(paymentId));
    String errorMessage = null;
    int checkValue = (int) Math.round(paymentAmount);
    String amountForV2Hash =
        checkValue != paymentAmount ? Double.toString(paymentAmount) : Integer.toString(checkValue);
    String value = new StringBuilder().append(paymentId).append(":").append(payeeAccount)
        .append(":").append(amountForV2Hash).append(":").append(paymentUnits).append(":")
        .append(paymentBatchNum).append(":").append(payeerAccount).append(":")
        .append(alternatePassPhrase).append(":").append(timeStamp).toString();

    if (checkHash(value, v2Hash)) {

      Map<String, Object> localAccessPermissions = null;
      try {
        localAccessPermissions = (Map<String, Object>) authorizedContactRepository
            .getAuthorizedContactByName(
                ((String) SQLProtection.protectRequestObject(suggestedMemo)).toLowerCase())
            .getPermissions().get(MapKey.PERMISSIONS);
      } catch (Exception ex) {
        errorMessage = "Not found values for user: " + suggestedMemo;
        return new ResponseEntity<>(
            "<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n"
                + "<meta http-equiv=\"refresh\" content=\"0; url=" + redirectUrl
                + "/payment?result=notfound\">\r\n"

                + "</head>\r\n" + "<body>\r\n" + "</body>\r\n" + "</html>",
            HttpStatus.EXPECTATION_FAILED);
      }
      if (errorMessage == null)
        errorMessage = liveLineBusinessService.paymentCredit(
            ((String) SQLProtection.protectRequestObject(suggestedMemo)).toLowerCase(),
            UUID.fromString((String) localAccessPermissions
                .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID))),
            paymentAmount, UUID.fromString("fb2a7511-99e4-4418-8e18-18deb93f5e72"), false, 0,
            timeStamp, "payment",
            ((String) SQLProtection.protectRequestObject(suggestedMemo)).toLowerCase(),
            "b0caea61-600a-4b24-8f9f-5f6e70dd8a8f");
    } else {
      errorMessage = "Bad v2hash";
    }

    if (errorMessage != null)

      return new ResponseEntity<>(
          "<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n"
              + "<meta http-equiv=\"refresh\" content=\"0; url=" + redirectUrl + "/payment?result="
              + errorMessage + "\">\r\n" + "</head>\r\n" + "<body>\r\n" + "</body>\r\n" + "</html>",
          HttpStatus.EXPECTATION_FAILED);
    else {
      liveLineQueuePoolExecutor.addNewQuery();
      //liveLineBusinessService.setNextUserMigration();
      return new ResponseEntity<>("<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n"
          + "<meta http-equiv=\"refresh\" content=\"0; url=" + redirectUrl
          + "/payment?result=success\">\r\n" + "</head>\r\n" + "<body>\r\n" + "</body>\r\n"
          + "</html>", HttpStatus.OK);
    }
  }

  public boolean checkHash(String toHash, String compaireValue) {
    String md5Hex = DigestUtils.md5Hex(toHash).toUpperCase();

    return md5Hex.equalsIgnoreCase(compaireValue);
  }
}
