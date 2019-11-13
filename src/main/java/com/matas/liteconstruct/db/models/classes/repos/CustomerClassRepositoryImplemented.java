package com.matas.liteconstruct.db.models.classes.repos;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.classes.abstractmodel.CustomerClassModelAbstract;
import com.matas.liteconstruct.db.models.classes.model.CustomerClassModel;
import com.matas.liteconstruct.db.tools.StructrueCollectionEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerClassRepositoryImplemented implements CustomerClassRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addCustomerClass(CustomerClassModelAbstract customClass) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s')",
        DBConstants.TBL_REGISTRY_CLASSES, customClass.getId(), customClass.getName(),
        customClass.getType(), customClass.getPermission());
    log.info("addCustomerClass sql: {}", sql);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void removeCustomerClass(UUID classId) {
    String sql = String.format("delete from %1$s where id = '%2$s'",
        DBConstants.TBL_REGISTRY_CLASSES, classId);
    log.info("delete custom class: {}", sql);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void updateCustomerClass(CustomerClassModelAbstract customClass) {
    String sql = String.format(
        "update %1$s set name = '%3$s', type = '%4$s', permission = '%5$s' where id = '%2$s'",
        DBConstants.TBL_REGISTRY_CLASSES, customClass.getId(), customClass.getName(),
        customClass.getType(), customClass.getPermission());
    jdbcTemplate.update(sql);
  }

  @Override
  public Map<UUID, CustomerClassModelAbstract> listByType(int i, int permission) {
    String sql = "SELECT id, name, type, permission FROM registry_classes";
    String subquery = "";
    if (permission > -1) {
      subquery += " permission > '" + permission + "'";
    }
    if (i > -1) {
      subquery += " and type = '" + i + "'";
    }
    if (!subquery.equals("")) {
      sql += " WHERE " + subquery;
    }
    sql += " order by type";
    log.info("listByType: {}", sql);
    return Optional
        .ofNullable(jdbcTemplate.query(sql,
            (rs, rowNum) -> new CustomerClassModel(UUID.fromString(rs.getString("id")),
                rs.getString("name"), rs.getByte("type"), rs.getInt("permission"))))
        .orElse(Collections.emptyList()).stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
  }

  public CustomerClassModelAbstract getById(UUID id) {
    String sql = String.format("SELECT * FROM %1$s WHERE id = '%2$s' limit 1",
        DBConstants.TBL_REGISTRY_CLASSES, id);
    log.info("get by id: {}", sql);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new CustomerClassModel((UUID) rows.get(0).get("id"), (String) rows.get(0).get("name"),
          (byte) (int) rows.get(0).get("type"), (int) rows.get(0).get("permission"));
    else
      return null;
  }

  @Override
  public CustomerClassModelAbstract getByName(String name) {
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(
        "SELECT id, class_structure_fields.class_id, name, field_class, class_structure_fields.inner, show_name FROM class_structure_fields WHERE name = '"
            + name + "' limit 1");
    if (rows != null && rows.size() > 0)
      return new CustomerClassModel((UUID) rows.get(0).get("id"), (String) rows.get(0).get("name"),
          (byte) (int) rows.get(0).get(StructrueCollectionEnum.TYPE), (int) rows.get(0).get("permission"));
    else
      return null;
  }

}
