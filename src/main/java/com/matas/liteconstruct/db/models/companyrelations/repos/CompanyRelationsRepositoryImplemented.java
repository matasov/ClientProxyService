package com.matas.liteconstruct.db.models.companyrelations.repos;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.companyrelations.abstractmodel.CompanyRelationsAbstract;
import com.matas.liteconstruct.db.models.companyrelations.model.CompanyRelations;
import com.matas.liteconstruct.db.models.stafflog.model.ClassLog;

public class CompanyRelationsRepositoryImplemented implements CompanyRelationsRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addCompanyRelations(CompanyRelationsAbstract companyRelation) {
    String sql = String.format(
        "INSERT INTO \"%1$s\" (\"master_company_id\", \"slave_company_id\", \"role_master_id\") VALUES "
            + "('%2$s', '%3$s', '%4$s')",
        DBConstants.TBL_REGISTRY_COMPANIES_LINKS, companyRelation.getMasterCompanyId(),
        companyRelation.getSlaveCompanyId(), companyRelation.getMasterCompanyRoleId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeCompanyRelationsBySlaveCompanyId(UUID slaveCompanyId) {
    String sql = String.format("delete from %1$s where slave_company_id = '%2$s'",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, slaveCompanyId);
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeCompanyRelationsByMasterCompanyRoleId(UUID slaveCompanyId,
      UUID masterCompanyRoleId) {
    String sql = String.format(
        "delete from %1$s where slave_company_id = '%2$s' AND role_master_id = '%3$s'",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, slaveCompanyId, masterCompanyRoleId);
    jdbcTemplate.update(sql);
  }

  @Override
  public CompanyRelationsAbstract getCompanyRelationsByMasterSlaveRole(UUID masterCompanyId,
      UUID slaveCompanyId, UUID masterCompanyRoleId) {
    String sql = String.format(
        "SELECT * FROM \"%1$s\" where master_company_id = '%2$s' and slave_company_id = '%3$s' and role_master_id = '%4$s' order by date_change asc",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, masterCompanyId, slaveCompanyId,
        masterCompanyRoleId);
    return jdbcTemplate
        .query(sql,
            (rs, rowNum) -> new CompanyRelations(
                CommonMethods.getUUID(rs.getString("master_company_id")),
                CommonMethods.getUUID(rs.getString("slave_company_id")),
                CommonMethods.getUUID(rs.getString("role_master_id"))))
        .stream().findAny().orElse(null);
  }

  @Override
  public List<CompanyRelationsAbstract> getCompanyRelationsByMaster(UUID masterCompanyId) {
    String sql = String.format(
        "SELECT * FROM \"%1$s\" where master_company_id = '%2$s' order by date_change asc",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, masterCompanyId);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new CompanyRelations(
            CommonMethods.getUUID(rs.getString("master_company_id")),
            CommonMethods.getUUID(rs.getString("slave_company_id")),
            CommonMethods.getUUID(rs.getString("role_master_id"))));
  }

  @Override
  public List<CompanyRelationsAbstract> getCompanyRelationsBySlave(UUID slaveCompanyId) {
    String sql = String.format(
        "SELECT * FROM \"%1$s\" where slave_company_id = '%2$s' order by date_change asc",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, slaveCompanyId);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new CompanyRelations(
            CommonMethods.getUUID(rs.getString("master_company_id")),
            CommonMethods.getUUID(rs.getString("slave_company_id")),
            CommonMethods.getUUID(rs.getString("role_master_id"))));
  }

  @Override
  public List<CompanyRelationsAbstract> getCompanyRelationsByMasterAndRole(UUID masterCompanyId,
      UUID masterCompanyRoleId) {
    String sql = String.format(
        "SELECT * FROM \"%1$s\" where master_company_id = '%2$s' and role_master_id = '%4$s' order by date_change asc",
        DBConstants.TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION, masterCompanyId, masterCompanyRoleId);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new CompanyRelations(
            CommonMethods.getUUID(rs.getString("master_company_id")),
            CommonMethods.getUUID(rs.getString("slave_company_id")),
            CommonMethods.getUUID(rs.getString("role_master_id"))));
  }


}
