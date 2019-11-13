package com.matas.liteconstruct.db.tools.sqlselectfactory;

import java.sql.SQLException;

import com.matas.liteconstruct.db.tools.field.FieldList;

public interface FieldsFactoryInterface {
	public String getQuery() throws SQLException;
	
	public FieldList getFields();
}
