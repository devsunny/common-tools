package com.asksunny.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.asksunny.validator.annotation.ValueValidation;

public class RegExMatchValueValidator extends ValueValidator {

	private Pattern[] paterns = null;

	public RegExMatchValueValidator(Class<?> targetType, Class<?> valueType, String fieldName, ValueValidation fv,
			boolean neg) {
		super(targetType, valueType, fieldName, fv, neg);
		initPattern();
	}

	public RegExMatchValueValidator(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(targetType, fieldType, fieldName, fv);
		initPattern();
	}

	public RegExMatchValueValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);
		initPattern();
	}

	public RegExMatchValueValidator(String fieldName, ValueValidationRule rule, boolean neg) {
		super(fieldName, rule, neg);
		initPattern();
	}

	public RegExMatchValueValidator(String fieldName, ValueValidationRule rule) {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
			valid = allMatch(Arrays.asList(ar));
		} else if (List.class.isAssignableFrom(clz)) {
			valid = allMatch((List) value);
		} else if (Collection.class.isAssignableFrom(clz)) {
			valid = allMatch(new ArrayList((Collection) value));
		} else {
			for (int i = 0; i < paterns.length; i++) {
				valid = paterns[i].matcher(value.toString()).matches();
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
	public boolean allMatch(List objs) {
		boolean ma = true;
		for (Object object : objs) {
			String val = object.toString();
			boolean m = false;
			for (int i = 0; i < paterns.length; i++) {
				m = paterns[i].matcher(val).matches();
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
