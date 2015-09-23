package com.asksunny.db;

public class EntityMetaData {

	String entityName;
	char entityDelimiter;
	char fieldDelimiter;
	char paddingChar;
	String sourceName;
	FieldMetadata[] fieldMetadatas;
	
	
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public char getEntityDelimiter() {
		return entityDelimiter;
	}
	public void setEntityDelimiter(char entityDelimiter) {
		this.entityDelimiter = entityDelimiter;
	}
	public char getFieldDelimiter() {
		return fieldDelimiter;
	}
	public void setFieldDelimiter(char fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}
	public char getPaddingChar() {
		return paddingChar;
	}
	public void setPaddingChar(char paddingChar) {
		this.paddingChar = paddingChar;
	}
	public FieldMetadata[] getFieldMetadatas() {
		return fieldMetadatas;
	}
	public void setFieldMetadatas(FieldMetadata[] fieldMetadatas) {
		this.fieldMetadatas = fieldMetadatas;
	}
	
	
	

}
