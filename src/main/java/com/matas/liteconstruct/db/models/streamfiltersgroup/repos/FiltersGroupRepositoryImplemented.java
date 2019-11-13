package com.matas.liteconstruct.db.models.streamfiltersgroup.repos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.streamfiltersgroup.abstractmodel.FiltersGroupModelAbstract;
import com.matas.liteconstruct.db.models.streamfiltersgroup.model.FiltersGroupModel;

public class FiltersGroupRepositoryImplemented implements FiltersGroupRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addFiltersGroup(FiltersGroupModelAbstract filterGroup) {

    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s')",
        DBConstants.TBL_FILTER_GROUP, filterGroup.getId(), filterGroup.getName(),
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
  public void removeFiltersGroup(FiltersGroupModelAbstract filterGroup) {
    String sql = String.format("delete from %1$s where id = '%2$s'", DBConstants.TBL_FILTER_GROUP,
        filterGroup.getId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateFiltersGroup(FiltersGroupModelAbstract filterGroup) {
    String sql = String.format(
        "update %1$s set name = '%3$s', company_id = '%4$s', map = '%5$s', edit_access = '%6$s' where id = '%2$s'",
        DBConstants.TBL_FILTER_GROUP, filterGroup.getId(), filterGroup.getName(),
        filterGroup.getCompanyId(), getJsonStringForQuery(filterGroup.getMapJsonFilters()),
        filterGroup.getEditAccess());
    jdbcTemplate.update(sql);
  }

  @Override
  public FiltersGroupModelAbstract getFiltersGroupById(UUID filterGroupId) {
    ArrayList<FiltersGroupModelAbstract> result = new ArrayList<>(1);
    try {
      String sql = String.format("select * from %1$s where id = '%2$s'",
          DBConstants.TBL_FILTER_GROUP, filterGroupId);
      jdbcTemplate
          .query(sql,
              (rs, rowNum) -> new FiltersGroupModel(CommonMethods.getUUID(rs.getString("id")),
                  rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
                  getMapFromQuery(rs.getString("map")), rs.getShort("edit_access"), null))
          .forEach(group -> {
            result.add(group);
          });
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
    } finally {
      return result.get(0);
    }
  }

  @Override
  public List<FiltersGroupModelAbstract> getFiltersGroupsByCompanyIdForData(UUID companyId) {
    ArrayList<FiltersGroupModelAbstract> result = new ArrayList<>(1);
    try {
      String sql = String.format("select * from %1$s where company_id = '%2$s'",
          DBConstants.TBL_FILTER_GROUP, companyId);
      jdbcTemplate
          .query(sql,
              (rs, rowNum) -> new FiltersGroupModel(CommonMethods.getUUID(rs.getString("id")),
                  rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
                  getMapFromQuery(rs.getString("map")), rs.getShort("edit_access"), null))
          .forEach(group -> {
            result.add(group);
          });
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
    } finally {
      return result;
    }
  }

  @Override
  public List<FiltersGroupModelAbstract> getFiltersGroupsByCompanyIdForEdit(UUID companyId,
      short forAccess, short editAccess) {
    ArrayList<FiltersGroupModelAbstract> result = new ArrayList<>(1);
    try {
      String sql = String.format(
          "select * from %1$s where company_id = '%2$s' and editAccess >= '%3$s' and for_access = '%4$s'",
          DBConstants.TBL_FILTER_GROUP, companyId);
      jdbcTemplate
          .query(sql,
              (rs, rowNum) -> new FiltersGroupModel(CommonMethods.getUUID(rs.getString("id")),
                  rs.getString("name"), CommonMethods.getUUID(rs.getString("company_id")),
                  getMapFromQuery(rs.getString("map")), rs.getShort("edit_access"), null))
          .forEach(group -> {
            result.add(group);
          });
    } catch (Exception sqlex) {
      sqlex.printStackTrace();
    } finally {
      return result;
    }
  }

}
