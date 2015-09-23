package com.asksunny.db;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;

import com.asksunny.db.FieldFormatSpec.Justify;

public class FormattedSQLResultWriter extends AbstractSQLResultWriter {

	RecordFormatSpec recordFormatSpec = null;
	FieldFormatSpec[] fieldSpecs = null;
	int formatColumns = 0;
	boolean showHeader = false;
	
	
	public FormattedSQLResultWriter(Writer out) throws IOException {
		super(out);		
	}

	@Override
	public void write(ResultSet rs) throws IOException, SQLException, JSONException  
	{
		super.extractMetadata(rs);
		fieldSpecs = new FieldFormatSpec[recordFormatSpec.getFieldFormatSpecs().size()];
		recordFormatSpec.getFieldFormatSpecs().toArray(fieldSpecs);
		formatColumns = fieldSpecs.length<columnCount?fieldSpecs.length:columnCount;
		writeMetaData();
		writeData(rs);
	}

	protected void writeMetaData() throws IOException, SQLException, JSONException 
	{
		
		if(!showHeader) return;		
		for(int i=0; i<formatColumns; i++){		
			FieldFormatSpec spec = fieldSpecs[i];
			out.write(formatField(spec, columnNames[i]));
			if(i<formatColumns-1 && !spec.isFixedLength())	out.write(spec.getDelimiterChar())	;
		}
		out.write(recordFormatSpec.getDelimiterChar());
	}
	
	protected void writeData(ResultSet rs) throws IOException, SQLException, JSONException 
	{
		
		if(showHeaderOnly) return;		
		while(rs.next()){			
			for(int i=0; i<formatColumns; i++){	
				FieldFormatSpec spec = fieldSpecs[i];
				String value = getStringValue(rs, i+1, spec.getNullVaue());
				out.write(formatField(spec, value));
				if(i<formatColumns-1 && !spec.isFixedLength())	out.write(spec.getDelimiterChar())	;
			}		
			out.write(recordFormatSpec.getDelimiterChar());
		}
	}
	
	
	protected String formatField(FieldFormatSpec spec, String value)
	{
		int len = value.length();
		if(spec.getMinLength()!=-1 && spec.getMaxLength()==spec.getMinLength())
		{			
			if(len>spec.getMaxLength()){
				return value.substring(0, spec.getMaxLength());
			}else if (len<spec.getMaxLength()){
				return pad(spec.getJustify(), value, spec.getPaddingChar(), spec.getMaxLength()-len);
			}else{
				return value;
			}			
		}else if(spec.getMinLength()!=-1 && len< spec.getMinLength()){
			return pad(spec.getJustify(), value, spec.getPaddingChar(), spec.getMinLength()-len);
		}else if(spec.getMaxLength()!=-1 && len > spec.getMinLength()){
			return value.substring(0, spec.getMaxLength());
		}else{
			return value;
		}
	}
	
	
	protected String pad(Justify justify, String val, char padChar, int num)
	{
		StringBuilder buf = new StringBuilder();
		if(justify==Justify.LEFT){
			buf.append(val);
			for(int i=0; i<num; i++) buf.append(padChar);
		}else if(justify==Justify.RIGHT){			
			for(int i=0; i<num; i++) buf.append(padChar);
			buf.append(val);
		}else{
			int r = num /2;
			int l = num - r;
			for(int i=0; i<l; i++) buf.append(padChar);
			buf.append(val);
			for(int i=0; i<r; i++) buf.append(padChar);
		}
		return buf.toString();
	}
	
	
	
	public RecordFormatSpec getRecordFormatSpec() {
		return recordFormatSpec;
	}

	public FormattedSQLResultWriter setRecordFormatSpec(RecordFormatSpec recordFormatSpec) {
		this.recordFormatSpec = recordFormatSpec;
		return this;
	}

	public boolean isShowHeader() {
		return showHeader;
	}

	public FormattedSQLResultWriter setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
		return this;
	}
	
	

}
