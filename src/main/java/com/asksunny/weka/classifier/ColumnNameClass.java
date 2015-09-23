package com.asksunny.weka.classifier;

public class ColumnNameClass {

	
	private String columnName;
	private String clazz;
	
	
	
	public ColumnNameClass(String columnName, String clazz) {
		super();
		this.columnName = columnName;
		this.clazz = clazz;
	}

	

	public String getColumnName() {
		return columnName;
	}



	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}



	public String getClazz() {
		return clazz;
	}



	public void setClazz(String clazz) {
		this.clazz = clazz;
	}



	public ColumnNameClass() {
		// TODO Auto-generated constructor stub
	}

	

}
