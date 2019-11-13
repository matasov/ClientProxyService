package com.matas.liteconstruct.db.models.structure.chain;

import com.matas.liteconstruct.db.tools.conditions.AbstractCondition;
import com.matas.liteconstruct.db.models.structure.conditions.ConditionsConstants;
import com.matas.liteconstruct.db.tools.chain.AbstractChain;
import com.matas.liteconstruct.db.tools.chain.QueryAbstract;

public class StructureFieldByClassIDChain extends AbstractChain {

	@Override
	public boolean check(AbstractCondition conditions, QueryAbstract resultQuery) {
		if (conditions.hasCondition(ConditionsConstants.STR_CLASS_ID)) {
			resultQuery.setQuery(" class_id = '" + conditions.getCondition(ConditionsConstants.STR_CLASS_ID) + "'");
			return true;
		} else
			return checkNext(conditions, resultQuery);
	}

}