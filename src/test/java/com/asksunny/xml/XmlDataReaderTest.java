package com.asksunny.xml;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XmlDataReaderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParse() throws ParserConfigurationException, SAXException, IOException {
		new XmlDataReader("/ROOT/GRID@/CLUSTER/METRIC*", getClass().getResourceAsStream("/Test.xml")).parse();
	}

}
