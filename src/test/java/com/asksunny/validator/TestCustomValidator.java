package com.asksunny.validator;

import com.asksunny.validator.annotation.ValueValidation;

public class TestCustomValidator extends ValueValidator {

	public TestCustomValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);		
	}

	public TestCustomValidator(Class<?> targetType, Class<?> valueType, String fieldName, ValueValidation fv) {
		super(targetType, valueType, fieldName, fv);		
	}

	public TestCustomValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);		
	}

	@Override
	public ValidationResult validate(Object value) {
		System.out.println("All Good:" + value.toString());		
		boolean valid = true;
		return new ValidationResult(getClass().getName(), valid, getValidationRule().getTargetType(), getFieldName(),
				value, valid ? getValidationRule().getSuccessMessage() : getValidationRule().getFailedMessage());
	}

}
