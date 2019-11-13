package com.matas.liteconstruct.db.models.collectioncase.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.collectioncase.abstractmodel.CollectionCaseAbstract;
import com.matas.liteconstruct.db.models.collectioncase.model.CollectionCase;
import com.matas.liteconstruct.db.models.companydomain.abstractmodel.CompanyDomainAbstract;
import com.matas.liteconstruct.db.models.companydomain.model.CompanyDomain;
import com.matas.liteconstruct.db.models.companydomain.repos.CompanyDomainRepositoryImplemented;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CollectionCaseRepositoryImplemented implements CollectionCaseRepository {
  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addCollectionCase(CollectionCaseAbstract collectionCase) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s')",
        DBConstants.TBL_COLLECTION_CASE, collectionCase.getMetaKey(), collectionCase.getClassId(),
        collectionCase.getDynamicRoleId(), collectionCase.getCollectionId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeCollectionCase(CollectionCaseAbstract collectionCase) {
    String sql = String.format(
        "delete from %1$s where meta_key = '%2$s' and class_id = '%3$s' and dynamic_role_id = '%4$s'",
        DBConstants.TBL_COLLECTION_CASE, collectionCase.getMetaKey(), collectionCase.getClassId(),
        collectionCase.getDynamicRoleId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateCollectionCase(CollectionCaseAbstract collectionCase) {
    String sql = String.format(
        "update %1$s set collection_id = '%5$s' where meta_key = '%2$s' and class_id = '%3$s' and dynamic_role_id = '%4$s'",
        DBConstants.TBL_COLLECTION_CASE, collectionCase.getMetaKey(), collectionCase.getClassId(),
        collectionCase.getDynamicRoleId(), collectionCase.getCollectionId());
    jdbcTemplate.update(sql);
  }

  @Override
  public CollectionCaseAbstract getCollectionCaseByParams(String metaKey, UUID classId,
      UUID dynamicRoleId) {
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(String.format(
        "SELECT * FROM %1$s WHERE meta_key = '%2$s' and class_id = '%3$s' and dynamic_role_id = '%4$s'",
        DBConstants.TBL_COLLECTION_CASE, metaKey, classId, dynamicRoleId));
    if (rows != null && rows.size() > 0)
      return new CollectionCase((String) rows.get(0).get("meta_key"),
          (UUID) rows.get(0).get("class_id"), (UUID) rows.get(0).get("dynamic_role_id"),
          (UUID) rows.get(0).get("collection_id"));
    else
      return null;
  }

  @Override
  public List<CollectionCaseAbstract> getCollectionCasesByCaseAndDynamicRole(String metaKey,
      UUID dynamicRoleId) {
    String sql =
        String.format("SELECT * FROM %1$s WHERE meta_key = '%2$s' and dynamic_role_id = '%3$s'",
            DBConstants.TBL_COLLECTION_CASE, metaKey, dynamicRoleId);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new CollectionCase(rs.getString("meta_key"),
            CommonMethods.getUUID(rs.getString("class_id")),
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("collection_id"))));
  }

  @Override
  public List<CollectionCaseAbstract> getCollectionCasesByCaseAndClassId(String metaKey,
      UUID classId) {
    String sql = String.format("SELECT * FROM %1$s WHERE meta_key = '%2$s' and class_id = '%3$s'",
        DBConstants.TBL_COLLECTION_CASE, metaKey, classId);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new CollectionCase(rs.getString("meta_key"),
            CommonMethods.getUUID(rs.getString("class_id")),
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("collection_id"))));
  }
}
