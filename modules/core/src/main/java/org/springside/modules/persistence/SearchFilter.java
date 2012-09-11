package org.springside.modules.persistence;

public class SearchFilter {

	public enum Operator {
		EQ, LIKE
	}

	public String fieldName;
	public Object value;
	public Operator operator;

	public SearchFilter(String fieldName, Object value, Operator operator) {
		super();
		this.fieldName = fieldName;
		this.value = value;
		this.operator = operator;
	}

}
