package com.matas.liteconstruct.db.models.structure.chain;

import com.matas.liteconstruct.db.tools.conditions.AbstractCondition;
import com.matas.liteconstruct.db.models.structure.conditions.ConditionsConstants;
import com.matas.liteconstruct.db.tools.chain.AbstractChain;
import com.matas.liteconstruct.db.tools.chain.QueryAbstract;

public class StructureFieldByIdChain extends AbstractChain {

	@Override
	public boolean check(AbstractCondition conditions, QueryAbstract resultQuery) {
		if (conditions.hasCondition(ConditionsConstants.STR_RECORD_ID)) {
			resultQuery.setQuery(" id = '" + conditions.getCondition(ConditionsConstants.STR_RECORD_ID) + "'");
			return true;
		} else
			return checkNext(conditions, resultQuery);
	}

}
