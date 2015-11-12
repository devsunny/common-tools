package com.asksunny.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class SchemaExtractor {

	public SchemaExtractor() {
	}

	public static void main(String[] args) throws IOException {

		Resource resrc = new Resource();
		Map<String, String> attrs = new HashMap<>();
		attrs.put("jdbcDriverClass", "com.teradata.jdbc.TeraDriver");
		attrs.put("jdbcUrl", "${JDBC.URL}");
		attrs.put("jdbcUser", "${JDBC.USER}");
		attrs.put("jdbcPassword", "${JDBC.PASSWORD}");
		resrc.setResouceName("CCB TERADATA PROD");
		resrc.setResourceType(ResourceType.JDBC);
		resrc.setResourceAttributes(attrs);
		List<SchemaMapping> mappings = new ArrayList<>();
		SchemaMapping sm = SchemaMapping.newInstance("TB1", "SOR1", "ENTITY1");
		mappings.add(sm);
		mappings.add(SchemaMapping.newInstance("TB2", "SOR1", "ENTITY2"));
		mappings.add(SchemaMapping.newInstance("TB3", "SOR2", "ENTITY1"));
		BulkRegistrationConfiguration cfg = BulkRegistrationConfiguration.newInstance().setResource(resrc)
				.setSchemaMappings(mappings);
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectMapper xmlMapper = new XmlMapper();
		String jsonInString = mapper.writeValueAsString(cfg);
		String xmlInString =  xmlMapper.writeValueAsString(cfg);
		System.out.println(jsonInString);
		System.out.println(xmlInString);		
		BulkRegistrationConfiguration cfg2 = mapper.readValue(jsonInString, BulkRegistrationConfiguration.class);
		BulkRegistrationConfiguration cfg3 = xmlMapper.readValue(xmlInString, BulkRegistrationConfiguration.class);
		System.out.println(cfg2.getResource().getResourceType());		
		System.out.println(cfg2.getResource().getResourceAttributes().get("jdbcUrl"));
		System.out.println(cfg3.getResource().getResourceAttributes().get("jdbcUrl"));
		

	}

}
