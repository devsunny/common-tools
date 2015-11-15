package com.asksunny.validator;

import com.asksunny.validator.annotation.ValueValidation;

public class TestParentObject {

	@ValueValidation(operator = ValidationOperator.JAVA_BEAN, notNull = true)
	private TestChildObject childObj = null;

	public TestParentObject() {

	}

	public TestChildObject getChildObj() {
		return childObj;
	}

	public void setChildObj(TestChildObject childObj) {
		this.childObj = childObj;
	}

	@ValueValidation(operator = ValidationOperator.NONE, minSize = 2, maxSize = 100)
	public String getName() {
		return "Hello there";
	}

}
