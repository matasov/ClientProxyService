package com.matas.liteconstruct.db.models.collections.abstractmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import com.matas.liteconstruct.db.models.classes.abstractmodel.CustomerClassModelAbstract;
import com.matas.liteconstruct.db.models.classes.repos.CustomerClassRepository;
import com.matas.liteconstruct.db.models.collectioncase.abstractmodel.CollectionCaseAbstract;
import com.matas.liteconstruct.db.models.collectiondynamicrole.abstractmodel.CollectionDynamicRoleModelAbstract;
import com.matas.liteconstruct.db.models.collectiondynamicrole.repos.CollectionDynamicRoleRepository;
import com.matas.liteconstruct.db.models.collections.chain.StructureCollectionByClassIdChain;
import com.matas.liteconstruct.db.models.collections.chain.StructureCollectionByRecordIdChain;
import com.matas.liteconstruct.db.models.collections.field.FieldListImplementation;
import com.matas.liteconstruct.db.models.collections.field.StructureCollectionsField;
import com.matas.liteconstruct.db.models.collections.model.StructureCollectionNode;
import com.matas.liteconstruct.db.models.collections.queryfactory.SimpleStructureCollectionFactoryImplements;
import com.matas.liteconstruct.db.models.collections.repos.StructureCollectionsFieldsRepository;
import com.matas.liteconstruct.db.models.structure.conditions.StructureCondition;
import com.matas.liteconstruct.db.tools.chain.AbstractChain;
import com.matas.liteconstruct.db.tools.conditions.AbstractCondition;
import com.matas.liteconstruct.db.tools.field.FieldList;
import com.matas.liteconstruct.db.tools.sqlselectfactory.AbstractFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class StructureCollectionTreeFactoryAbstract {

  protected StructureCollectionsFieldsRepository structureCollectionSqlRep;

  protected CustomerClassRepository customerClassRepository;

  protected CollectionDynamicRoleRepository collectionDynamicRoleRepository;

  protected TreeMap<UUID, StructureCollectionNodeFieldsAbstract> structureCollectionFields;

  public ArrayList<StructureCollectionNodeFieldsAbstract> createTree(UUID dynamicRoleId,
      UUID startclassId, UUID startCollectionId, CollectionCaseAbstract collectionCase) {
    FieldList fieldsList = new FieldListImplementation();
    fieldsList.addNewField(new StructureCollectionsField("\"class_collections_fields_use\".\"id\""))
        .addNewField(new StructureCollectionsField("\"class_collections_fields_use\".\"class_id\""))
        .addNewField(new StructureCollectionsField("\"class_collections_fields_use\".\"field\""))
        .addNewField(new StructureCollectionsField("\"class_collections_fields_use\".\"turn\""))
        .addNewField(new StructureCollectionsField("\"class_collections_fields_use\".\"usef\""))
        .addNewField(new StructureCollectionsField("\"class_collections_fields_use\".\"visible\""))
        .addNewField(new StructureCollectionsField("\"class_collections_fields_use\".\"edit\""))
        .addNewField(new StructureCollectionsField("\"class_collections_fields_use\".\"delete\""))
        .addNewField(new StructureCollectionsField("\"class_collections_fields_use\".\"insert\""))
        .addNewField(new StructureCollectionsField("\"class_structure_fields\".\"name\""))
        .addNewField(new StructureCollectionsField("\"class_structure_fields\".\"field_class\""))
        .addNewField(new StructureCollectionsField("\"class_structure_fields\".\"inner\""))
        .addNewField(new StructureCollectionsField("\"class_structure_fields\".\"show_name\""));

    AbstractCondition useful = new StructureCondition().addCondition(
        com.matas.liteconstruct.db.models.structure.conditions.ConditionsConstants.STR_COLLECTION_CLASS_ID,
        startclassId).addCondition(
            com.matas.liteconstruct.db.models.structure.conditions.ConditionsConstants.STR_COLLECTION_RECORD_ID,
            startCollectionId);
    AbstractFactory queryFactory = new SimpleStructureCollectionFactoryImplements();
    queryFactory.setFields(fieldsList);
    queryFactory
        .setObjectString(" \"class_collections_fields_use\" join \"class_structure_fields\" on "
            + "\"class_structure_fields\".\"id\" = \"class_collections_fields_use\".\"field\"");
    List<AbstractChain> chains = new ArrayList<AbstractChain>();
    chains.add(new StructureCollectionByClassIdChain());
    chains.add(new StructureCollectionByRecordIdChain());
    queryFactory.setWhere(chains, useful);
    TreeMap<String, Boolean> orderBy = new TreeMap<>();
    orderBy.put("\"class_collections_fields_use\".\"turn\"", true);
    queryFactory.setOrderBy(orderBy);
    return getLine(null, useful, queryFactory, chains, dynamicRoleId, collectionCase);
  }

  @SuppressWarnings("finally")
  public ArrayList<StructureCollectionNodeFieldsAbstract> getLine(
      StructureCollectionNodeFieldsAbstract parent, AbstractCondition useful,
      AbstractFactory queryFactory, List<AbstractChain> chains, UUID dynamicRoleId,
      CollectionCaseAbstract collectionCase) {
    ArrayList<StructureCollectionNodeFieldsAbstract> result = new ArrayList<>(20);
    try {
      queryFactory.setWhere(chains, useful);
      List<StructureCollectionAbstract> output =
          structureCollectionSqlRep.queryByStructure(queryFactory);
      for (StructureCollectionAbstract entry : output) {
        StructureCollectionNode currentNode = new StructureCollectionNode(parent, entry);
        result.add(currentNode);
        if (entry.getStructureField().getInnerType() == 3) {
          List<CollectionDynamicRoleModelAbstract> collections =
              collectionDynamicRoleRepository.getCollectionDynamicRoleByDroleClass(dynamicRoleId,
                  currentNode.getCurrentData().getStructureField().getDataClass(), 1);
          if (collections.size() > 0) {
            useful = new StructureCondition().addCondition(
                com.matas.liteconstruct.db.models.structure.conditions.ConditionsConstants.STR_COLLECTION_CLASS_ID,
                currentNode.getCurrentData().getStructureField().getDataClass()).addCondition(
                    com.matas.liteconstruct.db.models.structure.conditions.ConditionsConstants.STR_COLLECTION_RECORD_ID,
                    collections.get(0).getCollectionId());
            ArrayList<StructureCollectionNodeFieldsAbstract> values =
                getLine(currentNode, useful, queryFactory, chains, dynamicRoleId, collectionCase);
            currentNode.addChilds(values);
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      return result;
    }
  }

  public String getJsonQuery(ArrayList<StructureCollectionNodeFieldsAbstract> values) {

    try {
      Map<UUID, CustomerClassModelAbstract> classesList =
          customerClassRepository.listByType(-1, -1);
      return getJsonRecursively(values, classesList);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  protected String getJsonRecursively(ArrayList<StructureCollectionNodeFieldsAbstract> values,
      Map<UUID, CustomerClassModelAbstract> classesList) {
    try {
      StringBuilder resultBuilder = new StringBuilder("");
      for (StructureCollectionNodeFieldsAbstract entry : values) {
        StringBuilder nestedString = new StringBuilder("");
        if (entry.getCurrentData().getStructureField().getInnerType() == 3) {
          nestedString.append(",\"nested\":")
              .append(getJsonRecursively(entry.getChilds(), classesList));
        }
        resultBuilder.append("\"").append(entry.getCurrentData().getTurn()).append("\":{\"id\":\"")
            .append(entry.getCurrentData().getFieldId()).append("\", \"name\":\"")
            .append(entry.getCurrentData().getStructureField().getFieldName())
            .append("\", \"perm\":\"")
            .append(com.matas.liteconstruct.db.tools.permissions.PermissionHandler
                .getStringPermission(entry.getCurrentData().isUseful(),
                    entry.getCurrentData().isVisible(), entry.getCurrentData().isEdit(),
                    entry.getCurrentData().isDelete(), entry.getCurrentData().isInsert()))
            .append("\", \"type\":\"")
            .append(classesList.get(entry.getCurrentData().getStructureField().getDataClass())
                .getName())
            .append("\", \"inner\":\"")
            .append(entry.getCurrentData().getStructureField().getInnerType())
            .append("\", \"object\":\"")
            .append(entry.getCurrentData().getStructureField().getDataClass())
            .append("\", \"title\":\"")
            .append(entry.getCurrentData().getStructureField().getFieldShowName()).append("\"")
            .append(nestedString.toString()).append("},");
      }
      if (resultBuilder.length() == 0)
        return "{}";
      else {
        resultBuilder.insert(0, "{");
        return resultBuilder.substring(0, resultBuilder.length() - 1) + "}";

      }
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
}
