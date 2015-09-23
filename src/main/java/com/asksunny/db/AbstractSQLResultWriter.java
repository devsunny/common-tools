package com.asksunny.db;

import java.io.IOException;
import java.io.Writer;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;

public abstract class AbstractSQLResultWriter extends Writer implements
		SQLResultWriter {

	Writer out = null;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
	SimpleDateFormat timestampFormat = null;
	
	String nullValue = "";
	String[] columnNames = null;
	int columnCount = 0;
	int[] columnTypes = null;
	int[] columnSizes = null;
	int[] columnScales = null;
	int[] columnPrecisions = null;

	String[] columnTypeNames = null;
	String[]  tableNames = null;
	String[]  schemaNames = null;
	String[] catalogNames = null;
	
	boolean showHeaderOnly;
	
	

	public AbstractSQLResultWriter(Writer out) throws IOException {
		this.out = out;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		out.write(cbuf, off, len);

	}

	protected void extractMetadata(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		this.columnCount = rsmd.getColumnCount();
		columnNames = new String[columnCount];
		columnTypes = new int[columnCount];
		columnSizes = new int[columnCount];
		columnTypeNames = new String[columnCount];
		columnScales = new int[columnCount];
		columnPrecisions = new int[columnCount];
		tableNames = new String[columnCount];
		schemaNames = new String[columnCount];
		catalogNames = new String[columnCount];

		for (int colIdx = 1; colIdx <= columnCount; colIdx++) {
			columnNames[colIdx - 1] = rsmd.getColumnName(colIdx);
			columnTypes[colIdx - 1] = rsmd.getColumnType(colIdx);
			columnSizes[colIdx - 1] = rsmd.getColumnDisplaySize(colIdx);
			columnTypeNames[colIdx - 1] = rsmd.getColumnTypeName(colIdx);
			columnScales[colIdx - 1] = rsmd.getScale(colIdx);
			columnPrecisions[colIdx - 1] = rsmd.getPrecision(colIdx);
			tableNames[colIdx - 1] = rsmd.getTableName(colIdx);;
			schemaNames[colIdx - 1] = rsmd.getSchemaName(colIdx);
			catalogNames[colIdx - 1] = rsmd.getCatalogName(colIdx);
		}
	}
	
	protected String getStringValue(ResultSet rs, int colIdx, String null_val) throws SQLException
	{
		int type = this.columnTypes[colIdx-1];
		String ret = null_val==null?nullValue:null_val;
		switch(type)
		{
		case Types.DATE:
			Date d = rs.getDate(colIdx);
			if(d!=null){
				ret = dateFormat.format(d);
			}
			break;
		case Types.TIME:
			Date t = rs.getDate(colIdx);
			if(t!=null){
				ret = timeFormat.format(t);
			}
			break;
		case Types.TIMESTAMP:
			Date ts = rs.getDate(colIdx);
			if(ts!=null){
				if(timestampFormat!=null){
					ret = timestampFormat.format(ts);
				}else{
					//this is default javascript timestamp value
					ret = Long.toString(ts.getTime());
				}
			}
			break;
		default:
			ret = rs.getString(colIdx);
			if(ret==null) ret = null_val;
			break;
		}
		return ret;
	}
	
	protected String getStringValue(ResultSet rs, int colIdx) throws SQLException
	{		
		return getStringValue( rs,  colIdx,  nullValue);
	}
	
	
	

	public String getNullValue() {
		return nullValue;
	}

	public AbstractSQLResultWriter setNullValue(String nullValue) {
		this.nullValue = nullValue;
		return this;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}
	
	public AbstractSQLResultWriter setDateFormat(String dateFormat) {
		this.dateFormat = new SimpleDateFormat(dateFormat);
		return this;
	}

	public AbstractSQLResultWriter setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
		return this;
	}

	public SimpleDateFormat getTimeFormat() {
		return timeFormat;
	}
	
	public AbstractSQLResultWriter setTimeFormat(String timeFormat) {
		this.timeFormat =  new SimpleDateFormat(timeFormat);
		return this;
	}

	public AbstractSQLResultWriter setTimeFormat(SimpleDateFormat timeFormat) {
		this.timeFormat = timeFormat;
		return this;
	}

	public SimpleDateFormat getTimestampFormat() {
		return timestampFormat;
	}

	public AbstractSQLResultWriter setTimestampFormat(SimpleDateFormat timestampFormat) {
		this.timestampFormat = timestampFormat;
		return this;
	}
	
	public AbstractSQLResultWriter setTimestampFormat(String timestampFormat) {
		this.timestampFormat = new SimpleDateFormat(timestampFormat);
		return this;
	}

	@Override
	public void flush() throws IOException {
		out.flush();

	}

	@Override
	public void close() throws IOException {
		out.close();

	}
	
	public boolean isShowHeaderOnly() {
		return showHeaderOnly;
	}


	public AbstractSQLResultWriter setShowHeaderOnly(boolean showHeaderOnly) {
		this.showHeaderOnly = showHeaderOnly;
		return this;
	}

}
