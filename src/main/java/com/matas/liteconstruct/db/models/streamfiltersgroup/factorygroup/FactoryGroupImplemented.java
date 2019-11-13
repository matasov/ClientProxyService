package com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matas.liteconstruct.db.models.streamfiltersgroup.abstractmodel.FiltersGroupModelAbstract;
import com.matas.liteconstruct.db.models.streamfiltersgroup.repos.FiltersGroupRepository;
import com.matas.liteconstruct.db.models.streamfiltersrecord.abstractmodel.FiltersRecordModelAbstract;
import com.matas.liteconstruct.db.models.streamfiltersrecord.repos.FiltersRecordRepository;
import com.matas.liteconstruct.db.models.streamliterals.abstractmodel.LiteralModelAbstract;
import com.matas.liteconstruct.db.models.streamliterals.repos.LiteralRepository;
import com.matas.liteconstruct.db.tools.operations.SQLStaticOperations;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Data
public class FactoryGroupImplemented implements FactoryGroupAbstract {

  private FiltersRecordRepository filtersRecordRepository;

  private FiltersGroupRepository filtersGroupRepository;

  private LiteralRepository literalRepository;

  private UUID classId;

  private List<UUID> filterGroupIds;

  private List<FiltersGroupModelAbstract> filterGroups;

  private List<Map<String, Object>> dynamicMap;

  private Map<UUID, FiltersRecordModelAbstract> usedStaticFilterRecords;

  @Autowired
  public void setFiltersRecordRepository(FiltersRecordRepository filtersRecordRepository) {
    this.filtersRecordRepository = filtersRecordRepository;
  }

  @Autowired
  public void setFiltersGroupRepository(FiltersGroupRepository filtersGroupRepository) {
    this.filtersGroupRepository = filtersGroupRepository;
  }

  @Autowired
  public void setLiteralRepository(LiteralRepository literalRepository) {
    this.literalRepository = literalRepository;
  }

  @Override
  public void setWorkclassId(UUID classId) {
    this.classId = classId;
  }

  @Override
  public void setFilterGroupId(List<UUID> filterGroupIds) {
    if (filterGroupIds != null) {
      this.filterGroupIds =
          (List<UUID>) filterGroupIds.stream().distinct().collect(Collectors.toList());
      filterGroups = filterGroupIds.stream().map(e -> filtersGroupRepository.getFiltersGroupById(e))
          .collect(Collectors.toList());
      usedStaticFilterRecords =
          filterGroups.stream().flatMap(e -> getMapFiltersRecordsByListId(e).entrySet().stream())
              .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
    }
  }

  private Map<UUID, FiltersRecordModelAbstract> getMapFiltersRecordsByListId(
      FiltersGroupModelAbstract group) {
    return filtersRecordRepository.getMapFiltersRecordsByListId(
        FactoryGroupAbstract.getAllFiltersFromMap(group.getMapJsonFilters()));
  }

  @Override
  public void setDynamicFilters(List<Map<String, Object>> dynamicMap) {
    this.dynamicMap = dynamicMap;
  }

  @Override
  public String getSubqueryForCredentials(UUID parentclassId, UUID parentFieldId, UUID classId) {
    String fastKey = FactoryGroupAbstract.getFastKey(parentclassId, parentFieldId, classId);
    String staticFilterSubquery = getSubqueryFromStaticFilters(fastKey);

    String dynamicFilterSubquery = getSubqueryFromDynamicFilters(fastKey);
    StringBuilder result = new StringBuilder(100).append("");
    if (!staticFilterSubquery.equals("") || !dynamicFilterSubquery.equals("")) {
      // result.append("where ");
      if (!staticFilterSubquery.equals("")) {
        result.append(staticFilterSubquery);
        if (!dynamicFilterSubquery.equals("")) {
          result.append(" and ").append(dynamicFilterSubquery);
        }
      } else {
        result.append(dynamicFilterSubquery);
      }
    }
    return result.toString();
  }

