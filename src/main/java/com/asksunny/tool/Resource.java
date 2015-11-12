package com.asksunny.tool;

import java.util.Map;

public class Resource {

	private String resouceName;
	private ResourceType resourceType;
	private Map<String, String> resourceAttributes;

	public String getResouceName() {
		return resouceName;
	}

	public Resource setResouceName(String resouceName) {
		this.resouceName = resouceName;
		return this;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public Resource setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
		return this;
	}

	public Map<String, String> getResourceAttributes() {
		return resourceAttributes;
	}

	public Resource setResourceAttributes(Map<String, String> resourceAttributes) {
		this.resourceAttributes = resourceAttributes;
		return this;
	}

}
