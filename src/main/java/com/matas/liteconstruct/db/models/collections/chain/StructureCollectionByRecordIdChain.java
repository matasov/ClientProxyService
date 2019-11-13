package com.matas.liteconstruct.db.models.collections.chain;

import com.matas.liteconstruct.db.models.structure.conditions.ConditionsConstants;
import com.matas.liteconstruct.db.tools.chain.AbstractChain;
import com.matas.liteconstruct.db.tools.chain.QueryAbstract;
import com.matas.liteconstruct.db.tools.conditions.AbstractCondition;

public class StructureCollectionByRecordIdChain extends AbstractChain {

	@Override
	public boolean check(AbstractCondition conditions, QueryAbstract resultQuery) {
		if (conditions.hasCondition(ConditionsConstants.STR_COLLECTION_RECORD_ID)) {
			resultQuery.addQuery("and", " \"class_collections_fields_use\".\"id\" = '"
					+ conditions.getCondition(ConditionsConstants.STR_COLLECTION_RECORD_ID) + "'");
			return checkNext(conditions, resultQuery);
		} else {
			return true;
		}
	}
}