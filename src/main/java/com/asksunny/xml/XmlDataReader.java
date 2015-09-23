package com.asksunny.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlDataReader extends DefaultHandler {

	private Stack<XmlSelectionNode> selectionPath = new Stack<XmlSelectionNode>();
	private InputSource xmlInput = null;
	private Stack<XmlDataNode> currentPath = new Stack<XmlDataNode>();
	private StringBuilder buf = new StringBuilder();

	private boolean isInSearchPath = false;
	private boolean isDirectSelect = false;
	

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		XmlDataNode node = new XmlDataNode(qName);		
		if(!currentPath.empty()){
			node.putAll(currentPath.peek());
		}		
		currentPath.push(node);
		isInSearchPath = isInSearchPath();
		isDirectSelect = isInSearchPath && (currentPath.size() <= selectionPath.size()); 
		if(isInSearchPath){
			node.setXpath(getCurrentXPath());
		}		
		if(isDirectSelect){
			SelectionType st = selectionPath.get(currentPath.size()-1).getSelectionType();
			if(st==SelectionType.ATTRIBUTE || st==SelectionType.BOTH){
				int c = attributes.getLength();
				for(int i=0; i<c; i++){
					node.put(String.format("%s.%s", node.getXpath(), attributes.getQName(i)), attributes.getValue(i));					
				}
			}
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		currentPath.peek().setElementValue(buf.toString().trim());
		buf.setLength(0);
		isInSearchPath = isInSearchPath();
		isDirectSelect = isInSearchPath && (currentPath.size() <= selectionPath.size()); 		
		if(isInSearchPath && !isDirectSelect){			
			currentPath.get(currentPath.size()-2).put(currentPath.peek().getXpath(), currentPath.peek().getElementValue());			
		}
		if(isInSearchPath && currentPath.size() == selectionPath.size()){
			System.out.println(currentPath.peek());			
		}		
		currentPath.pop();		
	}
	
	protected String getCurrentXPath()
	{
		int csize = currentPath.size();
		StringBuilder pbuf = new StringBuilder();
		for (int i = 0; i < csize; i++) {
			if(pbuf.length()>0) pbuf.append(".");
			pbuf.append(currentPath.get(i).getName());			
		}		
		return pbuf.toString();
	}

	protected boolean isInSearchPath() {
		int csize = currentPath.size();
		int ssize = selectionPath.size();
		if ((csize - ssize) == 1
				&& (selectionPath.peek().getSelectionType() == SelectionType.ELEMENT || selectionPath
						.peek().getSelectionType() == SelectionType.BOTH)) {
			return Boolean.TRUE;
		} else if (csize > ssize) {
			return Boolean.FALSE;
		}
		for (int i = 0; i < csize; i++) {
			if (!currentPath.get(i).getName()
					.equals(selectionPath.get(i).getName())) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}
	
	
	

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		buf.append(ch, start, length);
	}

	@Override
	public void error(SAXParseException e) throws SAXException {

	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {

	}

	public XmlDataReader(String xpath, String xmlContent) {
		this(xpath, new InputSource(new StringReader(xmlContent)));
	}

	public XmlDataReader(String xpath, File xmlFile)
			throws MalformedURLException {
		this(xpath, new InputSource(xmlFile.toURI().toURL().toString()));
	}

	public XmlDataReader(String xpath, Reader xmlReader) {
		this(xpath, new InputSource(xmlReader));
	}

	public XmlDataReader(String xpath, InputStream xmlInputStream) {
		this(xpath, new InputSource(xmlInputStream));
	}

	public XmlDataReader(String xpath, InputSource xmlInput) {
		this.xmlInput = xmlInput;
		String[] xnodes = xpath.replaceFirst("/", "").split("/");
		
		try {
			selectionPath = XPathFilterParser.parseXPath(xpath);
		} catch (ParseException e) {
			throw new InvalidXPathExpression(String.format("Invalid XPath Expression:%s", xpath), e);
		}
		
	}	

	public void parse() throws ParserConfigurationException, SAXException,
			IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(this.xmlInput, this);
	}
	
	public static class InvalidXPathExpression extends RuntimeException
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public InvalidXPathExpression(String message, Throwable cause) {
			super(message, cause);
			
		}

	
		
		
	}

	
	public static enum SelectionType {
		NONE, ATTRIBUTE, ELEMENT, BOTH
	};

	public static enum FilterType {
		EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_EQUAL, LESS_THAN, LESS_THAN_EQUAL, MATCH, NOT_MATCH
	};

	public static enum FilterOperator {
		AND, OR
	};

	public static class NameValuePair {
		public final String name;
		public final String value;

		public NameValuePair(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}

		public static NameValuePair newPair(String name, String value) {
			return new NameValuePair(name, value);
		}
	}

	
	
	public static class Filter {
		private NameValuePair filterTarget;
		private FilterType filterType = FilterType.EQUAL;
		private FilterOperator filterOperator = FilterOperator.AND;
		

		public Filter(NameValuePair filterTarget, FilterType filterType) {
			super();
			this.filterTarget = filterTarget;
			this.filterType = filterType;
		}
		
		public Filter(String name, String value, FilterType filterType) {
			super();
			this.filterTarget = NameValuePair.newPair(name, value);
			this.filterType = filterType;
		}
		
		public Filter(FilterOperator filterOperator) {
			super();
			this.filterOperator = filterOperator;
		}
		
		

		public NameValuePair getFilterTarget() {
			return filterTarget;
		}

		public void setFilterTarget(NameValuePair filterTarget) {
			this.filterTarget = filterTarget;
		}

		public FilterType getFilterType() {
			return filterType;
		}

		public void setFilterType(FilterType filterType) {
			this.filterType = filterType;
		}

		

		public FilterOperator getFilterOperator() {
			return filterOperator;
		}

		public void setFilterOperator(FilterOperator filterOperator) {
			this.filterOperator = filterOperator;
		}

		public String toString()
		{
			return String.format("%s %s %s", getFilterTarget().name, getFilterType(), getFilterTarget().value );
		}


		public boolean match(Map<String, String> nvps) {
			String value = nvps.get(getFilterTarget().name);
			boolean m = false;
			if (this.filterType == FilterType.MATCH) {
				m = value.matches(getFilterTarget().value);
			}else if (this.filterType == FilterType.NOT_MATCH) {
				m = !value.matches(getFilterTarget().value);
			} else {
				BigDecimal d1 = new BigDecimal(getFilterTarget().value);
				BigDecimal d2 = new BigDecimal(value);
				int d = d1.compareTo(d2);
				if (this.filterType == FilterType.GREATER_THAN) {
					m = d > 0;
				} else if (this.filterType == FilterType.GREATER_THAN_EQUAL) {
					m = d >= 0;
				} else if (this.filterType == FilterType.LESS_THAN) {
					m = d < 0;
				} else if (this.filterType == FilterType.LESS_THAN_EQUAL) {
					m = d <= 0;
				} else if (this.filterType == FilterType.NOT_EQUAL) {
					m = d != 0;
				} else {
					m = d == 0;
				}
			}
			return m;
		}

	}
	
	public static class FilterChain extends Filter
	{
		private List<Filter> chainedFilters = new ArrayList<Filter>();
		public FilterChain(FilterOperator filterOperator) {
			super(filterOperator);			
		}
		
		public void addFilter(Filter filter)
		{
			chainedFilters.add(filter);
		}
		
		public boolean match(Map<String, String> nvps) 
		{			
			boolean m = false;
			int size = chainedFilters.size();
			for(int i=0; i<size; i++){
				Filter filter = chainedFilters.get(i);
				m = filter.match(nvps);
				if ( m==false && i< size-1 && chainedFilters.get(i+1).getFilterOperator()==FilterOperator.OR){
					continue;
				}else{
					break;
				}				
			}			
			return m;
		}
		
		public String toString()
		{
			int size = chainedFilters.size();
			StringBuilder buf = new StringBuilder();
			buf.append("(");
			for(int i=0; i<size; i++){
				Filter filter = chainedFilters.get(i);
				if(i!=0){
					buf.append(" ").append(filter.getFilterOperator()).append(" ");
				}
				buf.append(filter.toString());
			}
			buf.append(")");
			return buf.toString();
		}
		
		
	}

	public static class XmlSelectionNode {
		private String name;
		private SelectionType selectionType = SelectionType.NONE;
		private Filter filter = null;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public SelectionType getSelectionType() {
			return selectionType;
		}

		public void setSelectionType(SelectionType selectionType) {
			this.selectionType = selectionType;
		}

		public Filter getFilter() {
			return filter;
		}

		public void setFilter(Filter filter) {
			this.filter = filter;
		}
		
		
	}

	public static class XmlDataNode extends HashMap<String, String> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String name;
		private String elementValue;
		private String xpath;

		public String getXpath() {
			return xpath;
		}

		public void setXpath(String xpath) {
			this.xpath = xpath;
		}

		public XmlDataNode(String name) {
			super();
			this.name = name;
		}

		public String getElementValue() {
			return elementValue;
		}

		public void setElementValue(String elementValue) {
			this.elementValue = elementValue;
		}

		public String getName() {
			return name;
		}

	}

}
