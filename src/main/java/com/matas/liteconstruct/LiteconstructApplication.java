package com.matas.liteconstruct;

import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.matas.liteconstruct.service.business.LiveLineQueuePoolExecutor;
import com.matas.liteconstruct.service.dynamic.CacheMainParams;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class LiteconstructApplication implements CommandLineRunner {

  private LiveLineQueuePoolExecutor liveLineQueuePoolExecutor;

  @Autowired
  public void setLiveLineQueuePoolExecutor(LiveLineQueuePoolExecutor liveLineQueuePoolExecutor) {
    this.liveLineQueuePoolExecutor = liveLineQueuePoolExecutor;
  }
  
  private CacheMainParams cacheMainParams;

  @Autowired
  public void setCacheMainParams(CacheMainParams cacheMainParams) {
    this.cacheMainParams = cacheMainParams;
  }
  // @Autowired
  // private JdbcTemplate jdbcTemplate;

  // private static final org.slf4j.Logger log =
  // LoggerFactory.getLogger(LiteconstructApplication.class);

  public static void main(String[] args) {

    SpringApplication.run(LiteconstructApplication.class, args);

  }

  @Override
  public void run(String... args) throws Exception {
    liveLineQueuePoolExecutor.initService();
    cacheMainParams.initService();
//    new StructureCollectionsService().addCustomClass(UUID.randomUUID(), "test", (byte) 1, 1,
//        "something",
//        new DynamicRoleModel(UUID.fromString("62900a19-88a9-4655-a7ac-71488070b659"), "superadmin",
//            UUID.fromString("af09ea17-d47c-452d-93de-2c89157b9d5b"),
//            UUID.fromString("b56b99b6-2c6f-4103-849a-e914e8594869"),
//            SystemRoles.SUPERADMIN_ROLE.getUUID()));
    // ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    // executor.scheduleAtFixedRate(() -> {
    //// log.info("in thread message");
    //// String str;
    //// try {
    ////// while ((str = queue.poll()) != null) {
    ////// call(str); // do further processing
    ////// }
    //// } catch (IOException e) {
    //// e.printStackTrace();
    //// }
    // }, 0, 500, TimeUnit.MILLISECONDS);
//     System.out.println(new
//     BCryptPasswordEncoder().encode("5b4cc5b9-d7b5-42c9-baab-c90b89d4ad9b"));
    // StructureFieldAbstract newStructureLine = new StructureFieldImplemented(
    // UUID.fromString("32d53f46-3f49-43d6-8bf2-cbbcc994d6c5"),
    // UUID.fromString("32d53f46-3f49-43d6-8bf2-cbbcc994d6c5"),
    // "test_field",
    // UUID.fromString("32d53f46-3f49-43d6-8bf2-cbbcc994d6c5"),
    // (byte)0,
    // "Test Field");
    // new StructureFieldsRepositoryImplemented().addStructureFields(newStructureLine);
    // jdbcTemplate.queryForList("select * from site_domains");
    // jdbcTemplate.query(
    // "SELECT id, class_structure_fields.class, name, fieldclass, class_structure_fields.inner,
    // show_name FROM class_structure_fields WHERE name = ?",
    // new Object[] {"id"},
    // (rs, rowNum) -> {})
    // .forEach(structure -> {
    // try {
    // //log.info(new ObjectMapper().writeValueAsString(structure));
    // } catch (JsonProcessingException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // });
  }

  @PreDestroy
  public void onExit() {
    liveLineQueuePoolExecutor.stopDaemon();
    cacheMainParams.stopDaemon();
    log.info("###STOP FROM THE LIFECYCLE###");
  }
}
