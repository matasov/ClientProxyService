package com.matas.liteconstruct.db.tools.operations;

public interface SQLStaticOperations {

  public static String getOperationByIndex(String field, short index, String value) {
    switch (index) {
      default:
        return field + " = '" + value + "'";
      case 1:
        return field + " >= '" + value + "'";
      case 2:
        return field + " > '" + value + "'";
      case 3:
        return field + " <= '" + value + "'";
      case 4:
        return field + " < '" + value + "'";
      case 5:
        return field + " like '" + value + "%'";
      case 6:
        return field + " like '%" + value + "'";
      case 7:
        return field + " like '%" + value + "%'";
      case 8:
        return "LOWER(" + field + ") like LOWER('" + value + "')";
      case 9:
        return "LOWER(" + field + ") like LOWER('" + value + "%')";
      case 10:
        return "LOWER(" + field + ") like LOWER('%" + value + "')";
      case 11:
        return "LOWER(" + field + ") like LOWER('%" + value + "%')";
      case 12:
        return "LOWER(" + field + ") not like LOWER('" + value + "')";
      case 13:
        return "" + field + " != '" + value + "'";
    }
  }

  public static String getOperationByIndexForSelects(String field, short index, String value) {
    switch (index) {
      default:
        return field + " = " + value + "";
      case 1:
        return field + " >= " + value + "";
      case 2:
        return field + " > " + value + "";
      case 3:
        return field + " <= " + value + "";
      case 4:
        return field + " < '" + value + "'";
      case 5:
        return field + "::character varying like concat(" + value + ",'%')";
      case 6:
        return field + "::character varying like concat('%', " + value + ")";
      case 7:
        return field + "::character varying like concat('%', " + value + ",'%')";
      case 8:
        return "LOWER(" + field + "::character varying) like LOWER('" + value + "'::character varying)";
      case 9:
        return "LOWER(" + field + "::character varying) like concat(LOWER('" + value + "::character varying), '%')";
      case 10:
        return "LOWER(" + field + "::character varying) like concat('%', LOWER('" + value + "'::character varying))";
      case 11:
        return "LOWER(" + field + "::character varying) like concat('%', LOWER(" + value + "::character varying), '%')";
      case 12:
        return "LOWER(" + field + "::character varying) not like LOWER('" + value + "'::character varying)";
      case 13:
        return "" + field + " != '" + value + "'";
    }
  }
}
