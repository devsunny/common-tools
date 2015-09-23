package com.asksunny.db;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XmlSQLResultWriter extends AbstractSQLResultWriter {

	XMLOutputFactory factory    =null;
	XMLStreamWriter writer  = null;
	public XmlSQLResultWriter(Writer out) throws IOException, XMLStreamException {
		super(out);
		factory    = XMLOutputFactory.newInstance();
		writer = factory.createXMLStreamWriter(out); 
	}

	@Override
	public void write(ResultSet rs) throws IOException, SQLException, XMLStreamException 
	{
		super.extractMetadata(rs);
		writer.writeStartElement("resultset");
		writer.writeStartDocument();
		writeMetaData();
		writeData(rs);		
		writer.writeEndElement();
		writer.writeEndDocument();
	}
	
	protected void writeMetaData() throws IOException, SQLException, XMLStreamException 
	{
		writer.writeStartElement("metadata");		
		for(int i=0; i<columnCount; i++){
			writer.writeStartElement("column");
			writer.writeAttribute("columnName", columnNames[i]);
			writer.writeAttribute("columnType", String.valueOf(columnTypes[i]));
			writer.writeAttribute("columnSize", String.valueOf(columnSizes[i]));
			writer.writeAttribute("columnTypeName", columnTypeNames[i]);			
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}
	
	protected void writeData(ResultSet rs) throws IOException, SQLException, XMLStreamException 
	{
		if(showHeaderOnly) return;
		writer.writeStartElement("data");		
		while(rs.next()){
			writer.writeStartElement("r");
			for(int i=1; i<=columnCount; i++){
				writer.writeStartElement("c");
				writer.writeCharacters(getStringValue(rs, i));
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}

}
