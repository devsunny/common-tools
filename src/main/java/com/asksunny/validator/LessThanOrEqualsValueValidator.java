package com.asksunny.validator;

import com.asksunny.validator.annotation.ValueValidation;

public class LessThanOrEqualsValueValidator extends LessThanValueValidator {

	public LessThanOrEqualsValueValidator(Class<?> targetType, Class<?> fieldType, String fieldName,
			ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);
		setIncludeEquals(true);
	}

	public LessThanOrEqualsValueValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);
		setIncludeEquals(true);
	}

	public LessThanOrEqualsValueValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);
		setIncludeEquals(true);
	}

}
