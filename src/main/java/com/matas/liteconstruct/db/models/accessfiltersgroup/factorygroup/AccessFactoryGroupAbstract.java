package com.matas.liteconstruct.db.models.accessfiltersgroup.factorygroup;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface AccessFactoryGroupAbstract {

	void setWorkclassId(UUID classId);
	
	void setFilterGroupId(UUID filterGroupId);
	
	String getSubquery(Map<String, Object> requestParams);

	public static List<Object> getAllFiltersFromMap(Map<String, Object> fromJsonMap) {
		return flatten(fromJsonMap).collect(Collectors.toList());
	}

	public static Stream<Object> flatten(Object o) {
		if (o instanceof Map<?, ?>) {
			return ((Map<?, ?>) o).entrySet().stream()
					.filter(x -> (x.getValue() instanceof Map<?, ?> || x.getValue() instanceof List<?>
							|| ((String) x.getKey()).startsWith("v_")))
					.map(x -> x.getValue()).flatMap(AccessFactoryGroupAbstract::flatten);
		} else if (o instanceof List<?>) {
			return ((List<?>) o).stream().flatMap(AccessFactoryGroupAbstract::flatten);
		}
		return Stream.of(o);
	}

	public static String getFastKey(UUID parentclassId, UUID parentFieldId, UUID classId) {
		return new StringBuilder(24).append(getFirstPartOfUUID(parentclassId)).append(".").append(getFirstPartOfUUID(parentFieldId)).append(".")
				.append(getFirstPartOfUUID(classId)).toString();
	}

	public static String getFirstPartOfUUID(UUID value) {
		if (value == null) {
			return "null";
		}
		return new StringBuilder(8).append(value.toString()).substring(0, 8).toString();
	}

}
