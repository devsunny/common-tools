package com.asksunny.validator;

import com.asksunny.validator.annotation.ValueValidation;

public class GreaterThanOrEqualsValueValidator extends GreaterThanValueValidator {

	public GreaterThanOrEqualsValueValidator(Class<?> targetType, Class<?> fieldType, String fieldName,
			ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);
		setIncludeEquals(true);
	}

	public GreaterThanOrEqualsValueValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);
		setIncludeEquals(true);
	}

	public GreaterThanOrEqualsValueValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);
		setIncludeEquals(true);
	}

}
