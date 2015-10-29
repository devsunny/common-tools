package com.asksunny.maven;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PomXmlParser extends DefaultHandler {

	private StringBuilder buf = new StringBuilder();
	private Stack<String> elements = new Stack<>();
	private InputSource src = null;
	private String version = null;
	private String groupId = null;
	private String artifactId = null;
	private String parentGroupId = null;
	private String parentVersion = null;
	private String parentArtifactId = null;

	public PomXmlParser(Reader reader) {
		src = new InputSource(reader);
	}

	public PomXmlParser(InputStream in) {
		src = new InputSource(in);
	}

	public PomXmlParser(String xmlText) {
		src = new InputSource(new StringReader(xmlText));
	}

	public PomXmlParser parse() throws ParserConfigurationException, SAXException, IOException {
		System.out.println(elements);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(src, this);
		return this;
	}

	protected boolean isInParent() {

		return (elements.size() >= 2);
	}

	@Override
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {

		buf.append(arg0, arg1, arg2);
	}

	@Override
	public void endElement(String arg0, String arg1, String arg2) throws SAXException {

		if (elements.size() == 3 && elements.get(1).equals("parent")) {
			if (elements.peek().equals("version")) {
				this.parentVersion = buf.toString().trim();
			} else if (elements.peek().equals("artifactId")) {
				this.parentArtifactId = buf.toString().trim();
			} else if (elements.peek().equals("groupId")) {
				this.parentGroupId = buf.toString().trim();
			}

		} else if (elements.size() == 2 && elements.get(0).equals("project")) {
			if (elements.peek().equals("version")) {
				this.version = buf.toString().trim();
			} else if (elements.peek().equals("artifactId")) {
				this.artifactId = buf.toString().trim();
			} else if (elements.peek().equals("groupId")) {
				this.groupId = buf.toString().trim();
			}
		}
		buf.setLength(0);
		elements.pop();
	}

	@Override
	public void startElement(String arg0, String arg1, String qName, Attributes arg3) throws SAXException {
		elements.push(qName);
	}

	public StringBuilder getBuf() {
		return buf;
	}

	public void setBuf(StringBuilder buf) {
		this.buf = buf;
	}

	public Stack<String> getElements() {
		return elements;
	}

	public void setElements(Stack<String> elements) {
		this.elements = elements;
	}

	public InputSource getSrc() {
		return src;
	}

	public void setSrc(InputSource src) {
		this.src = src;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getParentGroupId() {
		return parentGroupId;
	}

	public void setParentGroupId(String parentGroupId) {
		this.parentGroupId = parentGroupId;
	}

	public String getParentVersion() {
		return parentVersion;
	}

	public void setParentVersion(String parentVersion) {
		this.parentVersion = parentVersion;
	}

	public String getParentArtifactId() {
		return parentArtifactId;
	}

	public void setParentArtifactId(String parentArtifactId) {
		this.parentArtifactId = parentArtifactId;
	}

}
