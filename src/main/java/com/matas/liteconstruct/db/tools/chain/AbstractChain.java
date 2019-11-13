package com.matas.liteconstruct.db.tools.chain;

import com.matas.liteconstruct.db.tools.conditions.AbstractCondition;

public abstract class AbstractChain {

	private AbstractChain next;

	public AbstractChain linkWith(AbstractChain next) {
		this.next = next;
		return next;
	}

	protected boolean checkNext(AbstractCondition conditions, QueryAbstract resultQuery) {
		if (next == null) {
			return true;
		}
		return next.check(conditions, resultQuery);
	}

	public abstract boolean check(AbstractCondition conditions, QueryAbstract resultQuery);
}
