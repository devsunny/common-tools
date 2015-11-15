package com.asksunny.validator;

import com.asksunny.validator.annotation.ValueValidation;

public class MaxMinValueSizeValidator extends ValueValidator {

	public MaxMinValueSizeValidator(Class<?> targetType, Class<?> valueType, String fieldName, ValueValidation fv,
			boolean neg) {
		super(targetType, valueType, fieldName, fv, neg);
	}

	public MaxMinValueSizeValidator(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);

	}

	public MaxMinValueSizeValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);

	}

	public MaxMinValueSizeValidator(String fieldName, ValueValidationRule rule, boolean neg) {
		super(fieldName, rule, neg);

	}

	public MaxMinValueSizeValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);

	}

	@Override
	public ValidationResult validate(Object value) {
		boolean valid = Boolean.TRUE;
		if ((getValidationRule().getMinSize() > 0 || getValidationRule().getMaxSize() > 0)) {
			valid = isValidSize(getValidationRule().getMinSize(), getValidationRule().getMaxSize() , value);
		}
		return new ValidationResult(getClass().getName(), valid, getValidationRule().getTargetType(), getFieldName(),
				value, valid ? getValidationRule().getSuccessMessage() : getValidationRule().getFailedMessage());
	}

}
