package com.asksunny.validator;

import com.asksunny.validator.annotation.ValueValidation;

public class EqualsValueValidator extends ValueValidator {

	public EqualsValueValidator(Class<?> targetType, Class<?> valueType, String fieldName, ValueValidation fv,
			boolean neg) {
		super(targetType, valueType, fieldName, fv, neg);
	}

	public EqualsValueValidator(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);
	}

	public EqualsValueValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);
	}

	public EqualsValueValidator(String fieldName, ValueValidationRule rule, boolean neg) {
		super(fieldName, rule, neg);
	}

	public EqualsValueValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);
	}

	@Override
	public ValidationResult validate(Object val) {
		if (ValueValidationRule.isBlank(getValidationRule().getValue()) || val == null) {
			return new ValidationResult(getClass().getName(), Boolean.FALSE, getValidationRule().getTargetType(),
					getFieldName(), val, getValidationRule().getFailedMessage());
		}
		boolean valid = valueCompare(getValidationRule().getValue(), val) == 0;		
		valid = isNegate() ? !valid : valid;
		return new ValidationResult(getClass().getName(), valid, getValidationRule().getTargetType(), getFieldName(),
				val, valid ? getValidationRule().getSuccessMessage() : getValidationRule().getFailedMessage());
	}

}
