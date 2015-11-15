package com.asksunny.validator;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.asksunny.validator.annotation.ValueValidation;

public abstract class ValueValidator {

	private ValueValidationRule validationRule;
	private String fieldName;
	private SimpleDateFormat dateFormat;
	private boolean negate;
	private boolean shortCircuit = false;

	public ValueValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		this(null, fieldType, fieldName, fv, false);
	}

	public ValueValidator(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv) {
		this(targetType, fieldType, fieldName, fv, false);
	}

	public ValueValidator(Class<?> targetType, Class<?> valueType, String fieldName, ValueValidation fv, boolean neg) {
		this.validationRule = new ValueValidationRule(targetType, valueType, fv);
		this.fieldName = fieldName;
		this.dateFormat = new SimpleDateFormat(fv.format());
		this.negate = neg;
	}

	public ValueValidator(String fieldName, ValueValidationRule rule) {
		this(fieldName, rule, false);
	}

	public ValueValidator(String fieldName, ValueValidationRule rule, boolean neg) {
		this.validationRule = rule;
		this.fieldName = fieldName;
		this.dateFormat = new SimpleDateFormat(rule.getDateFormat());
		this.negate = neg;
	}

	public abstract ValidationResult validate(Object value);

	public Class<?> getValueType() {
		return this.validationRule.getValueType();
	}

	public Class<?> getTargetType() {
		return this.validationRule.getTargetType();
	}

	public String getFieldName() {
		return fieldName;
	}

	protected int valueCompare(String val1, Object val2) {
		int cmpResult = 0;
		if (int.class.isAssignableFrom(getValueType()) || Integer.class.isAssignableFrom(getValueType())) {
			cmpResult = Integer.valueOf(val1).compareTo((Integer) val2);
		} else if (long.class.isAssignableFrom(getValueType()) || Long.class.isAssignableFrom(getValueType())) {
			cmpResult = Long.valueOf(val1).compareTo((Long) val2);
		} else if (short.class.isAssignableFrom(getValueType()) || Short.class.isAssignableFrom(getValueType())) {
			cmpResult = Short.valueOf(val1).compareTo((Short) val2);
		} else if (double.class.isAssignableFrom(getValueType()) || Double.class.isAssignableFrom(getValueType())) {
			cmpResult = Double.valueOf(val1).compareTo((Double) val2);
		} else if (float.class.isAssignableFrom(getValueType()) || Float.class.isAssignableFrom(getValueType())) {
			cmpResult = Float.valueOf(val1).compareTo((Float) val2);
		} else if (BigInteger.class.isAssignableFrom(getValueType())) {
			cmpResult = new BigInteger(val1).compareTo((BigInteger) val2);
		} else if (BigDecimal.class.isAssignableFrom(getValueType())) {
			cmpResult = new BigDecimal(val1).compareTo((BigDecimal) val2);
		} else if (Date.class.isAssignableFrom(getValueType())) {
			try {
				cmpResult = getDateFormat().parse(val1).compareTo((Date) val2);
			} catch (ParseException e) {
				throw new InvalidDateFormatException(getValidationRule().getDateFormat(), val1);
			}
		} else if (Calendar.class.isAssignableFrom(getValueType())) {
			try {
				cmpResult = getDateFormat().parse(val1).compareTo(((Calendar) val2).getTime());
			} catch (ParseException e) {
				throw new InvalidDateFormatException(getValidationRule().getDateFormat(), val1);
			}
		} else {
			cmpResult = val1.compareTo(val2.toString());
		}
		return cmpResult;
	}

	@SuppressWarnings("rawtypes")
	protected int sizeCompare(int val1, Object val2) {
		if (List.class.isAssignableFrom(getValueType())) {
			return val1 - ((List) val2).size();
		} else if (Map.class.isAssignableFrom(getValueType())) {
			return val1 - ((Map) val2).size();
		} else if (Collection.class.isAssignableFrom(getValueType())) {
			return val1 - ((Collection) val2).size();
		} else if (CharSequence.class.isAssignableFrom(getValueType())) {
			return val1 - ((CharSequence) val2).length();
		} else if (getValueType().isArray()) {
			return val1 - Array.getLength(val2);
		} else {
			throw new InvalidObjectTypeException(
					"java.util.Map, java.util.Collection, java.lang.CharSequence, java.util.Array");
		}
	}

	@SuppressWarnings("rawtypes")
	protected boolean isValidSize(int min, int max, Object val2) {
		if (val2 == null) {
			return false;
		}
		int size = 0;
		if (List.class.isAssignableFrom(getValueType())) {
			size = ((List) val2).size();
		} else if (Map.class.isAssignableFrom(getValueType())) {
			size = ((Map) val2).size();
		} else if (Collection.class.isAssignableFrom(getValueType())) {
			size = ((Collection) val2).size();
		} else if (CharSequence.class.isAssignableFrom(getValueType())) {
			size = ((CharSequence) val2).length();
		} else if (getValueType().isArray()) {
			size = Array.getLength(val2);
		} else {
			size = 1;
		}
		boolean valid = (min > 0) ? size >= min : Boolean.TRUE;
		valid = (max > 0) ? size <= max && valid : valid;
		return valid;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public boolean isNegate() {
		return negate;
	}

	public void setNegate(boolean negate) {
		this.negate = negate;
	}

	public ValueValidationRule getValidationRule() {
		return validationRule;
	}

	public void setValidationRule(ValueValidationRule validationRule) {
		this.validationRule = validationRule;
	}

	public boolean isShortCircuit() {
		return shortCircuit;
	}

	public void setShortCircuit(boolean shortCircuit) {
		this.shortCircuit = shortCircuit;
	}

}
