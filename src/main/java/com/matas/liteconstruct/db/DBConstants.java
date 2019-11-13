package com.matas.liteconstruct.db;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.security.abstractmodel.SystemRoles;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;

public interface DBConstants {

  public String SUPER_ADMIN_ID = "1d021b86-41c6-47c1-a38e-0aa89b98dc28";
  public String ADMIN_ID = "65fe5829-ff9a-4b58-aa76-d8a92eaeee7e";

  public String TBL_STRUCTURE_FIELDS = "class_structure_fields";
  public String TBL_COLLECTIONS_STRUCTURE = "class_collections_fields_use";
  public String TBL_REGISTRY_CLASSES = "registry_classes";
  public String TBL_REGISTRY_DYNAMIC_ROLE = "registry_dynamic_role";
  public String TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION = "registry_dynamic_role_collection";
  public String TBL_CLASS_STRUCTURE_USE_FAST = "class_structure_cache";
  public String TBL_MANAGEMENT_RULES = "class_management_rules";
  public String TBL_MANAGEMENT_SETTINGS = "class_management_settings";
  
  public String TBL_TOKEN_IMPLEMENTED_RECORDS = "cc_%1$s_implemented_records";
  public String TBL_BELONG_RECORDS = "class_belong_records";
  
  public String TBL_LITERAL = "class_literal";

  public String TBL_SITE_AUTHORIZED_CONTACTS = "site_authorized_contacts";

  public String TBL_OWN_RECORD_DYNAMIC = "class_own_dynamic";
  public String TBL_RECORD_OWNER = "class_record_owner";

  public String TBL_FILTER_RECORD = "class_filter_record";
  public String TBL_FILTER_GROUP = "class_filter_group";
  public String TBL_STAFF_DESCRIPTIONS = "class_staff_descriptions";
  public String TBL_CLASS_LOG = "class_staff_log";
  public String TBL_REGISTRY_COMPANIES_LINKS = "registry_companies_links";
  // access tables
  public String TBL_ACCESS_RULE = "class_access_rule";
  public String TBL_ACCESS_LITERAL = "class_access_literal";
  public String TBL_ACCESS_FILTER_RECORD = "class_access_filter_record";
  public String TBL_ACCESS_FILTER_GROUP = "class_access_filter_group";
  // site tables
  public String TBL_SITE_DOMAINS = "site_domains";
  // relations table
  public String TBL_CLASS_COLLECTION_REQUEST_RELATIONS = "class_collection_request_relations";
  public String TBL_COMPANY_CONTACT_CLASS_RELATION = "registry_company_contact_class";
  public String TBL_SIGNUP_FIELDS = "registry_signup_fields";
  public String TBL_DICTIONARY_SYSTEM = "registry_dictionary_system";
  public String TBL_COLLECTION_CASE = "registry_collection_case";

  // blocks names
  public String SETTINGS = "set";
  public String INIT = "init";
  public String WORK = "work";
  public String STRUCTURE = "str";
  public String DATA = "dat";

  // const classes for permissions:
  public String ROLE_ID = "97086af0-956b-4380-a385-ea823cff377a";
  public String COMPANY_ID = "2ed029b6-d745-4f85-8d9f-2dccd2a7da37";
  public String SERVICE_ID = "3db2f640-e01a-42ac-904e-87a46e0373fd";
  public String DYNAMIC_ROLE_ID = "4c20a934-5837-4fa1-a030-254eb2bd3a6d";
  public String DOMAIN_ID = "390d879f-d575-4424-bb7a-e69e0eaf76cf";

  // init params
  public String CONTACT_ID = "7052a1e5-8d00-43fd-8f57-f2e4de0c8b24";

  // work params
  public String WORK_CLASS = "wClass";

  public String SYSTEM_UUID = "3a1ee914-ec32-4e83-8f04-df0897daf8e9";

