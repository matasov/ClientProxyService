package com.matas.liteconstruct.db.models.sortfactory.model;

import java.util.List;
import java.util.UUID;
import com.matas.liteconstruct.db.models.sortfactory.abstractmodel.SortingNodeAbstract;
import com.matas.liteconstruct.db.models.streamfiltersgroup.factorygroup.FactoryGroupAbstract;

public class SortingNode implements SortingNodeAbstract {

  private UUID classId;

  private UUID fieldId;

  private UUID parentclassId;

  private UUID parentFieldId;

  private List<List<UUID>> path;

  private String fastKey;

  private int direction;

  public SortingNode(UUID classId, UUID fieldId, UUID parentclassId, UUID parentFieldId,
      List<List<UUID>> path, int direction) {
    this.classId = classId;
    this.fieldId = fieldId;
    this.parentclassId = parentclassId;
    this.parentFieldId = parentFieldId;
    this.path = path;
    fastKey = new StringBuilder(35).append(FactoryGroupAbstract.getFirstPartOfUUID(parentclassId))
        .append("_").append(FactoryGroupAbstract.getFirstPartOfUUID(parentFieldId)).append("_")
        .append(FactoryGroupAbstract.getFirstPartOfUUID(classId)).append("_")
        .append(FactoryGroupAbstract.getFirstPartOfUUID(fieldId)).toString();
    this.direction = direction;
  }

  @Override
  public UUID getClassId() {
    return classId;
  }

  @Override
  public UUID getFieldId() {
    return fieldId;
  }

  @Override
  public UUID getParentclassId() {
    return parentclassId;
  }

  @Override
  public UUID getParentFieldId() {
    return parentFieldId;
  }

  @Override
  public String getTransitFieldNameForSortingSubquery(UUID requestclassId, UUID parentclassId,
      UUID parentFieldId) {
    for (int i = 0; i < path.size(); i++)// List<UUID> nodeValues : path)
    {
      List<UUID> nodeValues = path.get(i);
      // System.out.println("equal (" + requestclassId + ", " + parentclassId + ", " + parentFieldId
      // + ")");
      // System.out.println("with (" + nodeValues.get(0) + ", " + nodeValues.get(2) + ", " +
      // nodeValues.get(3)
      // + ") ["
      // + (nodeValues != null && nodeValues.get(0).equals(requestclassId)
      // && ((nodeValues.get(2) == null && parentclassId == null)
      // || (parentclassId != null && parentclassId.equals(nodeValues.get(2))))
      // && ((nodeValues.get(3) == null && parentFieldId == null)
      // || (parentFieldId != null && parentFieldId.equals(nodeValues.get(3)))))
      // + "]");
      if (nodeValues != null && nodeValues.get(0).equals(requestclassId)
          && ((nodeValues.get(2) == null && parentclassId == null)
              || (parentclassId != null && parentclassId.equals(nodeValues.get(2))))
          && ((nodeValues.get(3) == null && parentFieldId == null)
              || (parentFieldId != null && parentFieldId.equals(nodeValues.get(3))))) {
        return "\"" + fastKey + "\"";
      }
    }
    return null;
  }

  @Override
  public String getFieldForSortingSubquery(UUID requestclassId, UUID parentclassId,
      UUID parentFieldId) {
    System.out.println("getFieldForSortingSubquery path: " + path);
    if (path != null) {

      List<UUID> lastElement = path.get(path.size() - 1);
      // System.out.println("equal (class: " + requestclassId + ", pclass: " + parentclassId + ",
      // pfield: "
      // + parentFieldId + ")");
      // System.out.println("with (class: " + lastElement.get(0) + ", pclass: " + lastElement.get(2)
      // + ", pfield: "
      // + lastElement.get(3) + ") ["
      // + (lastElement != null && lastElement.get(0).equals(requestclassId)
      // && ((lastElement.get(2) == null && parentclassId == null)
      // || (parentclassId != null && parentclassId.equals(lastElement.get(2))))
      // && ((lastElement.get(3) == null && parentFieldId == null)
      // || (parentFieldId != null && parentFieldId.equals(lastElement.get(3)))))
      // + "]");
      if (requestclassId.equals(lastElement.get(0))
          && ((lastElement.get(2) == null && parentclassId == null)
              || (parentclassId != null && parentclassId.equals(lastElement.get(2))))
          && ((lastElement.get(3) == null && parentFieldId == null)
              || (parentFieldId != null && parentFieldId.equals(lastElement.get(3))))) {
        return "\"cc_" + lastElement.get(0) + "_data_use\".\"" + lastElement.get(1) + "\" as \""
            + fastKey + "\"";
      }
    }
    return null;
  }

  @Override
  public String[] getOnlyFieldForSortingSubquery(UUID requestclassId, UUID parentclassId,
      UUID parentFieldId) {
    if (path != null) {

      List<UUID> lastElement = path.get(path.size() - 1);
      // System.out.println("equal (class: " + requestclassId + ", pclass: " + parentclassId + ",
      // pfield: "
      // + parentFieldId + ")");
      // System.out.println("with (class: " + lastElement.get(0) + ", pclass: " + lastElement.get(2)
      // + ", pfield: "
      // + lastElement.get(3) + ") ["
      // + (lastElement != null && lastElement.get(0).equals(requestclassId)
      // && ((lastElement.get(2) == null && parentclassId == null)
      // || (parentclassId != null && parentclassId.equals(lastElement.get(2))))
      // && ((lastElement.get(3) == null && parentFieldId == null)
      // || (parentFieldId != null && parentFieldId.equals(lastElement.get(3)))))
      // + "]");
      if (requestclassId.equals(lastElement.get(0))
          && ((lastElement.get(2) == null && parentclassId == null)
              || (parentclassId != null && parentclassId.equals(lastElement.get(2))))
          && ((lastElement.get(3) == null && parentFieldId == null)
              || (parentFieldId != null && parentFieldId.equals(lastElement.get(3))))) {
        String[] result = {lastElement.get(0).toString(), lastElement.get(1).toString(), fastKey,
            getDirectionString()};
        return result;
      }
    }
    return null;
  }

  public String[] getGeneralParamsFieldForSubquery() {
    List<UUID> lastElement = path.get(path.size() - 1);
    String[] result = {lastElement.get(0).toString(), lastElement.get(1).toString(), fastKey,
        getDirectionString()};
    return result;
  }

  @Override
  public String getFastKey() {
    return fastKey;
  }

  @Override
  public String getDirectionString() {
    return direction == 0 ? "asc" : "desc";
  }

}
