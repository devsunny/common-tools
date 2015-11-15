package com.asksunny.validator;

import com.asksunny.validator.annotation.ValueValidation;

public class WinthinValuesValidator extends ValueValidator {

	public WinthinValuesValidator(Class<?> targetType, Class<?> valueType, String fieldName, ValueValidation fv,
			boolean neg) {
		super(targetType, valueType, fieldName, fv, neg);

	}

	public WinthinValuesValidator(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);

	}

	public WinthinValuesValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);

	}

	public WinthinValuesValidator(String fieldName, ValueValidationRule rule, boolean neg) {
		super(fieldName, rule, neg);

	}

	public WinthinValuesValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);

	}

	@Override
	public ValidationResult validate(Object val) {
		if (getValidationRule().getValues() == null || val == null) {
			return new ValidationResult(getClass().getName(), Boolean.FALSE, getValidationRule().getTargetType(),
					getFieldName(), val, "No comparing rule been specified");
		}
		boolean valid = false;
		for (String cmpVal : getValidationRule().getValues()) {
			valid = valueCompare(cmpVal, val) == 0;
			if (valid) {
				break;
			}
		}
		valid = isNegate() ? !valid : valid;
		return new ValidationResult(getClass().getName(), valid, getValidationRule().getTargetType(), getFieldName(),
				val, valid ? getValidationRule().getSuccessMessage() : getValidationRule().getFailedMessage());

	}

}
