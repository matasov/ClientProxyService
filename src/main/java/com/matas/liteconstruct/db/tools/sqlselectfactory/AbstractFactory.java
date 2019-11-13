package com.matas.liteconstruct.db.tools.sqlselectfactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.matas.liteconstruct.db.tools.conditions.AbstractCondition;
import com.matas.liteconstruct.db.tools.field.FieldList;
import com.matas.liteconstruct.db.tools.field.TableFieldInterface;
import com.matas.liteconstruct.db.models.structure.chain.StructureQueryWhereChain;
import com.matas.liteconstruct.db.tools.chain.AbstractChain;
import com.matas.liteconstruct.db.tools.chain.QueryAbstract;

import lombok.Data;

@Data
public abstract class AbstractFactory implements FieldsFactoryInterface {

	private String objectString;

	private FieldList fields;

	private QueryAbstract whereClause;

	private Map<String, Boolean> orderBy;

	private List<Integer> limit;

	public String getQuery() throws SQLException {

		if (objectString == null) {
			throw new SQLException();
		}
		String result = "select ";
		if (fields != null && !fields.isEmpty()) {
			for (TableFieldInterface field : fields.getFields()) {
				result += field.getFieldName() + ", ";
			}
		} else {
			result += "*  ";
		}
		result = result.substring(0, result.length() - 2) + " from " + objectString;

		if (whereClause != null && whereClause.getQuery() != null) {
			result += " where " + whereClause.getQuery();
		}

		if (orderBy != null && !orderBy.isEmpty()) {
			result += " order by ";
			for (Map.Entry<String, Boolean> entry : orderBy.entrySet()) {
				result += getValueForOrderBy(entry.getKey(), entry.getValue()) + ", ";
			}
			result = result.substring(0, result.length() - 2) + " ";
		}

		if (limit != null && !limit.isEmpty()) {
			result += "limit " + limit.get(0);
			if (limit.size() > 1) {
				result += ", " + limit.get(1);
			}
		}

		return result;
	}

	private String getValueForOrderBy(String name, boolean asc) {
		return name + " " + (asc ? "asc " : "desc");
	}

	public void setWhere(List<AbstractChain> queryWhereBlocks, AbstractCondition conditions) {
		if (whereClause == null) {
			whereClause = new StructureQueryWhereChain();
		} else {
			whereClause.setQuery("");
		}
		if (queryWhereBlocks != null && !queryWhereBlocks.isEmpty()) {
			AbstractChain first = queryWhereBlocks.get(0);
			int index = 1;
			while (index < queryWhereBlocks.size()) {
				first.linkWith(queryWhereBlocks.get(index));
				index++;
			}
			first.check(conditions, whereClause);
		}
	}

	public FieldList getFields() {
		return fields;
	}
}
