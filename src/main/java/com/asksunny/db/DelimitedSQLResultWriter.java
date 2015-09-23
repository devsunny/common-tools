package com.asksunny.db;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DelimitedSQLResultWriter extends AbstractSQLResultWriter{

	
	boolean showHeader;
	boolean shouldFullHeader;
	
	
	char columnDelimiter = '|';
	char recordDelimiter = '\n';
	char headerDelimiter = ':';
	
	public DelimitedSQLResultWriter(Writer out) throws IOException {
		super(out);		
	}
	
	
	@Override
	public void write(ResultSet rs) throws IOException, SQLException {
		writeMetaData();
		if(showHeaderOnly) return;
		
		
	}

	
	protected void writeMetaData() throws IOException, SQLException 
	{
		if(!showHeader && !showHeaderOnly && !shouldFullHeader){
			return;
		}		
		for(int i=0; i<columnCount; i++){
			if(shouldFullHeader){
				out.write(columnNames[i]);
				out.write(headerDelimiter);
				out.write(columnTypes[i]);
				out.write(headerDelimiter);				
				out.write(columnSizes[i]);
				out.write(headerDelimiter);
				out.write(columnTypeNames[i]);				
				if(i<columnCount) out.write(this.columnDelimiter);
			}else{
				out.write(columnNames[i]);
				if(i<columnCount) out.write(this.columnDelimiter);
			}			
		}
		out.write(this.recordDelimiter);
	}
	
	protected void writeData(ResultSet rs) throws IOException, SQLException 
	{		
		while(rs.next()){
			
			for(int i=1; i<=columnCount; i++){
				out.write(getStringValue(rs, i));
				if(i<columnCount) out.write(this.columnDelimiter);
			}
			out.write(this.recordDelimiter);
		}		
	}

	public boolean isShowHeader() {
		return showHeader;
	}


	public DelimitedSQLResultWriter setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
		return this;
	}


	public boolean isShouldFullHeader() {
		return shouldFullHeader;
	}


	public DelimitedSQLResultWriter setShouldFullHeader(boolean shouldFullHeader) {
		this.shouldFullHeader = shouldFullHeader;
		return this;
	}


	

	public char getColumnDelimiter() {
		return columnDelimiter;
	}


	public DelimitedSQLResultWriter setColumnDelimiter(char columnDelimiter) {
		this.columnDelimiter = columnDelimiter;
		return this;
	}

	public DelimitedSQLResultWriter setColumnDelimiter(String columnDelimiter) {
		this.columnDelimiter = CharUtils.toChar(columnDelimiter);
		return this;
	}
	

	public char getRecordDelimiter() {
		return recordDelimiter;
	}


	public DelimitedSQLResultWriter setRecordDelimiter(char recordDelimiter) {
		this.recordDelimiter = recordDelimiter;
		return this;
	}
	
	
	public DelimitedSQLResultWriter setRecordDelimiter(String recordDelimiter) {
		this.recordDelimiter = CharUtils.toChar(recordDelimiter);
		return this;
	}


	public char getHeaderDelimiter() {
		return headerDelimiter;
	}


	public DelimitedSQLResultWriter setHeaderDelimiter(char headerDelimiter) {
		this.headerDelimiter = headerDelimiter;
		return this;
	}
	
	public DelimitedSQLResultWriter setHeaderDelimiter(String headerDelimiter) {
		this.headerDelimiter = CharUtils.toChar(headerDelimiter);
		return this;
	}
}
