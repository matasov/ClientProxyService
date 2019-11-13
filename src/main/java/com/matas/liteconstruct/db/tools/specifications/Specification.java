package com.matas.liteconstruct.db.tools.specifications;

public interface Specification<T> {
	public boolean isSatisfiedBy(T t);
}