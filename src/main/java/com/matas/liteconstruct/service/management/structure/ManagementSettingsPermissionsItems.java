package com.matas.liteconstruct.service.management.structure;

import java.util.stream.Stream;

public enum ManagementSettingsPermissionsItems {
  GET_CLASSES("get_classes"), GET_STRUCTURES("get_structures"), GET_COLLECTIONS(
      "get_collections"), EDIT_CLASS("edit_class"), INSERT_CLASS("insert_class"), DELETE_CLASS(
          "delete_class"), EDIT_STRUCTURE(
              "edit_structure"), INSERT_STRUCTURE("insert_structure"), DELETE_STRUCTURE(
                  "delete_structure"), EDIT_COLLECTIONS("edit_collections"), INSERT_COLLECTIONS(
                      "insert_collections"), DELETE_COLLECTIONS("delete_collections");

  private String name;

  ManagementSettingsPermissionsItems(String name) {
    this.name = name;
  }

  public ManagementSettingsPermissionsItems getKind(String name) {
    return Stream.of(ManagementSettingsPermissionsItems.values()).filter(x -> x.name.equals(name))
        .findAny().orElse(null);
  }

  @Override
  public String toString() {
    return name;
  }

}
