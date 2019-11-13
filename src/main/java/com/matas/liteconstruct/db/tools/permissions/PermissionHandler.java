package com.matas.liteconstruct.db.tools.permissions;

public interface PermissionHandler {
  
  public enum PermissionField{
    USEFUL, VISIBLE, EDIT, DELETE, INSERT;
  }
  
  public static String getStringPermission(boolean useful, boolean visible, boolean edit,
      boolean delete, boolean insert) {
    int result = 0;
    if (useful) {
      result = 10000;
      if (visible) {
        result += 1000;
      }
      if (edit) {
        result += 100;
      }
      if (delete) {
        result += 10;
      }
      if (insert) {
        result += 1;
      }
    } else {
      return "00000";
    }
    return Integer.toString(result);
  }

  public static boolean isUseful(int permission) {
    return permission > 0;
  }

  public static boolean isVisible(int permission) {
    return permission >= 11000;
  }

  public static boolean isEdit(int permission) {
    return permission == 10100 || permission >= 11100;
  }

  public static boolean isDelete(int permission) {
    return permission == 10010 || permission == 11010 || permission >= 11110;
  }

  public static boolean isInsert(int permission) {
    return (permission % 2 != 0);
  }

}
