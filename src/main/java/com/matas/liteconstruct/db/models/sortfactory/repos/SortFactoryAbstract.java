package com.matas.liteconstruct.db.models.sortfactory.repos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matas.liteconstruct.db.CommonMethods;
import com.matas.liteconstruct.db.models.sortfactory.abstractmodel.SortingNodeAbstract;
import com.matas.liteconstruct.db.models.sortfactory.model.SortingNode;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;
import com.matas.liteconstruct.db.tools.StructrueCollectionEnum;
import com.matas.liteconstruct.service.signup.SignupServiceHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SortFactoryAbstract {

  protected UUID masterClass;

  protected Map<String, Object> structure;

  protected List<Map<String, Object>> sortMap;

  protected List<SortingNodeAbstract> sortingNodes;

  public void setMasterClass(UUID masterClass) {
    this.masterClass = masterClass;
  }

  public void setStructureMap(Map<String, Object> structure) {
    this.structure = structure;
  }

  /*
   * example sorting map: [{"map":"fd27729c.e0cfbed0.5cb705ea.d68f2806", "direct":"0"},
   * {"map":"e07f8c02.f9aa970a.fd27729c.370a78d8", "direct":"0"},
   * {"map":"null.null.e07f8c02.f9aa970a", "direct":"1"}]
   */
  public void setSortingMap(String sortingMap) {
    if (sortingMap != null) {
      ObjectMapper objectMapper = new ObjectMapper();
      try {
        sortMap = objectMapper.readValue(sortingMap, new TypeReference<List<Object>>() {});
        System.out.println("sortMap: " + sortMap);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    if (structure != null && masterClass != null) {
      // sortMap.entrySet().stream().forEach(e->{((Map<String,
      // Object>)e.getValue()).get("map")});
      sortingNodes = getSortingNodes(sortMap);
    }
  }

  public List<SortingNodeAbstract> getSortingNodes(List<Map<String, Object>> sortingMap) {
    log.info("getSortingNodes: {}", sortingMap);
    if (sortingMap != null && !sortingMap.isEmpty() && sortingMap.get(0) != null) {
      List<SortingNodeAbstract> result = new ArrayList<>(3);
      sortingMap.forEach(sortElement -> {
        List<List<UUID>> path =
            createPathForNode((String) ((Map<String, Object>) sortElement).get("map"));
        System.out.println("new path: " + path);
        if (path != null) {
          List<UUID> lastPathNode = path.get(path.size() - 1);
          int direction = 0;
          try {
            direction =
                Integer.parseInt((String) ((Map<String, Object>) sortElement).get("direct"));
          } catch (NumberFormatException e) {
            e.printStackTrace();
          }
          result.add(new SortingNode(lastPathNode.get(0), lastPathNode.get(1), lastPathNode.get(2),
              lastPathNode.get(3), path, direction));
        }
      });
      // for (Map.Entry<String, Object> sortElement : sortingMap.entrySet()) {
      // // ((Map<String, Object>) sortElement.getValue()).get("map");
      // List<List<UUID>> path =
      // createPathForNode((String) ((Map<String, Object>) sortElement.getValue()).get("map"));
      // System.out.println("new path: " + path);
      // if (path != null) {
      // List<UUID> lastPathNode = path.get(path.size() - 1);
      // int direction = 0;
      // try {
      // direction = Integer
      // .parseInt((String) ((Map<String, Object>) sortElement.getValue()).get("direct"));
      // } catch (NumberFormatException e) {
      // e.printStackTrace();
      // }
      // result.add(new SortingNode(lastPathNode.get(0), lastPathNode.get(1), lastPathNode.get(2),
      // lastPathNode.get(3), path, direction));
      // }
      // }
      sortingNodes = result;
      return result;
    }
    return null;
  }

  public List<List<UUID>> createPathForNode(String valueMap) {
    System.out
        .println("sortfactory. for class: " + masterClass + ", start work with map: " + valueMap);
    if (valueMap == null)
      return null;
    String[] tokenList = valueMap.split("\\.");
    List<List<UUID>> result = new ArrayList<>(2);
    String parentClass = tokenList[0];
    String parentField = tokenList[1];
    String currentClass = tokenList[2];
    String currentField = tokenList[3];
    System.out.println("sortfactory. parent class: " + parentClass);
    if (parentClass.equals("null")) {
      // for start class
      System.out.println("sortfactory. work with main class");
      if (FactoryGroupAbstract.getFirstPartOfUUID(masterClass).equals(currentClass)) {
        List<UUID> temp = new ArrayList<>(4);
        temp.add(masterClass);
        for (Map.Entry<String, Object> currentElement : structure.entrySet()) {
          if (((String) ((Map<String, Object>) currentElement.getValue()).get("id"))
              .startsWith(currentField)) {
            temp.add(CommonMethods
                .getUUID(((String) ((Map<String, Object>) currentElement.getValue()).get("id"))));
            break;
          }
        }
        temp.add(CommonMethods.getUUID("null"));
        temp.add(CommonMethods.getUUID("null"));
        return new ArrayList<List<UUID>>(1) {
          {
            add(temp);
          }
        };
      }
    } else {
      // for current
      String currentClassFilled = getValueFromStructureMap(StructrueCollectionEnum.OBJECT.toString(), currentClass, structure);
      String currentFieldFilled = getValueFromStructureMap("id", currentField, structure);
      // for parent
      String tempParentClass = getValueFromStructureMap(StructrueCollectionEnum.OBJECT.toString(), parentClass, structure);
      if (tempParentClass == null || tempParentClass.equals("null"))
        tempParentClass = this.masterClass.toString();
      String parentClassFilled = tempParentClass;
      String parentFieldFilled = getValueFromStructureMap("id", parentField, structure);

      result.add(new ArrayList<UUID>(4) {
        {
          add(CommonMethods.getUUID(currentClassFilled));
          add(CommonMethods.getUUID(currentFieldFilled));
          add(CommonMethods.getUUID(parentClassFilled));
          add(CommonMethods.getUUID(parentFieldFilled));
        }
      });
      List<String> nextSearchValue =
          getParent(currentClass, currentField, parentClass, parentField, structure);
      // System.out.println("found: " + nextSearchValue);
      while (nextSearchValue != null) {

        if (nextSearchValue != null) {
          final String currentParentclassId =
              nextSearchValue.get(0) == null || nextSearchValue.get(0).equals("null")
                  ? masterClass.toString()
                  : nextSearchValue.get(0);

          final String currentParentFieldId = nextSearchValue.get(1);
          result.add(0, new ArrayList<UUID>(4) {
            {
              add(result.get(0).get(2));
              add(result.get(0).get(3));
              add(CommonMethods.getUUID(currentParentclassId));
              add(CommonMethods.getUUID(currentParentFieldId));
            }
          });
        }
        if (result.get(0).get(2) == null)
          nextSearchValue = null;
        else
          nextSearchValue =
              getParent(result.get(0).get(0).toString(), result.get(0).get(1).toString(),
                  result.get(0).get(2).toString(), result.get(0).get(3).toString(), structure);
      }
    }
    return result;
  }

  // public List<String> getParentDown(String searchedValue, String parentclassId, String
  // parentFieldId,
  // String currentclassId, Map<String, Object> structureValue) {
  // for (Map.Entry<String, Object> currentElement : structure.entrySet()) {
  // if (((String) ((Map<String, Object>)
  // currentElement.getValue()).get(StructrueCollectionEnum.OBJECT.toString())).startsWith(searchedValue)) {
  // return new ArrayList<String>() {
  // {
  // add(parentclassId);
  // add(parentFieldId);
  // }
  // };
  // }
  // if (((Map<String, Object>) currentElement.getValue()).containsKey(StructrueCollectionEnum.NESTED.toString())
  // && ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.NESTED.toString()) != null) {
  // List<String> result = getParentDown(searchedValue, currentclassId,
  // (String) ((Map<String, Object>) currentElement.getValue()).get("id"),
  // (String) ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.OBJECT.toString()),
  // (Map<String, Object>) ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.NESTED.toString()));
  // if (result != null)
  // return result;
  // }
  // }
  // return null;
  // }
  //
  // public List<String> getParentUp(String searchedValue, String parentclassId, String
  // parentFieldId,
  // String currentclassId, Map<String, Object> structureValue) {
  // for (Map.Entry<String, Object> currentElement : structureValue.entrySet()) {
  // if (((String) ((Map<String, Object>)
  // currentElement.getValue()).get(StructrueCollectionEnum.OBJECT.toString())).startsWith(searchedValue)) {
  // return new ArrayList<String>() {
  // {
  // add(parentclassId);
  // add(parentFieldId);
  // }
  // };
  // }
  // if (((Map<String, Object>) currentElement.getValue()).containsKey(StructrueCollectionEnum.NESTED.toString())
  // && ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.NESTED.toString()) != null) {
  // List<String> result = getParentUp(searchedValue, currentclassId,
  // (String) ((Map<String, Object>) currentElement.getValue()).get("id"),
  // (String) ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.OBJECT.toString()),
  // (Map<String, Object>) ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.NESTED.toString()));
  // if (result != null)
  // return result;
  // }
  // }
  // return null;
  // }

  public List<String> getParent(String currentClass, String currentField, String parentclassId,
      String parentFieldId, Map<String, Object> structureValue) {
    if (isPresentObject(parentclassId, parentFieldId, null, structureValue)) {
      // System.out.println("almoust working..");
      for (Map.Entry<String, Object> nestedElement : structureValue.entrySet()) {
        if (((String) ((Map<String, Object>) nestedElement.getValue()).get(StructrueCollectionEnum.OBJECT.toString())).startsWith(
            parentclassId) && ((Map<String, Object>) nestedElement.getValue()).containsKey(StructrueCollectionEnum.NESTED.toString())
            && ((Map<String, Object>) nestedElement.getValue()).get(StructrueCollectionEnum.NESTED.toString()) != null) {
          // System.out.println("find inherit: ");
          if (isPresentObject(currentClass, currentField, parentFieldId,
              (Map<String, Object>) ((Map<String, Object>) nestedElement.getValue())
                  .get(StructrueCollectionEnum.NESTED.toString()))) {
            return new ArrayList<String>() {
              {
                add("null");
                add((String) ((Map<String, Object>) nestedElement.getValue()).get("id"));
              }
            };
          }
        }
      }
    }
    for (Map.Entry<String, Object> currentElement : structureValue.entrySet()) {
      if (((Map<String, Object>) currentElement.getValue()).containsKey(StructrueCollectionEnum.NESTED.toString())
          && ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.NESTED.toString()) != null) {
        // System.out.println("find parent: ");
        if (isPresentObject(parentclassId, parentFieldId, null,
            (Map<String, Object>) ((Map<String, Object>) currentElement.getValue())
                .get(StructrueCollectionEnum.NESTED.toString()))) {
          // System.out.println("find next level");
          for (Map.Entry<String, Object> nestedElement : ((Map<String, Object>) ((Map<String, Object>) currentElement
              .getValue()).get(StructrueCollectionEnum.NESTED.toString())).entrySet()) {
            if (((String) ((Map<String, Object>) nestedElement.getValue()).get(StructrueCollectionEnum.OBJECT.toString()))
                .startsWith(parentclassId)
                && ((Map<String, Object>) nestedElement.getValue()).containsKey(StructrueCollectionEnum.NESTED.toString())
                && ((Map<String, Object>) nestedElement.getValue()).get(StructrueCollectionEnum.NESTED.toString()) != null) {
              // System.out.println("find inherit: ");
              if (isPresentObject(currentClass, currentField, parentFieldId,
                  (Map<String, Object>) ((Map<String, Object>) nestedElement.getValue())
                      .get(StructrueCollectionEnum.NESTED.toString()))) {
                return new ArrayList<String>() {
                  {
                    add((String) ((Map<String, Object>) (Map<String, Object>) currentElement
                        .getValue()).get(StructrueCollectionEnum.OBJECT.toString()));
                    add((String) ((Map<String, Object>) nestedElement.getValue()).get("id"));
                  }
                };
              }
            }
          }
        }
        List<String> result = getParent(currentClass, currentField, parentclassId, parentFieldId,
            (Map<String, Object>) ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.NESTED.toString()));
        if (result != null)
          return result;

      }

    }
    return null;
  }

  public boolean isPresentObject(String searchedClass, String searchedField, String parentField,
      Map<String, Object> structureValue) {
    // System.out.println("try find for: " + searchedClass);
    if (structureValue != null)
      for (Map.Entry<String, Object> currentElement : structureValue.entrySet()) {
        // System.out.println("compaire with: "
        // + ((String) ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.OBJECT.toString())) + " ["
        // + (((String) ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.OBJECT.toString()))
        // .startsWith(searchedClass)
        // && (parentField == null
        // || ((String) ((Map<String, Object>) currentElement.getValue()).get("id"))
        // .startsWith(parentField))
        // && ((Map<String, Object>) currentElement.getValue()).containsKey(StructrueCollectionEnum.NESTED.toString())
        // && ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.NESTED.toString()) != null)
        // + "]");
        if (((String) ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.OBJECT.toString()))
            .startsWith(searchedClass)
            && (parentField == null
                || ((String) ((Map<String, Object>) currentElement.getValue()).get("id"))
                    .startsWith(parentField))
            && ((Map<String, Object>) currentElement.getValue()).containsKey(StructrueCollectionEnum.NESTED.toString())
            && ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.NESTED.toString()) != null) {
          for (Map.Entry<String, Object> nestedStructureElement : ((Map<String, Object>) ((Map<String, Object>) currentElement
              .getValue()).get(StructrueCollectionEnum.NESTED.toString())).entrySet()) {
            if (((String) ((Map<String, Object>) nestedStructureElement.getValue()).get("id"))
                .startsWith(searchedField))
              return true;
          }
        }
      }
    return false;
  }

  public String getValueFromStructureMap(String key, String searchedValue,
      Map<String, Object> structureValue) {
    for (Map.Entry<String, Object> currentElement : structureValue.entrySet()) {
      if (((String) ((Map<String, Object>) currentElement.getValue()).get(key))
          .startsWith(searchedValue)) {
        return ((String) ((Map<String, Object>) currentElement.getValue()).get(key));
      }
      if (((Map<String, Object>) currentElement.getValue()).containsKey(StructrueCollectionEnum.NESTED.toString())
          && ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.NESTED.toString()) != null) {
        String result = getValueFromStructureMap(key, searchedValue,
            (Map<String, Object>) ((Map<String, Object>) currentElement.getValue()).get(StructrueCollectionEnum.NESTED.toString()));
        if (result != null)
          return result;
      }
    }
    return null;
  }

  public List<String> getFieldForSorting(UUID parentclassId, UUID parentFieldId, UUID classId) {
    System.out.println(
        "getFieldForSorting(" + parentclassId + ", " + parentFieldId + ", " + classId + ")");
    List<String> result = new ArrayList<>(3);
    if (sortingNodes != null) {
      for (SortingNodeAbstract currentNode : sortingNodes) {
        String fastKey =
            currentNode.getFieldForSortingSubquery(classId, parentclassId, parentFieldId);
        if (fastKey != null) {
          result.add(fastKey);
        }
      }
    }
    return result;
  }

  public String getSubqueryForSorting(UUID parentclassId, UUID parentFieldId, UUID classId) {
    // System.out.println("getFieldForSorting(" + parentclassId + ", " + parentFieldId + ", " +
    // classId + ")");
    List<String> result = new ArrayList<>(3);
    // String strResult = "";
    if (sortingNodes != null) {
      for (SortingNodeAbstract currentNode : sortingNodes) {
        String fastKey =
            currentNode.getFieldForSortingSubquery(classId, parentclassId, parentFieldId);
        if (fastKey != null) {
          // strResult += "\"" + fastKey + "\",";
          result.add("" + fastKey + " ");
        }
      }
    }
    String strResult =
        result.stream().collect(Collectors.joining(",")) + (result.size() > 0 ? ", " : "");
    return strResult;
  }

  public String[] getSubqueryForSortingWithIndex(UUID parentclassId, UUID parentFieldId,
      UUID classId) {
    // System.out.println("getFieldForSorting(" + parentclassId + ", " + parentFieldId + ", " +
    // classId + ")");
    List<String> result = new ArrayList<>(3);
    String[] sortingParams = {"", "false"};
    System.out.println("getSubqueryForSortingWithIndex sortingNodes: " + sortingNodes);
    if (sortingNodes != null) {
      sortingParams[1] = sortingNodes.get(0).getFieldForSortingSubquery(classId, parentclassId,
          parentFieldId) != null ? "true" : "false";
      for (SortingNodeAbstract currentNode : sortingNodes) {
        String fastKey =
            currentNode.getFieldForSortingSubquery(classId, parentclassId, parentFieldId);
        if (fastKey != null) {
          // strResult += "\"" + fastKey + "\",";
          result.add("" + fastKey + " ");
        }
      }
    }
    sortingParams[0] =
        result.stream().collect(Collectors.joining(",")) + (result.size() > 0 ? ", " : "");

    return sortingParams;
  }

  public List<String[]> getGeneralKeyElements() {
    if (sortingNodes != null) {
      ArrayList<String[]> result = new ArrayList<>(4);
      for (SortingNodeAbstract currentNode : sortingNodes) {
        String[] fastKeyFields = currentNode.getGeneralParamsFieldForSubquery();
        if (fastKeyFields != null) {
          // strResult += "\"" + fastKey + "\",";
          result.add(fastKeyFields);
        }
      }
      return result;
    }
    return null;
  }

  public List<String[]> getKeyElements(UUID parentclassId, UUID parentFieldId, UUID classId) {
    if (sortingNodes != null) {
      ArrayList<String[]> result = new ArrayList<>(4);
      for (SortingNodeAbstract currentNode : sortingNodes) {
        String[] fastKeyFields =
            currentNode.getOnlyFieldForSortingSubquery(classId, parentclassId, parentFieldId);
        if (fastKeyFields != null) {
          // strResult += "\"" + fastKey + "\",";
          result.add(fastKeyFields);
        }
      }
      return result;
    }
    return null;
  }

  public List<String> getTransitFieldsForClass(UUID parentclassId, UUID parentFieldId,
      UUID classId) {
    List<String> result = new ArrayList<>(3);
    if (sortingNodes != null) {
      for (SortingNodeAbstract currentNode : sortingNodes) {
        String fastKey = currentNode.getTransitFieldNameForSortingSubquery(classId, parentclassId,
            parentFieldId);
        if (fastKey != null) {
          result.add(fastKey);
        }
      }
    }
    return result;
  }

  public String getSubqueryForTransiting(UUID parentclassId, UUID parentFieldId, UUID classId) {
    System.out.println(
        "getFieldForSorting(" + parentclassId + ", " + parentFieldId + ", " + classId + ")");
    List<String> result = new ArrayList<>(3);

    if (sortingNodes != null) {
      // for (SortingNodeAbstract currentNode : sortingNodes) {
      // String fastKey = currentNode.getFieldForSortingSubquery(classId, parentclassId,
      // parentFieldId);
      // if (fastKey != null) {
      // // strResult += "\"" + fastKey + "\",";
      // result.add(fastKey);
      // }
      // }
      for (SortingNodeAbstract currentNode : sortingNodes) {
        String fastKey = currentNode.getTransitFieldNameForSortingSubquery(classId, parentclassId,
            parentFieldId);
        if (fastKey != null) {
          // strResult += "\"" + fastKey + "\",";
          result.add(fastKey);
        }
      }
    }
    String strResult =
        result.stream().collect(Collectors.joining(",")) + (result.size() > 0 ? ", " : "");
    return strResult;
  }
}
