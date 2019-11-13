package com.matas.liteconstruct.db.models.structure.specifications.structure;

import java.util.UUID;

import com.matas.liteconstruct.db.DBConstants;
import com.matas.liteconstruct.db.tools.specifications.AbstractWithSQLSpecification;
import com.matas.liteconstruct.db.tools.conditions.AbstractCondition;
import com.matas.liteconstruct.db.models.structure.conditions.ConditionsConstants;

public class StructureFieldsSQLSpecification extends AbstractWithSQLSpecification<AbstractCondition> {

	private UUID classId;

	public StructureFieldsSQLSpecification() {
	}

	@Override
	public String toSqlClauses() {
		// return "SELECT * FROM class_structure_fields where
		// class_structure_fields.class = ?";
		return String.format("SELECT * FROM %1$s WHERE `%2$s` = '%3$d'", DBConstants.TBL_STRUCTURE_FIELDS, "class_id",
				classId);
	}

	@Override
	public boolean isSatisfiedBy(AbstractCondition conditions) {
		if (conditions.hasCondition(ConditionsConstants.STR_CLASS_ID)) {
			classId = (UUID) conditions.getCondition(ConditionsConstants.STR_CLASS_ID);
			return true;
		}
		return false;

	}

}
