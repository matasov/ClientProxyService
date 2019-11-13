package com.matas.liteconstruct.db.tools.operations;

public enum StringEqualsOperations {
	absoluteEquals, notEquals,
	// like operations
	like, likeStartWith, likeEndsWith, likeConsistsWith, likePartsWith,
	// lower case operations
	lowerCaseEquals, lowerCaseLikeStartsWith, lowerCaseLikeEndsWith, lowerCaseLikeConsistsWith, lowerCaseLikePartsWith,
	lowerCaseNotEquals
}
