package com.asksunny.validator;

import com.asksunny.validator.annotation.ValueValidation;

public class NotWinthinValuesValidator extends WinthinValuesValidator {

	public NotWinthinValuesValidator(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);
		setNegate(true);
	}

	public NotWinthinValuesValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);
		setNegate(true);
	}

	public NotWinthinValuesValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);
		setNegate(true);
	}

}
