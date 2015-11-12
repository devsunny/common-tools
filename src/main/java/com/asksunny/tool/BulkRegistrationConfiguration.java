package com.asksunny.tool;

import java.util.List;

public class BulkRegistrationConfiguration {

	private Resource resource;

	private List<SchemaMapping> schemaMappings;

	public BulkRegistrationConfiguration() {

	}
	
	public static BulkRegistrationConfiguration newInstance()
	{
		return new BulkRegistrationConfiguration();
	}

	public Resource getResource() {
		return resource;
	}

	public BulkRegistrationConfiguration setResource(Resource resource) {
		this.resource = resource;
		return this;
	}

	public List<SchemaMapping> getSchemaMappings() {
		return schemaMappings;
	}

	public BulkRegistrationConfiguration setSchemaMappings(List<SchemaMapping> schemaMappings) {
		this.schemaMappings = schemaMappings;
		return this;
	}

}
