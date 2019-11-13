package com.matas.liteconstruct.db.models.dynamicclass.queryfactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.models.dynamicclass.abstractmodel.WhereClauseDynamicClassAbstract;
import com.matas.liteconstruct.db.models.recordowner.abstractmodel.RecordsOwnerClassSettingsAbstract;
import com.matas.liteconstruct.db.models.sortfactory.repos.SortFactory;
import com.matas.liteconstruct.db.models.sortfactory.repos.SortFactoryAbstract;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupImplemented;
import com.matas.liteconstruct.db.tools.StructrueCollectionEnum;
import com.matas.liteconstruct.db.tools.sqlselectfactory.AbstractFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DynamicClassesQueryFactory extends AbstractFactory {

  private FactoryGroupImplemented factoryGroupImplemented;

  SortFactoryAbstract sortFactory;

  private final int MAX_LIMIT = 50;
  @Setter
  private int startLimitValue = 0;
  @Setter
  private int endLimitValue = MAX_LIMIT;
  // private RecordsOwnerClassSettingsAbstract recordOwnerItem;
  // private Map<String, Object> incomingRequestBody;

  // @Autowired
  // public void setFactoryGroupImplemented(FactoryGroupImplemented factoryGroupImplemented) {
  // this.factoryGroupImplemented = factoryGroupImplemented;
  // }

  private Map<UUID, List<String>> notNullFields;

  private List<Map<String, Object>> setDefaultSortMap(UUID classId,
      List<Map<String, Object>> sortMap, Map<String, Object> structure) {
    if (sortMap == null || sortMap.isEmpty()) {
      // create new sort by change field
      final Map<String, Object> sortingObject = new HashMap() {
        {
          put("map",
              "null.null." + FactoryGroupAbstract.getFirstPartOfUUID(classId) + "."
                  + FactoryGroupAbstract.getFirstPartOfUUID(
                      UUID.fromString((String) ((Map<String, Object>) structure.get("3"))
                          .get(StructrueCollectionEnum.ID.toString()))));
          put("direct", "0");
        }
      };
      return new ArrayList() {
        {
          add(sortingObject);
        }
      };
    } else
      return sortMap;
  }

  private void setLimits(List<Integer> limits) {
    if (limits == null || limits.size() < 2 || limits.get(0) > limits.get(1)) {
      return;
    }
    startLimitValue = limits.get(0);
    endLimitValue = limits.get(1);
  }

  private void setSorting(UUID customclassId, Map<String, Object> structure,
      List<Map<String, Object>> sortMap) {
    sortFactory = new SortFactory();
    sortFactory.setMasterClass(customclassId);
    sortFactory.setStructureMap(structure);
    // sortFactory.setSortingMap("{\"ord_0\":{\"map\":\"fd27729c.e0cfbed0.5cb705ea.d68f2806\", "
    // + " \"direct\":\"0\"}, \"ord_1\":{\"map\":\"e07f8c02.931bcc8a.fd27729c.370a78d8\", "
    // + " \"direct\":\"0\"}, \"ord_2\":{\"map\":\"null.null.e07f8c02.f9aa970a\",
    // \"direct\":\"1\"}}");
    sortFactory.getSortingNodes(setDefaultSortMap(customclassId, sortMap, structure));
  }

  private void setFilterGroups(UUID customclassId, List<UUID> idFactoryGroups,
      List<Map<String, Object>> dynamicFilters) {
    factoryGroupImplemented = new FactoryGroupImplemented();
    factoryGroupImplemented.setWorkclassId(customclassId);
    factoryGroupImplemented.setFilterGroupId(idFactoryGroups);
    factoryGroupImplemented.setDynamicFilters(dynamicFilters);
  }

  public String getLineSubquery(UUID customclassId, Map<String, Object> incomingRequestBody,
      Map<String, Object> structure, List<UUID> idFactoryGroups,
      List<Map<String, Object>> dynamicFilters, RecordsOwnerClassSettingsAbstract recordOwnerItem,
      List<Map<String, Object>> sortMap, List<Integer> limits, UUID lng) {
    // updateQueryVariables();
    // this.recordOwnerItem = recordOwnerItem;
    // this.incomingRequestBody = incomingRequestBody;
    if (limits != null)
      setLimits(limits);

    setFilterGroups(customclassId, idFactoryGroups, dynamicFilters);

    setSorting(customclassId, structure, sortMap);

    StringBuilder result = new StringBuilder("SELECT ");
    for (Map.Entry<String, Object> entryValue : structure.entrySet()) {
      if (((Map<String, Object>) entryValue.getValue())
          .get(StructrueCollectionEnum.INNER.toString()).equals("3")
          && ((Map<String, Object>) entryValue.getValue())
              .containsKey(StructrueCollectionEnum.NESTED.toString())) {
        result
            .append(getLineSubqueryRecursively(customclassId,
                UUID.fromString(((Map<String, Object>) entryValue.getValue()).get("id").toString()),
                UUID.fromString(((Map<String, Object>) entryValue.getValue())
                    .get(StructrueCollectionEnum.OBJECT.toString()).toString()),
                ((Map<String, Object>) ((Map<String, Object>) entryValue.getValue())
                    .get(StructrueCollectionEnum.NESTED.toString()))))
            .append(" as \"").append(entryValue.getKey()).append("\",");
      } else
        result.append("\"cc_").append(customclassId).append("_data_use\".\"")
            .append(((Map<String, Object>) entryValue.getValue()).get("id")).append("\" as \"")
            .append(entryValue.getKey()).append("\",");
    }

    String resultWhere = "";
    result = new StringBuilder(result.substring(0, result.length() - 1)).append(resultWhere);
    result.insert(0, "SELECT row_to_json(\"result\") FROM (").append(" FROM ")
        .append(createLineWhere(customclassId, incomingRequestBody, structure, idFactoryGroups,
            dynamicFilters, recordOwnerItem, sortMap, limits, lng))
        .append(")\"result\"");
    return result.toString();
  }

  public String getLineSubqueryRecursively(UUID parentclassId, UUID parentImplementedFieldId,
      UUID customclassId, Map<String, Object> structure) {
    StringBuilder result = new StringBuilder("SELECT ");
    for (Map.Entry<String, Object> entryValue : structure.entrySet()) {
      if (((Map<String, Object>) entryValue.getValue())
          .get(StructrueCollectionEnum.INNER.toString()).equals("3")
          && ((Map<String, Object>) entryValue.getValue())
              .containsKey(StructrueCollectionEnum.NESTED.toString())) {
        result
            .append(getLineSubqueryRecursively(customclassId,
                UUID.fromString(((Map<String, Object>) entryValue.getValue()).get("id").toString()),
                UUID.fromString(((Map<String, Object>) entryValue.getValue())
                    .get(StructrueCollectionEnum.OBJECT.toString()).toString()),
                ((Map<String, Object>) ((Map<String, Object>) entryValue.getValue())
                    .get(StructrueCollectionEnum.NESTED.toString()))))
            .append(" as \"").append(entryValue.getKey()).append("\",");
      } else
        result.append("\"cc_").append(customclassId).append("_data_use\".\"")
            .append(((Map<String, Object>) entryValue.getValue()).get("id")).append("\" as \"")
            .append(entryValue.getKey()).append("\",");
    }
    result = new StringBuilder(result.substring(0, result.length() - 1));
    String resultWhere = "";
    result.append(" FROM \"cc_").append(customclassId).append("_data_use\" join ").append("\"cc_")
        .append(customclassId).append("_implemented_records\" on \"cc_").append(customclassId)
        .append("_data_use\".\"").append(((Map<String, Object>) structure.get("0")).get("id"))
        .append("\" = \"cc_").append(customclassId).append("_implemented_records\".\"record_id\" ")
        .append(" where ").append("\"cc_").append(customclassId)
        .append("_implemented_records\".\"implemented_id\" = ").append("\"cc_")
        .append(parentclassId).append("_data_use\".\"").append(parentImplementedFieldId)
        .append("\" ").append(resultWhere).append(" order by turn");
    return result.insert(0, "(select json_agg(\"" + customclassId + "_record\") from (")
        .append(") as \"").append(customclassId).append("_record\")").toString();
  }

  private String getWhereClauseForClass(UUID customclassId,
      Map<UUID, WhereClauseDynamicClassAbstract> whereClause) {
    if (whereClause == null || !whereClause.containsKey(customclassId)
        || whereClause.get(customclassId).getWhereClause() == null) {
      return "";
    }
    return whereClause.get(customclassId).getWhereClause();
  }

  private void addValueToNotNullFields(UUID classId, String fieldNotNull) {
    if (notNullFields == null) {
      notNullFields = new HashMap<>();
    }
    ArrayList<String> fields = (ArrayList<String>) notNullFields.get(classId);
    if (fields == null) {
      fields = new ArrayList<>(2);
    }
    fields.add(fieldNotNull);
  }

  private String getStringWithNotNull(UUID classId) {
    if (notNullFields == null || notNullFields.get(classId) == null) {
      return "";
    }
    StringBuilder result = new StringBuilder(" and (");
    for (String fieldNotNull : notNullFields.get(classId)) {
      result.append(fieldNotNull).append(" is not null ");
    }
    return result.substring(0, result.length() - 13) + ")";
  }

  // private String createLineWhere(UUID customclassId, Map<String, Object> structure) {
  //
  // }

  private String createLineWhere(UUID customclassId, Map<String, Object> incomingRequestBody,
      Map<String, Object> structure, List<UUID> idFactoryGroups,
      List<Map<String, Object>> dynamicFilters, RecordsOwnerClassSettingsAbstract recordOwnerItem,
      List<Map<String, Object>> sortMap, List<Integer> limits, UUID lng) {
    // String limit = "30";
    // String resultClause = "";
    String resultWhere =
        factoryGroupImplemented.getSubqueryForCredentials(null, null, customclassId);
    resultWhere = resultWhere.equals("") ? "" : (" where " + resultWhere + " ");


    String thisWhereToken = new StringBuilder().append("(SELECT * FROM ").append("\"cc_")
        .append(customclassId).append("_data_use\" ").append(resultWhere)

        .append(")").append(" as \"cc_").append(customclassId).append("_data_use\" ").toString();


    if (checkPresentInner(structure)) {
      List<List<String>> arrayFields = new ArrayList<>(2);
      List<String> subqueryLine = new ArrayList<>(2);

      for (Map.Entry<String, Object> entryValue : structure.entrySet()) {
        if (((Map<String, Object>) entryValue.getValue())
            .get(StructrueCollectionEnum.INNER.toString()).equals("3")
            && ((Map<String, Object>) entryValue.getValue())
                .containsKey(StructrueCollectionEnum.NESTED.toString())) {

          String classMapValueID = ((Map<String, Object>) entryValue.getValue())
              .get(StructrueCollectionEnum.OBJECT.toString()).toString();
          String fieldMapValueID =
              ((Map<String, Object>) entryValue.getValue()).get("id").toString();
          System.out.println("try get subquery for transiting start: pclass: " + customclassId
              + ", pfield: " + UUID.fromString(fieldMapValueID) + ", custom: " + classMapValueID);
          String subquery = new StringBuilder().append("(SELECT ")
              .append(sortFactory.getSubqueryForTransiting(customclassId,
                  UUID.fromString(fieldMapValueID), UUID.fromString(classMapValueID)))
              // sorting was here
              .append("\"cc_").append(customclassId).append("_data_use\".\"")
              .append(((Map<String, Object>) structure.get("0")).get("id")).append("\" as \"")
              .append(customclassId).append("_").append(getTokenUUID(fieldMapValueID))
              .append("_record_id\" from \"cc_").append(customclassId).append("_data_use\" join ")
              .append(createLineWhereRecursively(customclassId, UUID.fromString(fieldMapValueID),
                  UUID.fromString(classMapValueID),
                  ((Map<String, Object>) ((Map<String, Object>) entryValue.getValue())
                      .get(StructrueCollectionEnum.NESTED.toString()))))
              .append(" on \"cc_").append(customclassId).append("_data_use\".\"")
              .append(fieldMapValueID).append("\" = \"cc_").append(classMapValueID).append("_")
              .append(getTokenUUID(fieldMapValueID)).append("_filter\".\"").append(classMapValueID)
              .append("_").append(getTokenUUID(fieldMapValueID))
              .append("_implemented_id\") AS \"cc_").append(customclassId).append("_")
              .append(getTokenUUID(fieldMapValueID)).append("_filter\"").toString();

          subqueryLine.add(subquery);
          ArrayList<String> workValues = new ArrayList<>();
          workValues.add(fieldMapValueID);
          workValues.add(customclassId.toString());
          arrayFields.add(workValues);
        }
      }
      String globalWhereString = "";

      if (subqueryLine.size() > 0) {
        String internalJoinLine = subqueryLine.get(0);
        if (subqueryLine.size() > 1) {
          for (int index = 0; index < subqueryLine.size() - 1; index++) {
            if (index > 0) {
              internalJoinLine += " as \"cc_" + arrayFields.get(index).get(1) + "_"
                  + getTokenUUID(arrayFields.get(index).get(0)) + "_filter\"";
            }
            String internalTokenJoinLine = new StringBuilder().append("(SELECT \"cc_")
                .append(arrayFields.get(index + 1).get(1)).append("_")
                .append(getTokenUUID(arrayFields.get(index + 1).get(0))).append("_filter\".\"")
                .append(arrayFields.get(index + 1).get(1)).append("_")
                .append(getTokenUUID(arrayFields.get(index + 1).get(0)))
                .append("_record_id\" FROM ").append(internalJoinLine).append(" join ")
                .append(subqueryLine.get(index + 1)).append(" on \"cc_")
                .append(arrayFields.get(index + 1).get(1)).append("_")
                .append(getTokenUUID(arrayFields.get(index + 1).get(0))).append("_filter\".\"")
                .append(arrayFields.get(index + 1).get(1)).append("_")
                .append(getTokenUUID(arrayFields.get(index + 1).get(0)))
                .append("_record_id\" = \"cc_").append(arrayFields.get(index).get(1)).append("_")
                .append(getTokenUUID(arrayFields.get(index).get(0))).append("_filter\".\"")
                .append(arrayFields.get(index).get(1)).append("_")
                .append(getTokenUUID(arrayFields.get(index).get(0))).append("_record_id\")")
                .toString();
            internalJoinLine = internalTokenJoinLine;
          }
        }
        String asSuffix = "";
        if (arrayFields.size() > 1) {
          asSuffix = "as \"cc_" + arrayFields.get(arrayFields.size() - 1).get(1) + "_"
              + getTokenUUID(arrayFields.get(arrayFields.size() - 1).get(0)) + "_filter\" ";
        }
        globalWhereString = innerLangWrapper(
            new StringBuilder().append(" (SELECT * ").append(" FROM ").append(thisWhereToken)
                .append(" LEFT JOIN ").append(internalJoinLine).append(" ").append(asSuffix)
                .append(" on \"cc_").append(customclassId).append("_data_use\".\"")
                .append(((Map<String, Object>) structure.get("0")).get("id")).append("\" = \"cc_")
                .append(arrayFields.get(arrayFields.size() - 1).get(1)).append("_")
                .append(getTokenUUID(arrayFields.get(arrayFields.size() - 1).get(0)))
                .append("_filter\".\"").append(arrayFields.get(arrayFields.size() - 1).get(1))
                .append("_").append(getTokenUUID(arrayFields.get(arrayFields.size() - 1).get(0)))
                .append("_record_id\") as \"cc_").append(customclassId).append("_data_use\"")
                .toString(),
            customclassId.toString(), ((Map<String, Object>) structure.get("0"))
                .get(StructrueCollectionEnum.ID.toString()).toString(),
            lng.toString());
      } else {
        resultWhere = resultWhere.equals("") ? "" : (" where " + resultWhere + " ");
        globalWhereString = "FROM \"cc_" + customclassId + "_data_use\" " + resultWhere;
      }
      List<String[]> workTokensForOrderBy = sortFactory.getGeneralKeyElements();

      globalWhereString = commonLangWrapper(
          addOriginalSortAndLimitString(
              new StringBuilder(globalWhereString).append(" join class_record_owner on \"cc_")
                  .append(customclassId).append("_data_use\".\"")
                  .append(recordOwnerItem.getOwnerFieldId()).append("\" = class_record_owner.id")
                  .append(getWhereSubqueryForOwnerType(recordOwnerItem,
                      UUID.fromString((String) incomingRequestBody
                          .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID))),
                      UUID.fromString((String) incomingRequestBody
                          .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID))),
                      UUID.fromString((String) incomingRequestBody
                          .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.SERVICE_ID))),
                      UUID.fromString((String) incomingRequestBody
                          .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID)))))
                  .toString(),
              new StringBuilder("\"cc_").append(customclassId).append("_data_use\"").toString(),
              startLimitValue, endLimitValue, workTokensForOrderBy,
              new StringBuilder("\"").append(((Map<String, Object>) structure.get("0")).get("id"))
                  .append("\"").toString()),
          customclassId.toString(), ((Map<String, Object>) structure.get("0"))
              .get(StructrueCollectionEnum.ID.toString()).toString(),
          lng.toString());
      globalWhereString = addBracketsForGlobalString(globalWhereString,
          new StringBuilder("\"cc_").append(customclassId).append("_data_use\"").toString(),
          workTokensForOrderBy);
      // lang

      return globalWhereString;
    } else {
      // without inner case
      String[] sortToken = sortFactory.getSubqueryForSortingWithIndex(null, null, customclassId);
      String orderByString = new StringBuilder().append(" ORDER BY \"")
          .append(((Map<String, Object>) structure.get("3")).get("id")).append("\" desc")
          .toString();
      if (sortToken != null) {
        System.out.println("sortToken 0: " + sortToken[0] + ", sortToken 1: " + sortToken[1]);
        List<String[]> workTokensForOrderBy = sortFactory.getKeyElements(null, null, customclassId);
        boolean isFirst = sortToken[1].equals("true");
        orderByString =
            isFirst ? ("ORDER BY " + createOrderByFromParams(workTokensForOrderBy)) : "";
        System.out.println("order record for dynamic query: " + orderByString);
      }

      String oneLineWhereString = "FROM \"cc_" + customclassId + "_data_use\" " + resultWhere;
      oneLineWhereString = new StringBuilder().append("(SELECT * FROM (SELECT DISTINCT ON(\"")
          .append(((Map<String, Object>) structure.get("0")).get("id"))
          .append("\") * FROM  (SELECT * ").append(oneLineWhereString).append(" ORDER BY \"")
          .append(((Map<String, Object>) structure.get("3")).get("id")).append("\" desc")
          .append(") as ").append("\"cc_").append(customclassId).append("_data_use\"")
          .append(" join class_record_owner on \"cc_").append(customclassId)
          .append("_data_use\".\"").append(recordOwnerItem.getOwnerFieldId())
          .append("\" = class_record_owner.id")
          .append(getWhereSubqueryForOwnerType(recordOwnerItem,
              UUID.fromString((String) incomingRequestBody
                  .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.CONTACT_ID))),
              UUID.fromString((String) incomingRequestBody
                  .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.COMPANY_ID))),
              UUID.fromString((String) incomingRequestBody
                  .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.SERVICE_ID))),
              UUID.fromString((String) incomingRequestBody
                  .get(FactoryGroupAbstract.getFirstPartOfUUID(DBConstants.ROLE_ID)))))
          .append(") as ").append("\"cc_").append(customclassId).append("_data_use\" ")
          .append(orderByString).append(" limit ")
          .append(getLimitString(startLimitValue, endLimitValue)).append(") as ").append("\"cc_")
          .append(customclassId).append("_data_use\"").toString();
      return oneLineWhereString;
    }
  }

  private String innerLangWrapper(String filterSubquery, String classId, String recordIdFieldId,
      String lngId) {
    return new StringBuilder().append("(SELECT \"cc_").append(classId).append("_data_use\".* FROM ")
        .append(filterSubquery).append(" LEFT JOIN \"").append(DBConstants.LNG_RECORD_ACCESS)
        .append("\" on \"").append(DBConstants.LNG_RECORD_ACCESS)
        .append("\".\"record_id\" = \"cc_").append(classId).append("_data_use\".\"")
        .append(recordIdFieldId).append("\" and \"").append(DBConstants.LNG_RECORD_ACCESS)
        .append("\".\"lng_id\" = '").append(lngId).append("' where \"")
        .append(DBConstants.LNG_RECORD_ACCESS).append("\".\"access\" IS NULL or \"")
        .append(DBConstants.LNG_RECORD_ACCESS).append("\".\"access\" = true")
        .append(") as \"cc_").append(classId).append("_data_use\"").toString();
  }

  private String commonLangWrapper(String filterSubquery, String classId, String recordIdFieldId,
      String lngId) {
    return new StringBuilder().append("(SELECT \"cc_").append(classId).append("_data_use\".* FROM ")
        .append(filterSubquery).append(" LEFT JOIN \"").append(DBConstants.LNG_RECORD_ACCESS)
        .append("\" on \"").append(DBConstants.LNG_RECORD_ACCESS)
        .append("\".\"record_id\" = \"cc_").append(classId).append("_data_use\".\"")
        .append(recordIdFieldId).append("\" and \"").append(DBConstants.LNG_RECORD_ACCESS)
        .append("\".\"lng_id\" = '").append(lngId).append("' where \"")
        .append(DBConstants.LNG_RECORD_ACCESS).append("\".\"access\" IS NULL or \"")
        .append(DBConstants.LNG_RECORD_ACCESS).append("\".\"access\" = true")
        .append(") as \"cc_").append(classId).append("_data_use\"").toString();
  }

  public String createLineWhereRecursively(UUID parentclassId, UUID parentImplementedFieldId,
      UUID customclassId, Map<String, Object> localStructure) {
    String resultWhere = factoryGroupImplemented.getSubqueryForCredentials(parentclassId,
        parentImplementedFieldId, customclassId);
    resultWhere = resultWhere.equals("") ? "" : (" where (" + resultWhere + ")");
    String[] sortToken = sortFactory.getSubqueryForSortingWithIndex(parentclassId,
        parentImplementedFieldId, customclassId);
    List<String[]> workTokensForOrderBy =
        sortFactory.getKeyElements(parentclassId, parentImplementedFieldId, customclassId);
    boolean isFirst = sortToken[1].equals("true");
    String orderByString =
        isFirst ? ("ORDER BY " + createOrderByFromParams(workTokensForOrderBy)) : "";

    String thisWhereToken = new StringBuilder().append("(SELECT \"cc_").append(customclassId)
        .append("_implemented_records\".implemented_id as \"").append(customclassId).append("_")
        .append(getTokenUUID(parentImplementedFieldId)).append("_implemented_id\", ")

        .append(sortToken[0])

        .append("\"cc_").append(customclassId).append("_data_use\".* FROM ").append("\"cc_")
        .append(customclassId).append("_data_use\" join \"cc_").append(customclassId)
        .append("_implemented_records\" on ").append("\"cc_").append(customclassId)
        .append("_data_use\".\"").append(((Map<String, Object>) localStructure.get("0")).get("id"))
        .append("\" = ").append("\"cc_").append(customclassId)
        .append("_implemented_records\".\"record_id\" ").append(resultWhere).append(" ")
        .append(orderByString).append(") as \"cc_").append(customclassId).append("_")
        .append(getTokenUUID(parentImplementedFieldId)).append("_filter\"").toString();
    if (isFirst)
      thisWhereToken = addOriginalSortAndLimitString(thisWhereToken,
          new StringBuilder("\"cc_").append(customclassId).append("_")
              .append(getTokenUUID(parentImplementedFieldId)).append("_filter\"").toString(),
          startLimitValue, endLimitValue, workTokensForOrderBy,
          new StringBuilder("\"").append(customclassId).append("_")
              .append(getTokenUUID(parentImplementedFieldId)).append("_implemented_id\"")
              .toString());
    List<List<String>> arrayFields = new ArrayList<>(2);
    List<String> subqueryLine = new ArrayList<>(2);
    for (Map.Entry<String, Object> entryValue : localStructure.entrySet()) {

      if (((Map<String, Object>) entryValue.getValue())
          .get(StructrueCollectionEnum.INNER.toString()).equals("3")
          && ((Map<String, Object>) entryValue.getValue())
              .containsKey(StructrueCollectionEnum.NESTED.toString())) {
        System.out.println("try get subquery for transiting: pclass: " + parentclassId
            + ", pfield: " + parentImplementedFieldId + ", custom: " + customclassId);
        String classMapValueID = ((Map<String, Object>) entryValue.getValue())
            .get(StructrueCollectionEnum.OBJECT.toString()).toString();
        String fieldMapValueID = ((Map<String, Object>) entryValue.getValue()).get("id").toString();
        String resultSubquery = new StringBuilder().append("(SELECT ")
            .append(sortFactory.getSubqueryForTransiting(customclassId,
                UUID.fromString(fieldMapValueID), UUID.fromString(classMapValueID)))

            .append("\"cc_").append(customclassId).append("_data_use\".\"")
            .append(((Map<String, Object>) localStructure.get("0")).get("id")).append("\" as \"")
            .append(customclassId).append("_").append(getTokenUUID(fieldMapValueID))
            .append("_record_id\"").append(" from \"cc_").append(customclassId)
            .append("_data_use\" join ")
            .append(createLineWhereRecursively(customclassId, UUID.fromString(fieldMapValueID),
                UUID.fromString(classMapValueID),
                ((Map<String, Object>) ((Map<String, Object>) entryValue.getValue())
                    .get(StructrueCollectionEnum.NESTED.toString()))))
            .append(" on \"cc_").append(customclassId).append("_data_use\".\"")
            .append(fieldMapValueID).append("\" = \"cc_").append(classMapValueID).append("_")
            .append(getTokenUUID(fieldMapValueID)).append("_filter\".\"").append(classMapValueID)
            .append("_").append(getTokenUUID(fieldMapValueID)).append("_implemented_id\") AS \"cc_")
            .append(customclassId).append("_").append(getTokenUUID(fieldMapValueID))
            .append("_filter\"").toString();
        subqueryLine.add(resultSubquery);
        ArrayList<String> workValues = new ArrayList<>();
        workValues.add(fieldMapValueID);
        workValues.add(customclassId.toString());
        arrayFields.add(workValues);
      }
    }

    if (subqueryLine.size() > 0) {
      String internalJoinLine = subqueryLine.get(0);
      if (subqueryLine.size() > 1) {
        for (int index = 0; index < subqueryLine.size() - 1; index++) {
          if (index > 0) {
            internalJoinLine += " as \"cc_" + arrayFields.get(index).get(1) + "_"
                + getTokenUUID(arrayFields.get(index).get(0)) + "_filter\"";
          }
          String internalTokenJoinLine = new StringBuilder().append("(SELECT \"cc_")
              .append(arrayFields.get(index + 1).get(1)).append("_")
              .append(getTokenUUID(arrayFields.get(index + 1).get(0))).append("_filter\".\"")
              .append(arrayFields.get(index + 1).get(1)).append("_")
              .append(getTokenUUID(arrayFields.get(index + 1).get(0))).append("_record_id\" FROM ")
              .append(internalJoinLine).append(" join ").append(subqueryLine.get(index + 1))
              .append(" on \"cc_").append(arrayFields.get(index + 1).get(1)).append("_")
              .append(getTokenUUID(arrayFields.get(index + 1).get(0))).append("_filter\".\"")
              .append(arrayFields.get(index + 1).get(1)).append("_")
              .append(getTokenUUID(arrayFields.get(index + 1).get(0)))
              .append("_record_id\" = \"cc_").append(arrayFields.get(index).get(1)).append("_")
              .append(getTokenUUID(arrayFields.get(index).get(0))).append("_filter\".\"")
              .append(arrayFields.get(index).get(1)).append("_")
              .append(getTokenUUID(arrayFields.get(index).get(0))).append("_record_id\")")
              .toString();
          internalJoinLine = internalTokenJoinLine;
        }
      }
      String asSuffix = "";
      if (arrayFields.size() > 1) {
        asSuffix = "as \"cc_" + arrayFields.get(arrayFields.size() - 1).get(1) + "_"
            + getTokenUUID(arrayFields.get(arrayFields.size() - 1).get(0)) + "_filter\" ";
      }
      return new StringBuilder().append("(SELECT * FROM ").append(thisWhereToken).append(" join ")
          .append(internalJoinLine).append(" ").append(asSuffix).append(" on \"cc_")
          .append(customclassId).append("_").append(getTokenUUID(parentImplementedFieldId))
          .append("_filter\".\"").append(((Map<String, Object>) localStructure.get("0")).get("id"))
          .append("\" = \"cc_").append(arrayFields.get(arrayFields.size() - 1).get(1)).append("_")
          .append(getTokenUUID(arrayFields.get(arrayFields.size() - 1).get(0)))
          .append("_filter\".\"").append(arrayFields.get(arrayFields.size() - 1).get(1)).append("_")
          .append(getTokenUUID(arrayFields.get(arrayFields.size() - 1).get(0)))
          .append("_record_id\") as \"cc_").append(customclassId).append("_")
          .append(getTokenUUID(parentImplementedFieldId)).append("_filter\"").toString();
    }

    return thisWhereToken;
  }

  private class CustomWhereClause {
    private String whereClauseValue;

    CustomWhereClause() {
      whereClauseValue = "";
    }

    protected void setWhereClause(String addWhere) {
      whereClauseValue += addWhere;
    }

    protected String getWhereClause() {
      return whereClauseValue;
    }
  }

  private String getTokenUUID(String UUIDValue) {
    if (UUIDValue != null && UUIDValue.length() > 8) {
      return UUIDValue.substring(0, 8);
    }
    return null;
  }

  private String getTokenUUID(UUID UUIDValue) {
    if (UUIDValue != null && UUIDValue.toString().length() > 8) {
      return UUIDValue.toString().substring(0, 8);
    }
    return null;
  }

  private String addOriginalSortAndLimitString(String originalQuery, String resultTableName,
      int startLimit, int endLimit, List<String[]> keyStrings, String implementedIDSubquery) {
    return new StringBuilder(originalQuery.length() + (resultTableName.length() * 3) + 150)
        .append("(SELECT * FROM (SELECT DISTINCT ON(").append(implementedIDSubquery)
        .append(") * FROM ").append(originalQuery).append(") as ").append(resultTableName)
        .append(" ORDER BY ").append(createOrderByFromParams(keyStrings)).append(" limit ")
        .append(getLimitString(startLimit, endLimit)).append(") as ").append(resultTableName)
        .toString();
  }

  private String addBracketsForGlobalString(String originalQuery, String resultTableName,
      List<String[]> keyStrings) {
    return new StringBuilder(originalQuery.length() + (resultTableName.length() * 1) + 150)
        .append("(SELECT * FROM ").append(originalQuery).append(") as ").append(resultTableName)
        .toString();
  }

  private String createOrderByFromParams(List<String[]> mainSortingsParams) {
    return mainSortingsParams.stream().map(
        e -> e[2].startsWith("null") ? ("\"" + e[1] + "\" " + e[3]) : ("\"" + e[2] + "\" " + e[3]))
        .collect(Collectors.joining(", "));
  }

  private String getLimitString(int startLimit, int endLimit) {
    int resultLimit = endLimit;
    if (resultLimit < 0)
      resultLimit = MAX_LIMIT;
    if (startLimit > 0 && startLimit < endLimit) {
      return startLimit + ", " + endLimit;
    }
    return Integer.toString(endLimit);
  }

  private String getWhereSubqueryForOwnerType(RecordsOwnerClassSettingsAbstract ownSettings,
      UUID contactId, UUID companyId, UUID serviceId, UUID roleId) {
    switch (ownSettings.getTypeRecordAccess()) {
      case 0:
        return " where " + DBConstants.TBL_RECORD_OWNER + ".\"" + DBConstants.CONTACT_ID + "\" = '"
            + contactId + "'";
      case 2:
        return new StringBuilder().append(" where ").append(DBConstants.TBL_RECORD_OWNER)
            .append(".\"").append(DBConstants.COMPANY_ID).append("\" = '").append(companyId)
            .append("' and ").append(DBConstants.TBL_RECORD_OWNER).append(".\"")
            .append(DBConstants.SERVICE_ID).append("\" = '").append(serviceId).append("' and ")
            .append(DBConstants.TBL_RECORD_OWNER).append(".\"").append(DBConstants.ROLE_ID)
            .append("\" = '").append(roleId).append("'").toString();
      case 3:
        return new StringBuilder().append(" where ").append(DBConstants.TBL_RECORD_OWNER)
            .append(".\"").append(DBConstants.COMPANY_ID).append("\" = '").append(companyId)
            .append("' and ").append(DBConstants.TBL_RECORD_OWNER).append(".\"")
            .append(DBConstants.SERVICE_ID).append("\" = '").append(serviceId).append("'")
            .toString();
      case 4:
      default:
        return new StringBuilder().append(" where ").append(DBConstants.TBL_RECORD_OWNER)
            .append(".\"").append(DBConstants.COMPANY_ID).append("\" = '").append(companyId)
            .append("'").toString();
      case 5:
        return new StringBuilder().append(" where ").append(DBConstants.TBL_RECORD_OWNER)
            .append(".\"").append(DBConstants.ROLE_ID).append("\" = '").append(DBConstants.ADMIN_ID)
            .append("'").toString();
      case 6:
        return new StringBuilder().append(" where ").append(DBConstants.TBL_RECORD_OWNER)
            .append(".\"").append(DBConstants.ROLE_ID).append("\" = '")
            .append(DBConstants.SUPER_ADMIN_ID).append("'").toString();
    }

  }

  private boolean checkPresentInner(Map<String, Object> fastStructure) {
    return fastStructure.entrySet().parallelStream()
        .filter(x -> ((Map<String, Object>) x.getValue())
            .get(StructrueCollectionEnum.INNER.toString()).equals("3")
            && ((Map<String, Object>) x.getValue())
                .containsKey(StructrueCollectionEnum.NESTED.toString()))
        .findAny().orElse(null) != null;
  }

}