  // auth params
  public String TBL_AUTH_CONTACT = "cc_" + CONTACT_ID + "_data_use";
  public String FLD_AUTH_CONTACT_ID = "d2a47321-e0da-4ee5-bc76-110a4e67090c";
  public String FLD_AUTH_CONTACT_OWNER = "80ca0790-c30b-41ba-b74d-868943a3b9cd";
  public String FLD_AUTH_CONTACT_LOGIN = "060f16c7-7573-413f-8f38-fe8d4bf177aa";
  public String FLD_AUTH_CONTACT_PASSWORD = "28765a7e-fd96-47eb-851f-19f54f149789";

  public String TBL_AUTH_COMPANY = "cc_" + COMPANY_ID + "_data_use";
  public String FLD_AUTH_COMPANY_ID = "d700550b-7890-4836-91ae-b52e8a4cde6d";

  public String TBL_AUTH_SERVICE = "cc_" + SERVICE_ID + "_data_use";
  public String FLD_AUTH_SERVICE_ID = "b5afac44-2df9-42b5-88c3-694e63d3dd0a";

  public String TBL_AUTH_ROLE = "cc_" + ROLE_ID + "_data_use";
  public String FLD_AUTH_ROLE_ID = "ea4d5b30-1c60-4bce-a2cd-452e9b075434";

  // registry tables
  public String REGISTRY_DYNAMIC_ROLE_CONTACT = "registry_dynamic_role_contact";
  public String REGISTRY_DYNAMIC_ROLE_COLLECTION = "registry_dynamic_role_collection";
  public String REGISTRY_CLASSES = "registry_classes";

  // class static tables
  public String TBL_RECAPTCHA_BY_DOMAIN = "recaptcha_by_domain";
  
  //lang tables
  public String LNG_COMPANY_RELATIONS = "lng_company_relations";
  public String LNG_RECORD_ACCESS = "lng_record_access";
  public String LNG_RECORD_RELATIONS = "lng_record_relations";
  public String LNG_REGISTRY = "lng_registry";

  public static List<UUID> adminsPryorities = new LinkedList<UUID>() {
    {
      add(UUID.fromString(SystemRoles.SUPERADMIN_ROLE.getId()));
      add(UUID.fromString(SystemRoles.GLOBAL_SUPERVISOR_ROLE.getId()));
      add(UUID.fromString(SystemRoles.LOCAL_ADMIN_ROLE.getId()));
      add(UUID.fromString(SystemRoles.LOCAL_SUPERVISOR_ROLE.getId()));
    }
  };


  public static boolean isPresentNeccessaryDataInSettingsMap(Map<String, Object> settings) {
    return !(settings == null
        || !settings.containsKey(FactoryGroupAbstract.getFirstPartOfUUID(ROLE_ID))
        || !settings.containsKey(FactoryGroupAbstract.getFirstPartOfUUID(COMPANY_ID))
        || !settings.containsKey(FactoryGroupAbstract.getFirstPartOfUUID(SERVICE_ID))
        || !settings.containsKey(FactoryGroupAbstract.getFirstPartOfUUID(DYNAMIC_ROLE_ID)));
  }

  /**
   * return default role. if searchedRole == null then return role for admin site. else search necessary role from list.
   * @param presentObjects
   * @param searchedRole
   * @return 
   */
  public static DynamicRoleModelAbstract getDefaultRoleForList(
      List<DynamicRoleModelAbstract> presentObjects, UUID searchedRole) {
    if (searchedRole == null) {
      DynamicRoleModelAbstract[] result = {null};
      adminsPryorities.forEach(role -> {
        if (result[0] == null)
          presentObjects.parallelStream().forEach(x -> {
            if (result[0] == null && x.getRoleId().equals(role)) {
              result[0] = x;
            }
          });
      });
      return result[0];
    } else {
      DynamicRoleModelAbstract[] result = {null};
      presentObjects.parallelStream().forEach(x -> {
        if (result[0] == null && x.getRoleId().equals(searchedRole)) {
          result[0] = x;
        }
      });
      return result[0];
    }
  }
}
