package com.asksunny.validator;

import com.asksunny.validator.annotation.ValueValidation;

public class NoMatchedValidatorFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoMatchedValidatorFoundException(ValueValidationRule rule) {
		super(String.format("No matched validator found exception for %s", rule.toString()));
	}

	public NoMatchedValidatorFoundException(Class<?> targetType, Class<?> valueType, ValueValidation rule) {
		super(String.format("No matched validator found exception for %s", new ValueValidationRule(targetType, valueType, rule)));
	}

}
