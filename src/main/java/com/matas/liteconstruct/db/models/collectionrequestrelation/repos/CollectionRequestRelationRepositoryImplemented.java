package com.matas.liteconstruct.db.models.collectionrequestrelation.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.collectionrequestrelation.abstractmodel.CollectionRequestRelationAbstract;
import com.matas.liteconstruct.db.models.collectionrequestrelation.model.CollectionRequestRelation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CollectionRequestRelationRepositoryImplemented
    implements CollectionRequestRelationRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addCollectionRequest(CollectionRequestRelationAbstract collectionRequest) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s')",
        DBConstants.TBL_CLASS_COLLECTION_REQUEST_RELATIONS, collectionRequest.getUrl(),
        collectionRequest.getClassId(), collectionRequest.getDynamicRoleId(),
        collectionRequest.getCollectionId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeCollectionRequest(CollectionRequestRelationAbstract collectionRequest) {
    String sql = String.format(
        "delete from %1$s where full_url = '%2$s' and class_id = '%3$s' and dynamic_role_id = '%4$s'",
        DBConstants.TBL_CLASS_COLLECTION_REQUEST_RELATIONS, collectionRequest.getUrl(),
        collectionRequest.getClassId(), collectionRequest.getDynamicRoleId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeClassCollectionRequestsByCompanyId(UUID classId, UUID companyId) {
    String sql = String.format(
        "delete from %1$s where class_id = '%3$s' and dynamic_role_id in "
            + "(SELECT id FROM \"registry_dynamic_role\" where company_id = '%3$s')",
        DBConstants.TBL_CLASS_COLLECTION_REQUEST_RELATIONS, classId, companyId);
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateCollectionRequest(CollectionRequestRelationAbstract collectionRequest) {
    removeCollectionRequest(collectionRequest);
    addCollectionRequest(collectionRequest);
  }

  @Override
  public CollectionRequestRelationAbstract getCollectionRequestByKeys(String url, UUID classId,
      UUID dynamicRoleId) {
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(
        "SELECT * FROM %1$s WHERE full_url = '%2$s' and class_id = '%3$s' and dynamic_role_id = '%4$s' limit 1",
        DBConstants.TBL_CLASS_COLLECTION_REQUEST_RELATIONS, url, classId, dynamicRoleId);
    if (rows != null && rows.size() > 0)
      return new CollectionRequestRelation((String) rows.get(0).get("full_url"),
          (UUID) rows.get(0).get("class_id"), (UUID) rows.get(0).get("dynamic_role_id"),
          (UUID) rows.get(0).get("collection_id"), (Integer) rows.get(0).get("edit_access"));
    else
      return null;
  }

  @Override
  public List<CollectionRequestRelationAbstract> getCollectionRequestForUrl(String url,
      UUID companyId, int editAccess) {
    String sql = String.format(
        "SELECT * FROM %1$s where full_url = '%2$s' and edit_access >= '%3$s' and dynamic_role_id in "
            + "(SELECT id FROM \"registry_dynamic_role\" where company_id = '%4$s')",
        DBConstants.TBL_CLASS_COLLECTION_REQUEST_RELATIONS, url, companyId, editAccess);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new CollectionRequestRelation(rs.getString("full_url"),
            CommonMethods.getUUID(rs.getString("class_id")),
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("collection_id")), rs.getInt("edit_access")));
  }

  @Override
  public List<CollectionRequestRelationAbstract> getCollectionRequestForClass(String url,
      UUID classId, int editAccess) {
    String sql = String.format(
        "SELECT * FROM %1$s where full_url = '%2$s' and edit_access >= '%3$s' and class_id = '%4$s')",
        DBConstants.TBL_CLASS_COLLECTION_REQUEST_RELATIONS, url, classId, editAccess);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new CollectionRequestRelation(rs.getString("full_url"),
            CommonMethods.getUUID(rs.getString("class_id")),
            CommonMethods.getUUID(rs.getString("dynamic_role_id")),
            CommonMethods.getUUID(rs.getString("collection_id")), rs.getInt("edit_access")));
  }

}
