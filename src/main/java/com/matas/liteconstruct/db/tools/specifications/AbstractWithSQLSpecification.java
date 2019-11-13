package com.matas.liteconstruct.db.tools.specifications;

public abstract class AbstractWithSQLSpecification<T> extends AbstractSpecification<T> implements SQLSpecification{
	
	public AbstractSpecification<T> or(Specification<T> s) {
		return new OrSpecification<T>(this, s);
	}

	public AbstractSpecification<T> and(Specification<T> s) {
		return new AndSpecification<T>(this, s);
	}

	public AbstractSpecification<T> not() {
		return new NotSpecification<T>(this);
	}
	
	public abstract boolean isSatisfiedBy(T t);
}
