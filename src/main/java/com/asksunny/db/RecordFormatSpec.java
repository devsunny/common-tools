package com.asksunny.db;

import java.util.ArrayList;
import java.util.List;

public class RecordFormatSpec {

	char delimiterChar = '\n';
	List<FieldFormatSpec> fieldFormatSpecs = new ArrayList<>();
	
	
	public RecordFormatSpec addFieldFormatSpec(FieldFormatSpec fieldSpec)
	{
		this.fieldFormatSpecs.add(fieldSpec);
		return this;
	}
	
	
	
	public char getDelimiterChar() {
		return delimiterChar;
	}



	public RecordFormatSpec setDelimiterChar(char delimiterChar) {
		this.delimiterChar = delimiterChar;
		return this;
	}

	public RecordFormatSpec setDelimiterChar(String delimiterChar) {
		this.delimiterChar = CharUtils.toChar(delimiterChar);
		return this;
	}

	public List<FieldFormatSpec> getFieldFormatSpecs() {
		return fieldFormatSpecs;
	}



	public RecordFormatSpec setFieldFormatSpecs(List<FieldFormatSpec> fieldFormatSpecs) {
		this.fieldFormatSpecs = fieldFormatSpecs;
		return this;
	}



	public RecordFormatSpec() {		
	}

}
