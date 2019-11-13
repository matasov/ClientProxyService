package com.matas.liteconstruct.service.management.structure;

import java.util.UUID;
import lombok.Getter;
import java.util.stream.Stream;

public enum PrimitiveCustomClass {
  INTEGER("df0361f8-6f1b-4b7a-9062-c44b624b676a", "Integer", 0, 0), LONG(
      "8a8e6e83-9086-4af8-9379-7db4abb62509", "Long", 0,
      0), FLOAT("37b1c6cb-722f-4918-a6e1-2595f19f8dd4", "Float", 0, 0), DOUBLE(
          "00251e04-c11f-44a1-a5ee-c51e6834c3f3", "Double", 0,
          0), TIMESTAMP("37390d87-67d3-4eea-afe5-47d93f8936b0", "Timestamp", 0, 0), BOOL(
              "7087d761-3d73-4760-bba6-529b55d64e90", "Bool", 0,
              0), CUUID("a1d5b5c0-de80-43b4-bcb8-8168a4fedbd4", "Uuid", 0, 0), STRING(
                  "70b8c94f-7a92-4a1e-b8ab-25abd9211187", "String", 0,
                  0), TEXT("1353b45e-2fcc-4450-87b7-1f8b91663c1f", "Text", 0, 0), JSONB(
                      "fda03425-dcc1-4761-bcb2-2b460749a169", "Jsonb", 0, 0), SEQUENCESTRING(
                          "fcd278b5-719e-49d3-89c0-3739a922415f", "Sequence string", 0,
                          0), IMAGE("33fc0855-8d18-476d-8cd3-1127e7a5fa33", "Image", 0, 0), SYSTEM(
                              "3a1ee914-ec32-4e83-8f04-df0897daf8e9", "System", 0,
                              -1), CUSTOM("5287f5ba-0de2-4021-9877-c010763188c7", "Custom", 0, -1);
  @Getter
  private java.util.UUID uuid;
  @Getter
  private String name;
  @Getter
  private int type;
  @Getter
  private int permission;

  PrimitiveCustomClass(String uuid, String name, int type, int permission) {
    this.uuid = UUID.fromString(uuid);
    this.name = name;
    this.type = type;
    this.permission = permission;
  }

  public String getId() {
    return uuid.toString();
  }

  public static PrimitiveCustomClass getById(String primitiveStrID) {
    if (primitiveStrID == null)
      return null;
    UUID primitiveID = UUID.fromString(primitiveStrID);
    return getById(primitiveID);
  }

  public static PrimitiveCustomClass getById(UUID primitiveID) {
    if (primitiveID == null)
      return null;
    return Stream.of(PrimitiveCustomClass.values()).filter(x -> x.getUuid().equals(primitiveID))
        .findAny().orElse(null);
  }

  public static PrimitiveCustomClass getByName(String name) {
    if (name == null)
      return null;
    return Stream.of(PrimitiveCustomClass.values()).filter(x -> x.getName().equals(name)).findAny()
        .orElse(null);
  }

  public static PrimitiveCasePermission canChangeToClass(UUID extFromClass, UUID extToClass) {
    PrimitiveCustomClass fromClass = getById(extFromClass);
    if (fromClass == null) {
      fromClass = CUSTOM;
    }
    PrimitiveCustomClass toClass = getById(extToClass);
    if (toClass == null) {
      toClass = CUSTOM;
    }
    if (toClass.equals(fromClass))
      return PrimitiveCasePermission.YES_CASE;
    switch (fromClass) {
      case INTEGER:
        return fromInteger(toClass);
      case LONG:
        return fromBigInteger(toClass);
      case DOUBLE:
        return fromDouble(toClass);
      case FLOAT:
        return fromFloat(toClass);
      case TEXT:
        return fromText(toClass);
      case STRING:
        return fromString(toClass);
      case CUUID:
      case CUSTOM:
        return fromUUID(toClass);
      default:
        return PrimitiveCasePermission.NO;
    }
  }

