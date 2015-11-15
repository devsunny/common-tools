package com.asksunny.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.asksunny.validator.annotation.ValueValidation;

public class RegExContainsValueValidator extends ValueValidator {

	private Pattern[] paterns = null;

	public RegExContainsValueValidator(Class<?> targetType, Class<?> valueType, String fieldName, ValueValidation fv,
			boolean neg) {
		super(targetType, valueType, fieldName, fv, neg);
		initPattern();
	}

	public RegExContainsValueValidator(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);
		initPattern();
	}

	public RegExContainsValueValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);
		initPattern();
	}

	public RegExContainsValueValidator(String fieldName, ValueValidationRule rule, boolean neg) {
		super(fieldName, rule, neg);
		initPattern();
	}

	public RegExContainsValueValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);
		initPattern();
	}

	protected void initPattern() {
		String[] vals = getValidationRule().getValues();
		if (vals != null && vals.length > 0) {
			paterns = new Pattern[vals.length];
			for (int i = 0; i < paterns.length; i++) {
				paterns[i] = Pattern.compile(vals[i]);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ValidationResult validate(Object value) {
		if (paterns == null || paterns.length == 0) {
			return new ValidationResult(getClass().getName(), Boolean.TRUE, getValidationRule().getTargetType(),
					getFieldName(), value, "No validation pattern been specified");
		}
		boolean valid = false;
		Class<?> clz = value.getClass();
		if (clz.isArray()) {
			Object[] ar = (Object[]) value;
			valid = allContains(Arrays.asList(ar));
		} else if (List.class.isAssignableFrom(clz)) {
			valid = allContains((List) value);
		} else if (Collection.class.isAssignableFrom(clz)) {
			valid = allContains(new ArrayList((Collection) value));
		} else {
			for (int i = 0; i < paterns.length; i++) {
				valid = paterns[i].matcher(value.toString()).find();
				if (valid) {
					break;
				}
			}
		}
		valid = isNegate() ? !valid : valid;
		return new ValidationResult(getClass().getName(), valid, getValidationRule().getTargetType(), getFieldName(), value,
				valid ? getValidationRule().getSuccessMessage() : getValidationRule().getFailedMessage());
	}

	@SuppressWarnings("rawtypes")
	public boolean allContains(List objs) {
		boolean ma = true;
		for (Object object : objs) {
			String val = object.toString();
			if (getValidationRule().getMinSize() > 0 || getValidationRule().getMaxSize() > 0) {
				if (!isValidSize(getValidationRule().getMinSize(), getValidationRule().getMaxSize(), val)) {
					return false;
				}
			}
			boolean m = false;
			for (int i = 0; i < paterns.length; i++) {
				m = paterns[i].matcher(val).find();
				if (m) {
					break;
				}
			}
			ma = m;
			if (!ma) {
				break;
			}
		}
		return ma;
	}
}
