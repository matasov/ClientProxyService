package com.matas.liteconstruct.db.tools.operations;

public enum SQLEqualsOperations {
	absoluteEquals, moreAndEquals, moreThan, lessAndEquals, lessThan, notEquals,
	// like operations
	likeStartWith, likeEndsWith, likeConsistsWith,
	// lower case operations
	lowerCaseEquals, lowerCaseLikeStartsWith, lowerCaseLikeEndsWith, lowerCaseLikeConsistsWith, lowerCaseNotEquals
}
