package com.matas.liteconstruct.db.models.recordowner.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.recordowner.abstractmodel.RecordsOwnerModelAbstract;
import com.matas.liteconstruct.db.models.recordowner.model.RecordsOwnerModel;

public class RecordsOwnerRepositoryImplemented implements RecordsOwnerRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addRecordsOwner(RecordsOwnerModelAbstract recordsOwner) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s', '%6$s')",
        DBConstants.TBL_RECORD_OWNER, recordsOwner.getId(), recordsOwner.getContactId(),
        recordsOwner.getCompanyId(), recordsOwner.getServiceId(), recordsOwner.getRoleId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeRecordsOwner(RecordsOwnerModelAbstract recordsOwner) {
    String sql = String.format("delete from %1$s where id = '%2$s'", DBConstants.TBL_RECORD_OWNER,
        recordsOwner.getId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateRecordsOwner(RecordsOwnerModelAbstract recordsOwner) {
    String sql = String.format(
        "update %1$s set contact_id = '%3$s', company_id = '%4$s', service_id = '%5$s', role_id = '%6$s' where id = '%2$s'",
        DBConstants.TBL_RECORD_OWNER, recordsOwner.getId(), recordsOwner.getContactId(),
        recordsOwner.getCompanyId(), recordsOwner.getServiceId(), recordsOwner.getRoleId());
    jdbcTemplate.update(sql);
  }

  @Override
  public RecordsOwnerModelAbstract getRecordsOwnerById(UUID recordsOwnerId) {
    List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM "
        + DBConstants.TBL_RECORD_OWNER + " WHERE id = '" + recordsOwnerId + "' limit 1");
    System.out.println("getRecordsOwnerById: " + "SELECT * FROM " + DBConstants.TBL_RECORD_OWNER
        + " WHERE id = '" + recordsOwnerId + "' limit 1");
    if (rows != null && rows.size() > 0)
      return new RecordsOwnerModel((UUID) rows.get(0).get("id"),
          (UUID) rows.get(0).get("7052a1e5-8d00-43fd-8f57-f2e4de0c8b24"),
          (UUID) rows.get(0).get("2ed029b6-d745-4f85-8d9f-2dccd2a7da37"),
          (UUID) rows.get(0).get("3db2f640-e01a-42ac-904e-87a46e0373fd"),
          (UUID) rows.get(0).get("97086af0-956b-4380-a385-ea823cff377a"));
    else
      return null;
  }

  @Override
  public RecordsOwnerModelAbstract getRecordsOwnerByDynamicRoleData(UUID contactId, UUID companyId,
      UUID serviceId, UUID roleId) {
    List<Map<String, Object>> rows =
        jdbcTemplate.queryForList("SELECT * FROM " + DBConstants.TBL_RECORD_OWNER + " WHERE \""
            + DBConstants.CONTACT_ID + "\" = '" + contactId + "' and \"" + DBConstants.COMPANY_ID
            + "\" = '" + companyId + "' and \"" + DBConstants.SERVICE_ID + "\" = '" + serviceId
            + "' and \"" + DBConstants.ROLE_ID + "\" = '" + roleId + "' limit 1");
    if (rows != null && rows.size() > 0)
      return new RecordsOwnerModel((UUID) rows.get(0).get("id"),
          (UUID) rows.get(0).get("7052a1e5-8d00-43fd-8f57-f2e4de0c8b24"),
          (UUID) rows.get(0).get("2ed029b6-d745-4f85-8d9f-2dccd2a7da37"),
          (UUID) rows.get(0).get("3db2f640-e01a-42ac-904e-87a46e0373fd"),
          (UUID) rows.get(0).get("97086af0-956b-4380-a385-ea823cff377a"));
    else
      return null;
  }

}
