package com.matas.liteconstruct.db.models.accessfiltersgroup.factorygroup;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matas.liteconstruct.db.models.accessfiltersgroup.abstractmodel.AccessFiltersGroupModelAbstract;
import com.matas.liteconstruct.db.models.accessfiltersgroup.repos.AccessFiltersGroupRepository;
import com.matas.liteconstruct.db.models.accessfiltersrecord.abstractmodel.AccessFiltersRecordModelAbstract;
import com.matas.liteconstruct.db.models.accessfiltersrecord.repos.AccessFiltersRecordRepository;
import com.matas.liteconstruct.db.models.accessliteral.abstractmodel.AccessLiteralModelAbstract;
import com.matas.liteconstruct.db.models.accessliteral.repos.AccessLiteralRepository;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import com.matas.liteconstruct.db.tools.operations.SQLStaticOperations;
import lombok.Data;

@Data
@Service
public class AccessFactoryGroupImplemented implements AccessFactoryGroupAbstract {

  private static final org.slf4j.Logger LOGGER =
      LoggerFactory.getLogger(AccessFactoryGroupImplemented.class);

  private AccessFiltersRecordRepository filtersRecordRepository;

  private AccessFiltersGroupRepository filtersGroupRepository;

  private AccessLiteralRepository literalRepository;

  private UUID classId;

  private UUID filterGroupId;

  private AccessFiltersGroupModelAbstract filterGroup;

  private Map<String, Object> filtersMap;

  private Map<String, Object> permissions;

  private Map<UUID, AccessFiltersRecordModelAbstract> usedStaticFilterRecords;

  private Map<String, Object> requestParams;

  @Autowired
  public void setAccessFiltersRecordRepository(
      AccessFiltersRecordRepository filtersRecordRepository) {
    this.filtersRecordRepository = filtersRecordRepository;
  }

  @Autowired
  public void setAccessFiltersGroupRepository(
      AccessFiltersGroupRepository filtersGroupRepository) {
    this.filtersGroupRepository = filtersGroupRepository;
  }

  @Autowired
  public void setAccessLiteralRepository(AccessLiteralRepository literalRepository) {
    this.literalRepository = literalRepository;
  }

  @Override
  public void setWorkclassId(UUID classId) {
    this.classId = classId;
  }

  @Override
  public void setFilterGroupId(UUID filterGroupId) {
    this.filterGroupId = filterGroupId;
    filterGroup = filtersGroupRepository.getFiltersGroupById(filterGroupId);
    if (filterGroup != null) {
      this.filtersMap = filterGroup.getMapJsonFilters();
      usedStaticFilterRecords = filtersRecordRepository.getMapFiltersRecordsByListId(
          AccessFactoryGroupAbstract.getAllFiltersFromMap(filtersMap));
    }
  }

  @Override
  public String getSubquery(Map<String, Object> requestParams) {
    this.requestParams = requestParams;
    return getSubstringStaticForLayer(filtersMap);
  }

  private String getSubstringStaticForLayer(Map<String, Object> layer) {
    HashMap<String, String> signs = new HashMap<>(2);
    signs.put("0", " and ");
    signs.put("1", " or ");
    StringBuilder result = new StringBuilder(20);
    if (layer != null && layer.get("values") != null)
      for (Map.Entry<String, Object> values : ((Map<String, Object>) layer.get("values"))
          .entrySet()) {
        if (values.getValue() instanceof Map) {
          String subQueryLine = getSubstringStaticForLayer((Map<String, Object>) values.getValue());
          if (subQueryLine == null)
            return null;
          result.append("(").append(subQueryLine).append(")");
        } else {
          String subQueryLine =
              getSubqueryFromFilterRecords(UUID.fromString((String) values.getValue()));
          if (subQueryLine == null)
            return null;
          result.append(subQueryLine);
        }
        int index = Integer.parseInt(values.getKey().substring(2));
        if ((index + 1) < ((Map<String, Object>) layer.get("values")).size()) {
          String signKey = "s_" + index;
          result.append(signs.get(((Map<String, Object>) layer.get("signs")).get(signKey)));
        }
      }
    return result.toString();
  }

  private String getSubqueryFromFilterRecords(UUID filterRecordId) {
    AccessFiltersRecordModelAbstract element = usedStaticFilterRecords.get(filterRecordId);
    // LOGGER.info("structure element: {}", element.getComplexDataValue());
    // LOGGER.info("value element: {}", element.getComplexDataValue());
    if (element == null || element.getStructureLiteralModel() == null)
      return null;
    if (element.getStructureLiteralModel().getName() == null
        || element.getStructureLiteralModel().getName().equals("noname")) {
      String dataValue =
          getSimpleSubqueryForDataLiteral(element.getStructureLiteralId().toString());
      LOGGER.info("found data value: {}", dataValue);
      if (dataValue != null && element.getComplexDataValue().equals(dataValue))
        return "";
    }
    if (!preferredCheck(element.getStructureLiteralModel(), element)) {
      return null;
    }
    String structureLiteralData =
        getSubqueryForStructureLiteral(element.getStructureLiteralModel());
    // String complexData = "'" + element.getComplexDataValue() + "'";
    String complexData = getSimpleSubqueryForDataLiteral(element.getComplexDataValue());
    if (complexData != null)
      complexData = "'" + complexData + "'";
    if (element.getValueLiteralModel() != null
        && element.getValueLiteralModel().getClassId() != null) {
      complexData = getSubqueryForDataLiteral(element.getValueLiteralModel());
    }
    if (complexData == null) {
      return null;
    }
    return SQLStaticOperations.getOperationByIndexForSelects(structureLiteralData,
        element.getOperator(), complexData);
  }

  private boolean preferredCheck(AccessLiteralModelAbstract structureElement,
      AccessFiltersRecordModelAbstract element) {
    if (structureElement.getName() == null || structureElement.getName().equals("noname")) {
      String dataValue =
          getSimpleSubqueryForDataLiteral(element.getStructureLiteralId().toString());
      return (dataValue != null && element.getComplexDataValue().toString().equals(dataValue));
    }
    return true;
  }

  private String getSubqueryForDataLiteral(AccessLiteralModelAbstract literalModelAbstract) {
    String sql = String.format(
        "(select \"cc_%1$s_data_use\".\"%2$s\" from \"cc_%1$s_data_use\" where \"%3$s\" = '%4$s' limit 1)",
        literalModelAbstract.getClassId(), literalModelAbstract.getFieldId(),
        literalModelAbstract.getRecordFieldId(), literalModelAbstract.getRecordFieldValue());
    return sql;
  }

  private String getSimpleSubqueryForDataLiteral(String value) {
    LOGGER.info("getSimpleSubqueryForDataLiteral value: {}, params: {}, find: {}", value,
        requestParams, requestParams.get(FactoryGroupAbstract.getFirstPartOfUUID(value)));
    if (requestParams == null
        || !requestParams.containsKey(FactoryGroupAbstract.getFirstPartOfUUID(value))) {
      return null;
    }
    return (String) requestParams.get(FactoryGroupAbstract.getFirstPartOfUUID(value));
  }

  private String getSubqueryForStructureLiteral(AccessLiteralModelAbstract literalModelAbstract) {
    String sql = String.format(
        "(select \"cc_%1$s_data_use\".\"%2$s\" from \"cc_%1$s_data_use\" where \"%3$s\" = '%4$s' limit 1)",
        literalModelAbstract.getClassId(), literalModelAbstract.getFieldId(),
        literalModelAbstract.getRecordFieldId(), literalModelAbstract.getRecordFieldValue());
    return sql;
  }

}
