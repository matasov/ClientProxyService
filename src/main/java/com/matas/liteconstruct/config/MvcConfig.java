package com.matas.liteconstruct.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.matas.liteconstruct.db.models.accessfiltersgroup.repos.AccessFiltersGroupRepository;
import com.matas.liteconstruct.db.models.accessfiltersgroup.repos.AccessFiltersGroupRepositoryImplemented;
import com.matas.liteconstruct.db.models.accessfiltersrecord.repos.AccessFiltersRecordRepository;
import com.matas.liteconstruct.db.models.accessfiltersrecord.repos.AccessFiltersRecordRepositoryImplemented;
import com.matas.liteconstruct.db.models.accessliteral.repos.AccessLiteralRepository;
import com.matas.liteconstruct.db.models.accessliteral.repos.AccessLiteralRepositoryImplemented;
import com.matas.liteconstruct.db.models.accessrules.repos.AccessRuleRepository;
import com.matas.liteconstruct.db.models.accessrules.repos.AccessRuleRepositoryImplemented;
import com.matas.liteconstruct.db.models.classes.repos.CustomerClassRepository;
import com.matas.liteconstruct.db.models.classes.repos.CustomerClassRepositoryImplemented;
import com.matas.liteconstruct.db.models.classowndynamic.repos.ClassOwnDynamicRepository;
import com.matas.liteconstruct.db.models.classowndynamic.repos.ClassOwnDynamicRepositoryImplemented;
import com.matas.liteconstruct.db.models.collectiondynamicrole.repos.CollectionDynamicRoleRepository;
import com.matas.liteconstruct.db.models.collectiondynamicrole.repos.CollectionDynamicRoleRepositoryImplemented;
import com.matas.liteconstruct.db.models.collections.repos.StructureCollectionsFieldsRepository;
import com.matas.liteconstruct.db.models.collections.repos.StructureCollectionsFieldsRepositoryImplemented;
import com.matas.liteconstruct.db.models.companycontactclass.repos.CompanyContactRelationRepositoryImplemented;
import com.matas.liteconstruct.db.models.companydomain.repos.CompanyDomainRepository;
import com.matas.liteconstruct.db.models.companydomain.repos.CompanyDomainRepositoryImplemented;
import com.matas.liteconstruct.db.models.dynamicclass.repos.DynamicClassesRepository;
import com.matas.liteconstruct.db.models.dynamicclass.repos.DynamicClassesRepositoryImplemented;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepository;
import com.matas.liteconstruct.db.models.dynamicrole.repos.DynamicRoleRepositoryImplemented;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepository;
import com.matas.liteconstruct.db.models.faststructure.repos.FastStructureRepositoryImplemented;
import com.matas.liteconstruct.db.models.innerrecords.repos.InnerRecordsDynamicClassRepository;
import com.matas.liteconstruct.db.models.innerrecords.repos.InnerRecordsDynamicClassRepositoryImplemented;
import com.matas.liteconstruct.db.models.lngs.repos.LngCompanyAccessRepository;
import com.matas.liteconstruct.db.models.lngs.repos.LngCompanyAccessRepositoryImplemented;
import com.matas.liteconstruct.db.models.lngs.repos.LngCompanyRecordRelationsRepository;
import com.matas.liteconstruct.db.models.lngs.repos.LngCompanyRecordRelationsRepositoryImplemented;
import com.matas.liteconstruct.db.models.recordowner.repos.RecordsOwnerClassSettingsRepository;
import com.matas.liteconstruct.db.models.recordowner.repos.RecordsOwnerClassSettingsRepositoryImplemented;
import com.matas.liteconstruct.db.models.recordowner.repos.RecordsOwnerRepository;
import com.matas.liteconstruct.db.models.recordowner.repos.RecordsOwnerRepositoryImplemented;
import com.matas.liteconstruct.db.models.security.repos.ContactAuthRepository;
import com.matas.liteconstruct.db.models.security.repos.ContactAuthRepositoryImplemented;
import com.matas.liteconstruct.db.models.serviceauthorized.repos.AuthorizedContactRepository;
import com.matas.liteconstruct.db.models.serviceauthorized.repos.AuthorizedContactRepositoryImplemented;
import com.matas.liteconstruct.db.models.signupfields.repos.SignupFieldsRepository;
import com.matas.liteconstruct.db.models.signupfields.repos.SignupFieldsRepositoryImplemented;
import com.matas.liteconstruct.db.models.stafflog.repos.ClassLogRepository;
import com.matas.liteconstruct.db.models.stafflog.repos.ClassLogRepositoryImplemented;
import com.matas.liteconstruct.db.models.streamfiltersgroup.repos.FiltersGroupRepository;
import com.matas.liteconstruct.db.models.streamfiltersgroup.repos.FiltersGroupRepositoryImplemented;
import com.matas.liteconstruct.db.models.streamfiltersrecord.repos.FiltersRecordRepository;
import com.matas.liteconstruct.db.models.streamfiltersrecord.repos.FiltersRecordRepositoryImplemented;
import com.matas.liteconstruct.db.models.streamliterals.repos.LiteralRepository;
import com.matas.liteconstruct.db.models.streamliterals.repos.LiteralRepositoryImplemented;
import com.matas.liteconstruct.db.models.structure.repos.StructureFieldsRepository;
import com.matas.liteconstruct.db.models.structure.repos.StructureFieldsRepositoryImplemented;
import com.matas.liteconstruct.db.models.systemdictionary.repos.SystemDictionaryItemRepository;
import com.matas.liteconstruct.db.models.systemdictionary.repos.SystemDictionaryItemRepositoryImplemented;
import com.matas.liteconstruct.db.service.manager.DynamicTablesService;
import com.matas.liteconstruct.db.service.manager.DynamicTablesServiceImplemented;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.db.models.companyrelations.repos.CompanyRelationsRepository;
import com.matas.liteconstruct.db.models.companyrelations.repos.CompanyRelationsRepositoryImplemented;
import com.matas.liteconstruct.db.models.companycontactclass.repos.CompanyContactRelationRepository;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

  @Value("${web.resources.downloaded_image_path}")
  private String uploadPath;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedMethods("*").allowCredentials(false).allowedOrigins("*")
        .allowedHeaders("*");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**").addResourceLocations("file:/" + uploadPath + "\\");
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);

    return mapper;
  }

  public void addViewControllers(ViewControllerRegistry registry) {
    // registry.addViewController("/manager/login").setViewName("manager/login");
  }

  @Bean
  StructureFieldsRepository structureFieldRepository() {
    return new StructureFieldsRepositoryImplemented();
  }

  @Bean
  StructureCollectionsFieldsRepository structureCollectionsFieldRepository() {
    return new StructureCollectionsFieldsRepositoryImplemented();
  }

  @Bean
  CustomerClassRepository customerClassRepository() {
    return new CustomerClassRepositoryImplemented();
  }

  @Bean
  DynamicRoleRepository dynamicRoleRepository() {
    return new DynamicRoleRepositoryImplemented();
  }

  @Bean
  CollectionDynamicRoleRepository collectionDynamicRoleRepository() {
    return new CollectionDynamicRoleRepositoryImplemented();
  }

  @Bean
  FastStructureRepository fastStructureRepository() {
    return new FastStructureRepositoryImplemented();
  }

  @Bean
  LiteralRepository literalRepository() {
    return new LiteralRepositoryImplemented();
  }

  @Bean
  FiltersRecordRepository filtersRecordRepository() {
    return new FiltersRecordRepositoryImplemented();
  }

  @Bean
  FiltersGroupRepository filtersGroupRepository() {
    return new FiltersGroupRepositoryImplemented();
  }

  @Bean
  AccessRuleRepository accessRuleRepository() {
    return new AccessRuleRepositoryImplemented();
  }

  @Bean
  ClassOwnDynamicRepository classOwnDynamicRepository() {
    return new ClassOwnDynamicRepositoryImplemented();
  }

  @Bean
  AccessFiltersRecordRepository accessFiltersRecordRepository() {
    return new AccessFiltersRecordRepositoryImplemented();
  }

  @Bean
  AccessLiteralRepository accessLiteralRepositoryImplemented() {
    return new AccessLiteralRepositoryImplemented();
  }

  @Bean
  AccessFiltersGroupRepository accessFiltersGroupRepository() {
    return new AccessFiltersGroupRepositoryImplemented();
  }

  @Bean
  RecordsOwnerRepository recordsOwnerRepository() {
    return new RecordsOwnerRepositoryImplemented();
  }

  @Bean
  RecordsOwnerClassSettingsRepository recordsOwnerClassSettingsRepository() {
    return new RecordsOwnerClassSettingsRepositoryImplemented();
  }

  @Bean
  ContactAuthRepository contactAuthRepository() {
    return new ContactAuthRepositoryImplemented();
  }

  @Bean
  CompanyDomainRepository companyDomainRepository() {
    return new CompanyDomainRepositoryImplemented();
  }

  @Bean
  AuthorizedContactRepository authorizedContactRepository() {
    return new AuthorizedContactRepositoryImplemented();
  }

  @Bean
  DynamicClassesRepository dynamicClassesRepository() {
    return new DynamicClassesRepositoryImplemented();
  }

  @Bean
  ClassLogRepository classLogRepository() {
    return new ClassLogRepositoryImplemented();
  }

  @Bean
  DynamicTablesService dynamicTablesService() {
    return new DynamicTablesServiceImplemented();
  }

  @Bean
  CompanyRelationsRepository CompanyRelationsRepository() {
    return new CompanyRelationsRepositoryImplemented();
  }

  @Bean
  SystemDictionaryItemRepository systemDictionaryItemRepository() {
    return new SystemDictionaryItemRepositoryImplemented();
  }

  @Bean
  SignupFieldsRepository signupFieldsRepository() {
    return new SignupFieldsRepositoryImplemented();
  }

  @Bean
  CompanyContactRelationRepository companyContactRelationRepository() {
    return new CompanyContactRelationRepositoryImplemented();
  }

  @Bean
  InnerRecordsDynamicClassRepository innerRecordsDynamicClassRepository() {
    return new InnerRecordsDynamicClassRepositoryImplemented();
  }

  @Bean
  LngCompanyAccessRepository lngCompanyRelationsRepository() {
    return new LngCompanyAccessRepositoryImplemented();
  }

  @Bean
  LngCompanyRecordRelationsRepository lngCompanyRecordRelationsRepository() {
    return new LngCompanyRecordRelationsRepositoryImplemented();
  }

}
