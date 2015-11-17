package com.asksunny.validator;

import com.asksunny.validator.annotation.ValueValidation;

public class NotBetweenValueValidator extends BetweenValueValidator {

	public NotBetweenValueValidator(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);
		setNegate(true);
	}

	public NotBetweenValueValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);
		setNegate(true);
	}

	public NotBetweenValueValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);
		setNegate(true);
	}

}
