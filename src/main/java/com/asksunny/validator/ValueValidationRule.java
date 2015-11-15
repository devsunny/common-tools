package com.asksunny.validator;

import java.util.Arrays;

import com.asksunny.validator.annotation.ValueValidation;

public class ValueValidationRule {

	public static final String EMPTY_STRING = "";
	private boolean nillable = Boolean.TRUE;
	private String[] values = new String[0];
	private String maxValue = EMPTY_STRING;
	private String minValue = EMPTY_STRING;
	private int minSize = 0;
	private int maxSize = 0;
	private ValidationOperator operator = ValidationOperator.NONE;
	private String successMessage = EMPTY_STRING;
	private String failedMessage = EMPTY_STRING;
	private Class<?> targetType = null;
	private Class<?> valueType = null;
	private String dateFormat = "yyyy-MM-dd";

	public ValueValidationRule(Class<?> targetType, Class<?> valueType, boolean nillable, ValidationOperator operator,
			String[] values, String minValue, String maxValue, int minSize, int maxSize, String successMessage,
			String failedMessage) {
		super();
		this.targetType = targetType;
		this.valueType = valueType;
		this.nillable = nillable;
		this.operator = operator;
		this.values = values;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.successMessage = successMessage;
		this.failedMessage = failedMessage;
	}

	public ValueValidationRule(Class<?> targetType, Class<?> valueType, ValueValidation fvv) {
		this.targetType = targetType;
		this.valueType = valueType;
		this.nillable = !fvv.notNull();
		this.operator = fvv.operator();
		this.values = fvv.value();
		this.minValue = fvv.minValue();
		this.maxValue = fvv.maxValue();
		this.minSize = fvv.minSize();
		this.maxSize = fvv.maxSize();
		this.successMessage = fvv.successMessage();
		this.failedMessage = fvv.failedMessage();
	}

	public void setValue(String value) {
		this.values = new String[] { value };
	}

	public String getValue() {
		if (this.values != null && this.values.length > 0 && !isBlank(this.values[0])) {
			return this.values[0];
		} else if (!isBlank(this.minValue)) {
			return this.minValue;
		} else if (!isBlank(this.maxValue)) {
			return this.maxValue;
		} else {
			return EMPTY_STRING;
		}

	}

	public static boolean isBlank(String text) {
		return text == null || text.length() == 0;
	}

	public ValueValidationRule() {
	}

	public boolean isNillable() {
		return nillable;
	}

	public void setNillable(boolean nillable) {
		this.nillable = nillable;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public String getMaxValue() {
		if (!isBlank(this.maxValue)) {
			return this.maxValue;
		} else if (this.values != null && this.values.length > 0 && !isBlank(this.values[0])) {
			return this.values[0];
		} else {
			return null;
		}
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public String getMinValue() {
		if (!isBlank(this.minValue)) {
			return this.minValue;
		} else if (this.values != null && this.values.length > 0 && !isBlank(this.values[0])) {
			return this.values[0];
		} else {
			return null;
		}
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public ValidationOperator getOperator() {
		return operator;
	}

	public void setOperator(ValidationOperator operator) {
		this.operator = operator;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public String getFailedMessage() {
		return failedMessage;
	}

	public void setFailedMessage(String failedMessage) {
		this.failedMessage = failedMessage;
	}

	public Class<?> getTargetType() {
		return targetType;
	}

	public void setTargetType(Class<?> targetType) {
		this.targetType = targetType;
	}

	public Class<?> getValueType() {
		return valueType;
	}

	public void setValueType(Class<?> valueType) {
		this.valueType = valueType;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public String toString() {
		return "ValueValidationRule [targetType=" + targetType.getName() + ", valueType=" + valueType.getName()
				+ ", nillable=" + nillable + ", operator=" + operator + ", values=" + Arrays.toString(values)
				+ ", maxValue=" + maxValue + ", minValue=" + minValue + ", minSize=" + minSize + ", maxSize=" + maxSize
				+ ", getValue()=" + getValue() + ", isNillable()=" + isNillable() + ", getValues()="
				+ Arrays.toString(getValues()) + ", getMaxValue()=" + getMaxValue() + ", getMinValue()=" + getMinValue()
				+ ", getMinSize()=" + getMinSize() + ", getMaxSize()=" + getMaxSize() + ", getOperator()="
				+ getOperator() + ", getSuccessMessage()=" + getSuccessMessage() + ", getFailedMessage()="
				+ getFailedMessage() + ", getTargetType()=" + getTargetType() + ", getValueType()=" + getValueType()
				+ ", getDateFormat()=" + getDateFormat() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

}
