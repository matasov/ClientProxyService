package com.matas.liteconstruct.db.tools.conditions;

import java.util.TreeMap;

public abstract class AbstractCondition {
	TreeMap<String, Object> conditions;

	public AbstractCondition() {
		conditions = new TreeMap<String, Object>();
	}

	public AbstractCondition addCondition(String key, Object value) {
		conditions.put(key, value);
		return this;
	}

	public Object getCondition(String key) {
		return conditions.get(key);
	}

	public boolean hasCondition(String key) {
		return conditions.containsKey(key);
	}
}