  private String getSubqueryFromStaticFilters(String fastKey) {
    if (filterGroups != null) {
      ArrayList<String> resultTokens = new ArrayList<>();
      // StringBuilder resultString = new StringBuilder("");
      for (FiltersGroupModelAbstract currentGroup : filterGroups)
        for (Entry<String, Object> currenLayer : currentGroup.getMapJsonFilters().entrySet()) {
          if (fastKey.equals(getKeyFromStaticFilter(
              UUID.fromString((String) ((Map<String, Object>) ((Map<String, Object>) currentGroup
                  .getMapJsonFilters().get(currenLayer.getKey())).get("values")).get("v_0"))))) {
            // do something
            String layerResult = getSubstringStaticForLayer(
                (Map<String, Object>) currentGroup.getMapJsonFilters().get(currenLayer.getKey()));
            if (layerResult != null && !layerResult.isEmpty()) {
              resultTokens.add("(" + layerResult + ")");
            }
          }
        }
      if (!resultTokens.isEmpty()) {
        return resultTokens.stream().collect(Collectors.joining(" and "));
      }
    }
    return "";
  }

  private String getSubqueryFromDynamicFilters(String fastKey) {
    if (dynamicMap != null) {
      dynamicMap.forEach(x -> {
        log.info("key for element: {}",
            SQLStaticOperations.getOperationByIndex("test field", (short) 0, "test value"));
      });
      return dynamicMap.stream().flatMap(x -> x.entrySet().stream())
          .filter(x -> getKeyForDynamicFilter(x.getKey()).startsWith(fastKey))
          .map(x -> SQLStaticOperations.getOperationByIndex(getFieldFromDynamic(x.getKey()),
              (short) Integer
                  .parseInt((String) ((Map<String, Object>) x.getValue()).get("operator")),
              (String) ((Map<String, Object>) x.getValue()).get("value")))
          .collect(Collectors.joining(" and "));
    }
    return "";
  }

  private String getKeyForDynamicFilter(String dynamicKey) {
    try {
      return Stream.of(dynamicKey.split("\\."))
          .map(x -> x.equals("null") ? "null" : FactoryGroupAbstract.getFirstPartOfUUID(x))
          .collect(Collectors.joining("."));
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  private String getFieldFromDynamic(String dynamicKey) {
    try {
      return "\"cc_" + dynamicKey.split("\\.")[2] + "_data_use\".\"" + dynamicKey.split("\\.")[3]
          + "\"";
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  private String getKeyFromStaticFilter(UUID filterRecordId) {
    FiltersRecordModelAbstract currentRecord = usedStaticFilterRecords.get(filterRecordId);
    if (currentRecord == null) {
      return null;
    }
    try {
      return FactoryGroupAbstract.getFastKey(
          currentRecord.getStructureLiteralModel().getParentclassId(),
          currentRecord.getStructureLiteralModel().getParentFieldId(),
          currentRecord.getStructureLiteralModel().getClassId());
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  private String getSubstringStaticForLayer(Map<String, Object> layer) {
    HashMap<String, String> signs = new HashMap<>(2);
    signs.put("0", " and ");
    signs.put("1", " or ");
    StringBuilder result = new StringBuilder(20);
    for (Map.Entry<String, Object> values : ((Map<String, Object>) layer.get("values"))
        .entrySet()) {
      if (values.getValue() instanceof Map) {
        result.append("(")
            .append(getSubstringStaticForLayer((Map<String, Object>) values.getValue()))
            .append(")");
      } else {
        result.append(getSubqueryFromFilterRecords(UUID.fromString((String) values.getValue())));
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
    // System.out.println("getSubqueryFromFilterRecords: " + filterRecordId);
    FiltersRecordModelAbstract element = usedStaticFilterRecords.get(filterRecordId);
    // System.out.println("present filters: " + usedStaticFilterRecords);
    StringBuilder result = new StringBuilder(100);
    result.append("\"cc_").append(element.getStructureLiteralModel().getClassId())
        .append("_data_use\".\"").append(element.getStructureLiteralModel().getFieldId())
        .append("\"");
    String complexData = element.getComplexDataValue();
    if (element.getValueLiteralModel() != null
        && element.getValueLiteralModel().getClassId() != null) {
      complexData = getSubqueryForDataLiteral(element.getValueLiteralModel());
    }
    return SQLStaticOperations.getOperationByIndex(result.toString(), element.getOperator(),
        complexData);
  }

  /**
   * Join it!!!
   * 
   * @param literalModelAbstract
   * @return
   */
  private String getSubqueryForDataLiteral(LiteralModelAbstract literalModelAbstract) {
    // todo this!!!
    return literalRepository.getValueFromDataByLiteral(literalModelAbstract);
  }

}
