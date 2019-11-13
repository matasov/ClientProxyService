package com.matas.liteconstruct.db.models.systemdictionary.repos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.systemdictionary.abstractmodel.SystemDictionaryItemAbstract;
import com.matas.liteconstruct.db.models.systemdictionary.model.SystemDictionaryItem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemDictionaryItemRepositoryImplemented implements SystemDictionaryItemRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addSystemDictionaryItem(SystemDictionaryItemAbstract dictionaryItem) {
    String sql = String.format("insert into %1$s values ('%2$s', '%3$s', '%4$s', '%5$s')",
        DBConstants.TBL_DICTIONARY_SYSTEM, dictionaryItem.getCompanyId(), dictionaryItem.getLang(),
        dictionaryItem.getMetaKey(), dictionaryItem.getValue());
    jdbcTemplate.update(sql);
  }

  @Override
  public void updateSystemDictionaryItem(SystemDictionaryItemAbstract dictionaryItem) {
    String sql = String.format(
        "update %1$s set value = '%5$s' where company_id = '%2$s' and lang = '%3$s' and meta_key = '%4$s'",
        DBConstants.TBL_DICTIONARY_SYSTEM, dictionaryItem.getCompanyId(), dictionaryItem.getLang(),
        dictionaryItem.getMetaKey(), dictionaryItem.getValue());
    jdbcTemplate.update(sql);
  }

  @Override
  public SystemDictionaryItemAbstract getSystemDictionaryItemByParams(UUID companyId, String lang,
      String metaKey) {
    String sql = String.format(
        "SELECT * FROM \"%1$s\" WHERE company_id = '%2$s' and lang = '%3$s' and meta_key = '%4$s'",
        DBConstants.TBL_DICTIONARY_SYSTEM, companyId, lang, metaKey);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    if (rows != null && rows.size() > 0)
      return new SystemDictionaryItem((UUID) rows.get(0).get("class_id"),
          (String) rows.get(0).get("lang"), (String) rows.get(0).get("meta_key"),
          (String) rows.get(0).get("value"));
    else
      return null;
  }

  @Override
  public SystemDictionaryItemAbstract getSystemDictionaryItemByParamsLongWay(UUID companyId,
      String lang, String metaKey) {
    SystemDictionaryItemAbstract found = getSystemDictionaryItemByParams(companyId, lang, metaKey);
    if (found == null) {
      found = getSystemDictionaryItemByParams(companyId, "en", metaKey);
    }
    return found;
  }

  @Override
  public void removeItemByParams(UUID companyId, String lang, String key) {
    String sql = String.format(
        "DELETE FROM \"%1$s\" WHERE company_id = '%2$s' and lang = '%3$s' and meta_key = '%4$s'",
        DBConstants.TBL_DICTIONARY_SYSTEM, companyId, key);
    jdbcTemplate.update(sql);
  }

  @Override
  public void removeItemsByCompanyId(UUID companyId) {
    String sql = String.format("DELETE FROM \"%1$s\" WHERE company_id = '%2$s'",
        DBConstants.TBL_DICTIONARY_SYSTEM, companyId);
    jdbcTemplate.update(sql);
  }

  @Override
  public List<SystemDictionaryItemAbstract> getSystemDictionaryItemsByCompanyId(UUID companyId) {
    String sql = String.format("select * from %1$s where class_id = '%2$s'",
        DBConstants.TBL_DICTIONARY_SYSTEM, companyId);
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> new SystemDictionaryItem(CommonMethods.getUUID(rs.getString("class_id")),
            rs.getString("lang"), rs.getString("meta_key"), rs.getString("value")));
  }
}
