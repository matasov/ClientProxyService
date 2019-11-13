package com.matas.liteconstruct.db.models.companycontactclass.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.companycontactclass.abstractmodel.CompanyContactRelationAbstract;
import com.matas.liteconstruct.db.models.companycontactclass.model.CompanyContactRelation;
import com.matas.liteconstruct.db.models.companydomain.abstractmodel.CompanyDomainAbstract;
import com.matas.liteconstruct.db.models.companydomain.model.CompanyDomain;
import com.matas.liteconstruct.db.models.companydomain.repos.CompanyDomainRepositoryImplemented;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompanyContactRelationRepositoryImplemented
    implements CompanyContactRelationRepository {
  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addCompanyContactRelation(CompanyContactRelationAbstract companyContactRelationItem) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s')",
        DBConstants.TBL_COMPANY_CONTACT_CLASS_RELATION, companyContactRelationItem.getCompanyId(),
        companyContactRelationItem.getClass());
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeCompanyContactRelation(
      CompanyContactRelationAbstract companyContactRelationItem) {
    String sql = String.format("delete from %1$s where company_id = '%2$s'",
        DBConstants.TBL_COMPANY_CONTACT_CLASS_RELATION, companyContactRelationItem.getCompanyId());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateCompanyContactRelation(
      CompanyContactRelationAbstract companyContactRelationItem) {
    String sql = String.format("update %1$s set class_id = '%4$s' where company_id = '%2$s'",
        DBConstants.TBL_COMPANY_CONTACT_CLASS_RELATION, companyContactRelationItem.getCompanyId(),
        companyContactRelationItem.getClass());
    jdbcTemplate.update(sql);
  }

  @Override
  public CompanyContactRelationAbstract getCompanyContactRelationByCompanyId(UUID companyId) {
    String sql = String.format("SELECT * FROM %1$s WHERE company_id = '%2$s'",
        DBConstants.TBL_COMPANY_CONTACT_CLASS_RELATION, companyId);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    log.info("getCompanyDomainByCompanyId: {}", sql);
    if (rows != null && rows.size() > 0)
      return new CompanyContactRelation((UUID) rows.get(0).get("company_id"),
          (UUID) rows.get(0).get("class_id"));
    else
      return null;
  }
}
