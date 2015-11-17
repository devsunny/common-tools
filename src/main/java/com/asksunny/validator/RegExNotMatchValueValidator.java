package com.asksunny.validator;

import com.asksunny.validator.annotation.ValueValidation;

public class RegExNotMatchValueValidator extends RegExMatchValueValidator {

	public RegExNotMatchValueValidator(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);
		setNegate(true);
	}

	public RegExNotMatchValueValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);
		setNegate(true);
	}

	public RegExNotMatchValueValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);
		setNegate(true);
	}

}
