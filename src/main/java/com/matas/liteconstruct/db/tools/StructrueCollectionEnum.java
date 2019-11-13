package com.matas.liteconstruct.db.tools;

import java.util.stream.Stream;

public enum StructrueCollectionEnum {

  ID("id"), NAME("name"), PERM("perm"), TYPE("type"), INNER("inner"), TITLE("title"), OBJECT(
      "object"), NESTED("nested");

  String settingName;

  StructrueCollectionEnum(String settingName) {
    this.settingName = settingName;
  }

  @Override
  public String toString() {
    return settingName;
  }
  
  public StructrueCollectionEnum getByName(String settingName) {
    return Stream.of(StructrueCollectionEnum.values()).filter(x -> x.toString().equals(settingName)).findAny()
        .orElse(null);
  }
}
