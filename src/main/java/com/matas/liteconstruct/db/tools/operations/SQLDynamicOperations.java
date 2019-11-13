package com.matas.liteconstruct.db.tools.operations;

public interface SQLDynamicOperations {
	public static String getOperationByIndex(String field, String index, String value) {
		switch (index) {
		default:
			return field + " = '" + value + "'";
		case "1":
			return field + " >= '" + value + "'";
		case "2":
			return field + " > '" + value + "'";
		case "3":
			return field + " <= '" + value + "'";
		case "4":
			return field + " < '" + value + "'";
		case "5":
			return field + " like '" + value + "%'";
		case "6":
			return field + " like '%" + value + "'";
		case "7":
			return field + " like '%" + value + "%'";
		case "8":
			return "LOWER(" + field + ") like LOWER('" + value + "')";
		case "9":
			return "LOWER(" + field + ") like LOWER('" + value + "'%)";
		case "10":
			return "LOWER(" + field + ") like LOWER(%'" + value + "')";
		case "11":
			return "LOWER(" + field + ") like LOWER(%'" + value + "'%)";
		case "12":
			return "LOWER(" + field + ") not like LOWER('" + value + "')";
		}
	}
}
