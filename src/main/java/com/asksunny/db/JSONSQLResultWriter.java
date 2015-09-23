package com.asksunny.db;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONWriter;

public class JSONSQLResultWriter extends AbstractSQLResultWriter {

	JSONWriter jsonOut = null;
	public JSONSQLResultWriter(Writer out) throws IOException {
		super(out);	
		jsonOut = new JSONWriter(out);
	}

	
	@Override
	public void write(ResultSet rs) throws IOException, SQLException, JSONException 
	{
		
		super.extractMetadata(rs);
		jsonOut.object();
		writeMetaData();
		writeData(rs);			
		jsonOut.endObject();
	}
	
	
	protected void writeMetaData() throws IOException, SQLException, JSONException 
	{
		jsonOut.key("metadata");
		jsonOut.array();
		for(int i=0; i<columnCount; i++){
			jsonOut.object();
			jsonOut.key("columnName").value(columnNames[i]);
			jsonOut.key("columnType").value(columnTypes[i]);
			jsonOut.key("columnSize").value(columnSizes[i]);
			jsonOut.key("columnTypeName").value(columnTypeNames[i]);
			jsonOut.endObject();
		}
		jsonOut.endArray();
	}
	
	
	protected void writeData(ResultSet rs) throws IOException, SQLException, JSONException 
	{
		if(showHeaderOnly) return;
		jsonOut.key("data");
		jsonOut.array();
		while(rs.next()){
			jsonOut.array();
			for(int i=1; i<=columnCount; i++){
				jsonOut.value(getStringValue(rs, i));
			}
			jsonOut.endArray();
		}
		jsonOut.endArray();
	}
	

}
