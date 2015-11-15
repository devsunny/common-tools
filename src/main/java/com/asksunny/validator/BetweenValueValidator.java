package com.asksunny.validator;

import java.util.Collection;
import java.util.Map;

import com.asksunny.validator.annotation.ValueValidation;

public class BetweenValueValidator extends ValueValidator {

	public BetweenValueValidator(Class<?> targetType, Class<?> valueType, String fieldName, ValueValidation fv,
			boolean neg) {
		super(targetType, valueType, fieldName, fv, neg);
	}

	public BetweenValueValidator(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);
	}

	public BetweenValueValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);
	}

	public BetweenValueValidator(String fieldName, ValueValidationRule rule, boolean neg) {
		super(fieldName, rule, neg);
	}

	public BetweenValueValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);
	}

	@Override
	public ValidationResult validate(Object value) {
		boolean valid = false;
		Class<?> clz = value.getClass();
		if (clz.isArray() || CharSequence.class.isAssignableFrom(clz) || Map.class.isAssignableFrom(clz)
				|| Collection.class.isAssignableFrom(clz)) {
			valid = sizeCompare(getValidationRule().getMinSize(), value) <= 0
					&& sizeCompare(getValidationRule().getMaxSize(), value) >= 0;
		} else {
			valid = valueCompare(getValidationRule().getMinValue(), value) <= 0
					&& valueCompare(getValidationRule().getMaxValue(), value) >= 0;
		}
		valid = isNegate() ? !valid : valid;
		return new ValidationResult(getClass().getName(), valid, getValidationRule().getTargetType(), getFieldName(),
				value, valid ? getValidationRule().getSuccessMessage() : getValidationRule().getFailedMessage());
	}

}
