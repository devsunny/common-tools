package com.asksunny.validator;

import java.util.Collection;
import java.util.Map;

import com.asksunny.validator.annotation.ValueValidation;

public class LessThanValueValidator extends ValueValidator {

	private boolean includeEquals = false;

	public LessThanValueValidator(Class<?> targetType, Class<?> valueType, String fieldName, ValueValidation fv,
			boolean neg) {
		super(targetType, valueType, fieldName, fv, false);
		this.includeEquals = neg;
	}

	public LessThanValueValidator(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);
		this.includeEquals = false;
	}

	public LessThanValueValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);
		this.includeEquals = false;
	}

	public LessThanValueValidator(String fieldName, ValueValidationRule rule, boolean neg) {
		super(fieldName, rule, false);
		this.includeEquals = neg;
	}

	public LessThanValueValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);
		this.includeEquals = false;
	}

	@Override
	public ValidationResult validate(Object value) {
		if (getValidationRule().getMaxValue() == null) {
			return new ValidationResult(getClass().getName(), Boolean.TRUE, getValidationRule().getTargetType(), getFieldName(),value,
					getValidationRule().getSuccessMessage());
		}
		int cmpResult = 0;
		Class<?> clz = value.getClass();
		if (clz.isArray() || CharSequence.class.isAssignableFrom(clz) || Map.class.isAssignableFrom(clz)
				|| Collection.class.isAssignableFrom(clz)) {
			cmpResult = sizeCompare(getValidationRule().getMaxSize(), value);
		} else {
			cmpResult = valueCompare(getValidationRule().getMaxValue(), value);
		}
		boolean valid = isIncludeEquals() ? (cmpResult >= 0) : (cmpResult > 0);
		return new ValidationResult(getClass().getName(), valid, getValidationRule().getTargetType(), getFieldName(),
				value, valid ? getValidationRule().getSuccessMessage() : getValidationRule().getFailedMessage());
	}

	public boolean isIncludeEquals() {
		return includeEquals;
	}

	public void setIncludeEquals(boolean includeEquals) {
		this.includeEquals = includeEquals;
	}

}
