package com.matas.liteconstruct.db.tools.chain;

public abstract class QueryAbstract {
	
	protected String query;
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}
	
	public void addQuery(String operand, String query) {
		this.query += (isPresentQuery() ? " " + operand + " " : "") + query;
	}
	
	private boolean isPresentQuery() {
		return (query!=null && !query.equals(""));
	}
	
}
