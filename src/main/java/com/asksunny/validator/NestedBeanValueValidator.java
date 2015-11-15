package com.asksunny.validator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.asksunny.validator.annotation.ValueValidation;

public class NestedBeanValueValidator extends ValueValidator {

	public NestedBeanValueValidator(Class<?> targetType, Class<?> valueType, String fieldName, ValueValidation fv,
			boolean neg) {
		super(targetType, valueType, fieldName, fv, neg);

	}

	public NestedBeanValueValidator(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);

	}

	public NestedBeanValueValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);

	}

	public NestedBeanValueValidator(String fieldName, ValueValidationRule rule, boolean neg) {
		super(fieldName, rule, neg);

	}

	public NestedBeanValueValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);

	}

	@SuppressWarnings("rawtypes")
	@Override
	public ValidationResult validate(Object value) {
		if (value == null) {
			if (getValidationRule().isNillable()) {
				return new ValidationResult(getClass().getName(), Boolean.TRUE, getValidationRule().getTargetType(),
						getFieldName(), value, "Nullable value and value is null");
			} else {
				return new ValidationResult(getClass().getName(), Boolean.FALSE, getValidationRule().getTargetType(),
						getFieldName(), value, "not Nullable value and value is null");
			}
		}
		boolean valid = Boolean.TRUE;
		if (getValidationRule().getMinSize() > 0 || getValidationRule().getMaxSize() > 0) {
			valid = isValidSize(getValidationRule().getMinSize(), getValidationRule().getMaxSize(), value);
		}
		ValidationResult result = null;
		if (valid) {
			List<ValidationResult> allResults = new ArrayList<>();
			AnnotatedBeanValidator beanValidator = new AnnotatedBeanValidator(isShortCircuit());
			Class<?> cls = value.getClass();
			if (cls.isArray()) {
				int size = Array.getLength(value);
				for (int i = 0; i < size; i++) {
					Object obj = Array.get(value, i);
					List<ValidationResult> rss = beanValidator.validateValues(obj);
					allResults.addAll(rss);
					valid = valid ? calculateResult(rss) : valid;
				}
			} else if (Collection.class.isAssignableFrom(cls)) {
				Collection cl = (Collection) value;
				for (Iterator iter = cl.iterator(); iter.hasNext();) {
					Object obj = iter.next();
					List<ValidationResult> rss = beanValidator.validateValues(obj);
					allResults.addAll(rss);
					valid = valid ? calculateResult(rss) : valid;
				}
			} else if (Map.class.isAssignableFrom(cls)) {
				Map map = (Map) value;
				for (Iterator iter = map.values().iterator(); iter.hasNext();) {
					Object obj = iter.next();
					List<ValidationResult> rss = beanValidator.validateValues(obj);
					allResults.addAll(rss);
					valid = valid ? calculateResult(rss) : valid;
				}
			} else {
				List<ValidationResult> rss = beanValidator.validateValues(value);
				allResults.addAll(rss);
				valid = valid ? calculateResult(rss) : valid;
			}
			NestedBeanValidationResult nrs = new NestedBeanValidationResult(getClass().getName(), valid,
					getValidationRule().getTargetType(), getFieldName(), value,
					valid ? getValidationRule().getSuccessMessage() : getValidationRule().getFailedMessage());
			nrs.setNestedResults(allResults);
			result = nrs;
		} else {
			result = new ValidationResult(getClass().getName(), valid, getValidationRule().getTargetType(),
					getFieldName(), value,
					valid ? getValidationRule().getSuccessMessage() : getValidationRule().getFailedMessage());
		}
		return result;
	}

	protected boolean calculateResult(List<ValidationResult> results) {
		for (ValidationResult validationResult : results) {
			if (validationResult.isSuccess() == false) {
				return false;
			}
		}
		return true;
	}

}