  private static PrimitiveCasePermission fromInteger(PrimitiveCustomClass toClass) {
    switch (toClass) {
      case STRING:
      case TEXT:
      case FLOAT:
      case DOUBLE:
      case LONG:
        return PrimitiveCasePermission.YES_CASE;
      case TIMESTAMP:
        return PrimitiveCasePermission.YES_OPERATION;
      case BOOL:
      case JSONB:
      case IMAGE:
      case CUUID:
      case SYSTEM:
      case SEQUENCESTRING:
      case CUSTOM:
      default:
        return PrimitiveCasePermission.NO;
    }
  }

  private static PrimitiveCasePermission fromBigInteger(PrimitiveCustomClass toClass) {
    switch (toClass) {
      case INTEGER:
        return PrimitiveCasePermission.YES_OPERATION;
      default:
        return fromInteger(toClass);
    }
  }

  private static PrimitiveCasePermission fromDouble(PrimitiveCustomClass toClass) {
    switch (toClass) {
      case STRING:
      case TEXT:
      case FLOAT:
        return PrimitiveCasePermission.YES_CASE;
      case INTEGER:
      case TIMESTAMP:
        return PrimitiveCasePermission.YES_OPERATION;
      case BOOL:
      case JSONB:
      case IMAGE:
      case CUUID:
      case SYSTEM:
      case SEQUENCESTRING:
      case CUSTOM:
      default:
        return PrimitiveCasePermission.NO;
    }
  }

  private static PrimitiveCasePermission fromFloat(PrimitiveCustomClass toClass) {
    switch (toClass) {
      case STRING:
      case TEXT:
      case DOUBLE:
        return PrimitiveCasePermission.YES_CASE;
      case INTEGER:
      case TIMESTAMP:
        return PrimitiveCasePermission.YES_OPERATION;
      case BOOL:
      case JSONB:
      case IMAGE:
      case CUUID:
      case SYSTEM:
      case SEQUENCESTRING:
      case CUSTOM:
      default:
        return PrimitiveCasePermission.NO;
    }
  }

  private static PrimitiveCasePermission fromText(PrimitiveCustomClass toClass) {
    switch (toClass) {
      case STRING:
        return PrimitiveCasePermission.YES_CASE;
      case IMAGE:
        return PrimitiveCasePermission.YES_OPERATION;
      case INTEGER:
      case DOUBLE:
      case FLOAT:
      case TIMESTAMP:
      case BOOL:
      case JSONB:
      case CUUID:
      case SYSTEM:
      case SEQUENCESTRING:
      case CUSTOM:
      default:
        return PrimitiveCasePermission.NO;
    }
  }

  private static PrimitiveCasePermission fromString(PrimitiveCustomClass toClass) {
    switch (toClass) {
      case TEXT:
        return PrimitiveCasePermission.YES_CASE;
      case IMAGE:
        return PrimitiveCasePermission.YES_OPERATION;
      case INTEGER:
      case DOUBLE:
      case FLOAT:
      case TIMESTAMP:
      case BOOL:
      case JSONB:
      case CUUID:
      case SYSTEM:
      case SEQUENCESTRING:
      case CUSTOM:
      default:
        return PrimitiveCasePermission.NO;
    }
  }

  private static PrimitiveCasePermission fromUUID(PrimitiveCustomClass toClass) {
    switch (toClass) {
      case CUSTOM:
      case CUUID:
        return PrimitiveCasePermission.YES_CASE;
      case IMAGE:
      case TEXT:
      case INTEGER:
      case DOUBLE:
      case FLOAT:
      case TIMESTAMP:
      case BOOL:
      case JSONB:
      case SYSTEM:
      case SEQUENCESTRING:

      default:
        return PrimitiveCasePermission.NO;
    }
  }

  public static String getTableDataType(PrimitiveCustomClass fromClass) {
    switch (fromClass) {
      case INTEGER:
        return "integer";
      case DOUBLE:
        return "double precision";
      case FLOAT:
        return "real";
      case TIMESTAMP:
        return "bigint";
      case BOOL:
        return "boolean";
      case CUUID:
      case SYSTEM:
        return "uuid";
      case JSONB:
      case SEQUENCESTRING:
        return "jsonb";
      case STRING:
        return "character varying";
      case IMAGE:
      case TEXT:
      default:
        return "text";
    }
  }
}
