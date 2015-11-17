package com.asksunny.validator;

import com.asksunny.validator.annotation.ValueValidation;

public class NotEqualsValueValidator extends EqualsValueValidator {

	public NotEqualsValueValidator(Class<?> targetType, Class<?> valueType, String fieldName, ValueValidation fv) {
		super(targetType, valueType, fieldName, fv);
		setNegate(true);
	}

	
	public NotEqualsValueValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);
		setNegate(true);
	}
	

	public NotEqualsValueValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);
		setNegate(true);
	}
	

}
