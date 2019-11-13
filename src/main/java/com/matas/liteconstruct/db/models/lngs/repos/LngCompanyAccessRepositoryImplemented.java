package com.matas.liteconstruct.db.models.lngs.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.lngs.abstractmodel.LngCompanyAccess;
import com.matas.liteconstruct.db.models.lngs.model.LngCompanyAccessImplemented;

public class LngCompanyAccessRepositoryImplemented implements LngCompanyAccessRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addLng(LngCompanyAccess lng) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s')",
        DBConstants.LNG_COMPANY_RELATIONS, lng.getCompanyId(), lng.getLngId(), lng.getDescription(),
        lng.isMain());
    jdbcTemplate.execute(sql);
  }

  @Override
  public void removeLngByCompanyAndId(UUID companyId, UUID lngId) {
    String sql = String.format("delete from %1$s where company_id = '%2$s' and lng_id = '%3$s'",
        DBConstants.LNG_COMPANY_RELATIONS, companyId, lngId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void removeLngsByCompanyId(UUID companyId) {
    String sql = String.format("delete from %1$s where company_id = '%2$s'",
        DBConstants.LNG_COMPANY_RELATIONS, companyId);
    jdbcTemplate.execute(sql);
  }

  @Override
  public void updateLng(LngCompanyAccess lng) {
    String sql = String.format(
        "update %1$s set company_id = '%2$s', lng_id = '%3$s', description = '%4$s', is_main = '%5$s' where company_id = '%2$s' and lng_id = '%3$s'",
        DBConstants.LNG_COMPANY_RELATIONS, lng.getCompanyId(), lng.getLngId(), lng.getDescription(),
        lng.isMain());
    jdbcTemplate.update(sql);
  }

  @Override
  public LngCompanyAccess getMainLngByCompanyId(UUID companyId) {
    String sql = String.format("SELECT * FROM %1$s where company_id = '%2$s' and is_main = 'true'",
        DBConstants.LNG_COMPANY_RELATIONS, companyId);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new LngCompanyAccessImplemented((UUID) rows.get(0).get("company_id"),
          (UUID) rows.get(0).get("lng_id"), (String) rows.get(0).get("description"),
          (Boolean) rows.get(0).get("is_main"));
    else
      return null;
  }

  @Override
  public LngCompanyAccess getLngByTokenCompanyId(UUID companyId, String token) {
    String sql = String.format(
        "SELECT \"%1$s\".* FROM \"%2$s\" JOIN \"%1$s\" ON \"%1$s\".\"lng_id\" = \"%2$s\".\"id\" AND \"%1$s\".\"company_id\" = '%3$s' where \"%2$s\".\"short\" = '%4$s'",
        DBConstants.LNG_COMPANY_RELATIONS, DBConstants.LNG_REGISTRY, companyId, token);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new LngCompanyAccessImplemented((UUID) rows.get(0).get("company_id"),
          (UUID) rows.get(0).get("lng_id"), (String) rows.get(0).get("description"),
          (Boolean) rows.get(0).get("is_main"));
    else
      return null;
  }

  @Override
  public List<LngCompanyAccess> getLngsByCompanyId(UUID companyId) {
    String sql = String.format("SELECT * FROM %1$s where company_id = '%2$s'",
        DBConstants.LNG_COMPANY_RELATIONS, companyId);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new LngCompanyAccessImplemented(
            CommonMethods.getUUID(rs.getString("company_id")),
            CommonMethods.getUUID(rs.getString("lng_id")), rs.getString("description"),
            rs.getBoolean("is_main")));
  }

}
