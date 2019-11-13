package com.matas.liteconstruct.db.tools.objectfactory;
/**
 * Simple way to create any object.
 * @author engineer
 *
 */
public abstract class AbstractObjectFactory {
	
	/**
	 * table name
	 */
	private String className;
	
	public AbstractObjectFactory(String className) {
		this.className = className;
	}
	
	public AbstractObjectFactory addFieldValue(String fieldName, Object value) {
		
		return this;
	}
}
