package com.matas.liteconstruct.db.models.accessfiltersgroup.repos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.accessfiltersgroup.abstractmodel.AccessFiltersGroupModelAbstract;
import com.matas.liteconstruct.db.models.accessfiltersgroup.model.AccessFiltersGroupModel;

public class AccessFiltersGroupRepositoryImplemented implements AccessFiltersGroupRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void addFiltersGroup(AccessFiltersGroupModelAbstract filterGroup) {

    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s')",
        DBConstants.TBL_ACCESS_FILTER_GROUP, filterGroup.getId(), filterGroup.getName(),
        filterGroup.getCompanyId(), getJsonStringForQuery(filterGroup.getMapJsonFilters()),
        filterGroup.getEditAccess());
    jdbcTemplate.update(sql);
  }

  private String getJsonStringForQuery(Map<String, Object> filterGroupMap) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(filterGroupMap);
    } catch (JsonProcessingException jex) {
      return "{}";
    }
  }

  private Map<String, Object> getMapFromQuery(String jsonCode) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(jsonCode, new TypeReference<Map<String, Object>>() {});
    } catch (IOException e) {
      return new HashMap<>();
    }
  }

  @Override
  public void removeFiltersGroup(AccessFiltersGroupModelAbstract filterGroup) {
    String sql = String.format("delete from %1$s where id = '%2$s'",
        DBConstants.TBL_ACCESS_FILTER_GROUP, filterGroup.getId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateFiltersGroup(AccessFiltersGroupModelAbstract filterGroup) {
    String sql = String.format(
        "update %1$s set name = '%3$s', company_id = '%4$s', map = '%5$s', edit_access = '%6$s' where id = '%2$s'",
        DBConstants.TBL_ACCESS_FILTER_GROUP, filterGroup.getId(), filterGroup.getName(),
        filterGroup.getCompanyId(), getJsonStringForQuery(filterGroup.getMapJsonFilters()),
        filterGroup.getEditAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public AccessFiltersGroupModelAbstract getFiltersGroupById(UUID filterGroupId) {
    ArrayList<AccessFiltersGroupModelAbstract> result = new ArrayList<>(1);
    try {
      String sql = String.format("select * from %1$s where id = '%2$s'",
          DBConstants.TBL_ACCESS_FILTER_GROUP, filterGroupId);
      List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
      
      if (rows != null && !rows.isEmpty()) {
        return new AccessFiltersGroupModel(((UUID)rows.get(0).get("id")),
            (String) rows.get(0).get("name"),
            ((UUID) rows.get(0).get("company_id")),
            getMapFromQuery((String)((PGobject) rows.get(0).get("map")).toString()),
            (short)(int) rows.get(0).get("edit_access"), null);
      }
      // return jdbcTemplate
      // .query(sql,
      // (rs, rowNum) -> new AccessFiltersGroupModel(CommonMethods.getUUID(rs.getString("id")),
      // rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
      // getMapFromQuery(rs.getString("map")), rs.getShort("edit_access"), null))
      // .forEach(group -> {
      // result.add(group);
      // });
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
    }
    return null;
  }

  @Override
  public List<AccessFiltersGroupModelAbstract> getFiltersGroupsByCompanyIdForData(UUID companyId) {
    //ArrayList<AccessFiltersGroupModelAbstract> result = new ArrayList<>(1);
    try {
      String sql = String.format("select * from %1$s where company_id = '%2$s'",
          DBConstants.TBL_ACCESS_FILTER_GROUP, companyId);
      return jdbcTemplate
          .query(sql,
              (rs, rowNum) -> new AccessFiltersGroupModel(CommonMethods.getUUID(rs.getString("id")),
                  rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
                  getMapFromQuery(rs.getString("map")), rs.getShort("edit_access"), null));
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
      return null;
    }
  }

  @Override
  public List<AccessFiltersGroupModelAbstract> getFiltersGroupsByCompanyIdForEdit(UUID companyId,
      short editAccess) {
    //ArrayList<AccessFiltersGroupModelAbstract> result = new ArrayList<>(1);
    try {
      String sql = String.format(
          "select * from %1$s where company_id = '%2$s' and editAccess >= '%3$s' and for_access = '%4$s'",
          DBConstants.TBL_ACCESS_FILTER_GROUP, companyId);
      return jdbcTemplate
          .query(sql,
              (rs, rowNum) -> new AccessFiltersGroupModel(CommonMethods.getUUID(rs.getString("id")),
                  rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
                  getMapFromQuery(rs.getString("map")), rs.getShort("edit_access"), null));
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
      return null;
    }
  }

}
