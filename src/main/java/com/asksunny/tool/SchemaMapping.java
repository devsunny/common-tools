package com.asksunny.tool;

public class SchemaMapping {

	private String tableName;
	private String sorName;
	private String datasetName;

	public SchemaMapping(String tableName, String sorName, String datasetName) {
		super();
		this.tableName = tableName;
		this.sorName = sorName;
		this.datasetName = datasetName;
	}

	public static SchemaMapping newInstance(String tableName, String sorName, String datasetName) {
		SchemaMapping mapping = new SchemaMapping(tableName, sorName, datasetName);
		return mapping;
	}

	public SchemaMapping() {
	}

	public String getTableName() {
		return tableName;
	}

	public SchemaMapping setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}

	public String getSorName() {
		return sorName;
	}

	public SchemaMapping setSorName(String sorName) {
		this.sorName = sorName;
		return this;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public SchemaMapping setDatasetName(String datasetName) {
		this.datasetName = datasetName;
		return this;
	}

}
