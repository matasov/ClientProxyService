package com.matas.liteconstruct.service.dynamic.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matas.liteconstruct.aspect.log.LogExecutionTime;
import com.matas.liteconstruct.db.models.lngs.abstractmodel.LngCompanyRecordRelation;
import com.matas.liteconstruct.db.models.lngs.repos.LngCompanyRecordRelationsRepository;
import com.matas.liteconstruct.db.tools.StructrueCollectionEnum;
import com.matas.liteconstruct.service.dynamic.CacheMainParams;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LngWrapperService {

  private LngCompanyRecordRelationsRepository lngCompanyRecordRelationsRepository;

  @Autowired
  public void setLngCompanyRecordRelationsRepository(
      LngCompanyRecordRelationsRepository lngCompanyRecordRelationsRepository) {
    this.lngCompanyRecordRelationsRepository = lngCompanyRecordRelationsRepository;
  }

  private CacheMainParams cacheMainParams;

  @Autowired
  public void setCacheMainParams(CacheMainParams cacheMainParams) {
    this.cacheMainParams = cacheMainParams;
  }

  // private UUID mainClassId;

  private Map<String, Object> structureMap;

  // private List<Map<String, Object>> dataRecords;

  // private Map<UUID, List<UUID>> recordsRelationClass;

  private Map<UUID, String> positionInStructureLine;

  // private Map<UUID, List<PathStekToField>> pathsForField;

  // public LngWrapperService() {
  // // dataRecords = new ArrayList<>();
  // // recordsRelationClass = new HashMap<>();
  // positionInStructureLine = new HashMap<>();
  // }
  private List<String> queryRecordsList;
  private List<LngCompanyRecordRelation> resultChangeRecordsList;

  @LogExecutionTime
  public List<Map<String, Object>> getLngList(UUID contactSystemId, UUID mainClassId,
      List<Map<String, Object>> dataRecords) {
    // this.mainClassId = mainClassId;
    if (positionInStructureLine != null) {
      log.info("wtf");
      positionInStructureLine.clear();
    } else {
      positionInStructureLine = new HashMap<>(1);
    }
    if (queryRecordsList != null) {
      log.info("wtf");
      queryRecordsList.clear();
    } else {
      queryRecordsList = new ArrayList<>(1);
    }
    structureMap =
        cacheMainParams.getFastStructureMapCurrentClassForContact(contactSystemId, mainClassId);
    getRecordsRecursively(mainClassId, structureMap, dataRecords);
    return updateRecordsRecursively(mainClassId, structureMap, dataRecords,
        getChangesAndSeparateData());
  }

  public void getRecordsRecursively(UUID currentClassId, Map<String, Object> fastStructureMap,
      List<Map<String, Object>> dataRecords) {
    if (dataRecords == null)
      return;
    queryRecordsList
        .addAll(dataRecords.parallelStream().filter(item -> item != null && item.containsKey("0"))
            .map(item -> (String) item.get("0")).collect(Collectors.toList()));

    for (Map.Entry<String, Object> entry : fastStructureMap.entrySet()) {
      if (((Map<String, Object>) entry.getValue()).get(StructrueCollectionEnum.INNER.toString())
          .equals("3")
          && ((Map<String, Object>) entry.getValue())
              .containsKey(StructrueCollectionEnum.NESTED.toString())) {
        dataRecords.parallelStream().forEach(item -> {
          getRecordsRecursively(
              UUID.fromString((String) ((Map<String, Object>) entry.getValue())
                  .get(StructrueCollectionEnum.OBJECT.toString())),
              (Map<String, Object>) ((Map<String, Object>) entry.getValue())
                  .get(StructrueCollectionEnum.NESTED.toString()),
              (List<Map<String, Object>>) item.get(entry.getKey()));
        });
      }
    }
  }

  public List<Map<String, Object>> updateRecordsRecursively(UUID currentClassId,
      Map<String, Object> fastStructureMap, List<Map<String, Object>> dataRecords,
      Map<UUID, List<LngCompanyRecordRelation>> separateByClassId) {
    if (dataRecords == null)
      return null;
    if (separateByClassId == null || separateByClassId.isEmpty())
      return dataRecords;
    List<String> recordsList =
        dataRecords.parallelStream().filter(item -> item != null && item.containsKey("0"))
            .map(item -> (String) item.get("0")).collect(Collectors.toList());
    List<LngCompanyRecordRelation> updatedValues = separateByClassId.get(currentClassId);
    if (updatedValues != null && !updatedValues.isEmpty()) {
      dataRecords.forEach(item -> {
        int index = 0;
        while (index < updatedValues.size()) {
          if (updatedValues.get(index).getRecordId().toString().equals(item.get("0").toString())) {
            String keyPosition = getPositionFieldInTheStructure(
                updatedValues.get(index).getFieldId(), fastStructureMap);
            if (keyPosition != null) {
              item.put(keyPosition, updatedValues.get(index).getValue());
            }
            updatedValues.remove(index);
          } else {
            index++;
          }
        }
      });
    } else {
      log.warn("not found lng values for class: {}", currentClassId);
    }
    for (Map.Entry<String, Object> entry : fastStructureMap.entrySet()) {
      if (((Map<String, Object>) entry.getValue()).get(StructrueCollectionEnum.INNER.toString())
          .equals("3")
          && ((Map<String, Object>) entry.getValue())
              .containsKey(StructrueCollectionEnum.NESTED.toString())) {
        if (dataRecords != null && !dataRecords.isEmpty())
          dataRecords.stream().forEach(item -> {
            item.put(entry.getKey(),
                updateRecordsRecursively(
                    UUID.fromString((String) ((Map<String, Object>) entry.getValue())
                        .get(StructrueCollectionEnum.OBJECT.toString())),
                    (Map<String, Object>) ((Map<String, Object>) entry.getValue())
                        .get(StructrueCollectionEnum.NESTED.toString()),
                    (List<Map<String, Object>>) item.get(entry.getKey()), separateByClassId));
          });
      }
    }
    return dataRecords;
  }

  private Map<UUID, List<LngCompanyRecordRelation>> getChangesAndSeparateData() {
    if (queryRecordsList == null) {
      // do nothing
      return null;
    }
    Map<UUID, List<LngCompanyRecordRelation>> result = new HashMap<>(1);
    List<LngCompanyRecordRelation> updatedValues =
        lngCompanyRecordRelationsRepository.getLngRecordsByArray(
            queryRecordsList.parallelStream().distinct().collect(Collectors.toList()));
    if (updatedValues != null) {
      updatedValues.parallelStream().forEach(item -> {
        List<LngCompanyRecordRelation> classValues = result.get(item.getClassId());
        if (classValues == null) {
          classValues = new ArrayList(5);
        }
        classValues.add(item);
        result.put(item.getClassId(), classValues);
      });
    }
    log.info("getChangesAndSeparateData result: {}", result);
    return result == null || result.isEmpty() ? null : result;
  }



  // public void updateRecordsByPathRecursively(UUID currentClassId, PathStekToField path,
  // List<Map<String, Object>> dataRecords, List<LngCompanyRecordRelation> updatedValues) {
  // path.isLastElement(currentClassId);
  // }

  public String getPositionFieldInTheStructure(UUID fieldId, Map<String, Object> fastStructureMap)
      throws NullPointerException {
    log.info("getPositionFieldInTheStructure try find {} in {}", fieldId, fastStructureMap);
    if (!positionInStructureLine.containsKey(fieldId)) {
      String position = cacheMainParams.getIndexOfTheFieldId(fieldId, fastStructureMap);
      if (position == null)
        throw new NullPointerException("Not found field in structure.");
      positionInStructureLine.put(fieldId, position);
    }
    return positionInStructureLine.get(fieldId);
  }

  // private void findAllPathsToField(UUID mainClassId, UUID fieldId,
  // Map<String, Object> fastStructureMap) {
  // findPathRecursively(mainClassId, fieldId, fastStructureMap,
  // new PathStekToField(fieldId.toString()));
  // }

  // private void findPathRecursively(UUID currentClassId, UUID fieldId,
  // Map<String, Object> fastStructureMap, PathStekToField path) {
  // fastStructureMap.entrySet().parallelStream().forEach(item -> {
  // if (((Map<String, Object>) item.getValue()).get(StructrueCollectionEnum.ID.toString())
  // .toString().equals(fieldId.toString())) {
  // path.setNextElementToPath(new ArrayList() {
  // {
  // add(currentClassId.toString());
  // add(item.getKey());
  // }
  // });
  // addPathToMap(fieldId, path);
  // } else if (((Map<String, Object>) item.getValue())
  // .get(StructrueCollectionEnum.INNER.toString()).equals("3")
  // && ((Map<String, Object>) item.getValue())
  // .containsKey(StructrueCollectionEnum.NESTED.toString())) {
  // findPathRecursively(
  // UUID.fromString((String) ((Map<String, Object>) item.getValue())
  // .get(StructrueCollectionEnum.OBJECT.toString())),
  // fieldId, (Map<String, Object>) ((Map<String, Object>) item.getValue())
  // .get(StructrueCollectionEnum.NESTED.toString()),
  // clonePath(path));
  // }
  // });
  // }

  // private void addPathToMap(UUID fieldId, PathStekToField path) {
  // List<PathStekToField> paths = pathsForField.get(fieldId);
  // if (paths == null) {
  // paths = new ArrayList<>();
  // }
  // paths.add(path);
  // pathsForField.put(fieldId, paths);
  // }
  //
  // private PathStekToField clonePath(PathStekToField path) {
  // PathStekToField newPath = new PathStekToField(path.getTargetFieldId());
  // newPath.setElements(path.getElements());
  // return newPath;
  // }
  //
  // private class PathStekToField {
  //
  // @Getter
  // String targetFieldId;
  //
  // @Getter
  // @Setter
  // List<List<String>> elements;
  //
  // PathStekToField(String targetFieldId) {
  // this.targetFieldId = targetFieldId;
  // elements = new ArrayList<>();
  // }
  //
  // void setNextElementToPath(List<String> nextElement) {
  // elements.add(nextElement);
  // }
  //
  // public boolean isLastElement(UUID classId) {
  // return elements.get(elements.size() - 1).get(0).equals(classId.toString());
  // }
  // }
}
