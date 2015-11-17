package com.asksunny.validator;

import com.asksunny.validator.annotation.ValueValidation;

public class RegExNotContainsValueValidator extends RegExContainsValueValidator {

	public RegExNotContainsValueValidator(Class<?> targetType, Class<?> fieldType, String fieldName,
			ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);
		setNegate(true);
		
	}

	public RegExNotContainsValueValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);
		setNegate(true);
	}

	public RegExNotContainsValueValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);
		setNegate(true);
	}

}
