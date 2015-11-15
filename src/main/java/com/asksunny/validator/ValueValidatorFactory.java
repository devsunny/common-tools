package com.asksunny.validator;

import java.lang.reflect.Field;

import com.asksunny.validator.annotation.ValueValidation;

public final class ValueValidatorFactory {

	private ValueValidatorFactory() {

	}

	public static ValueValidator createValidator(Class<?> targetType, Class<?> valueType, String valueName,  ValueValidation fv) {

		if (fv.operator() == ValidationOperator.NONE) {
			return new MaxMinValueSizeValidator(targetType, valueType, valueName, fv);
		} else if (fv.operator() == ValidationOperator.JAVA_BEAN) {
			return new NestedBeanValueValidator(targetType, valueType, valueName, fv);
		} else if (fv.operator() == ValidationOperator.EQUALS
				|| (fv.operator() == ValidationOperator.WITHIN && fv.value().length == 1)) {
			return new EqualsValueValidator(targetType, valueType, valueName, fv);
		} else if (fv.operator() == ValidationOperator.NOT_EQUALS
				|| (fv.operator() == ValidationOperator.NOT_WITHIN && fv.value().length == 1)) {
			return new WinthinValuesValidator(targetType, valueType, valueName, fv, true);
		} else if (fv.operator() == ValidationOperator.WITHIN || fv.operator() == ValidationOperator.NOT_WITHIN) {
			return new WinthinValuesValidator(targetType, valueType, valueName, fv,
					fv.operator() == ValidationOperator.NOT_WITHIN);
		} else if (fv.operator() == ValidationOperator.BETWEEN || fv.operator() == ValidationOperator.NOT_BETWEEN) {
			return new BetweenValueValidator(targetType, valueType, valueName, fv,
					fv.operator() == ValidationOperator.NOT_BETWEEN);
		} else if (fv.operator() == ValidationOperator.GREATER
				|| fv.operator() == ValidationOperator.GREATER_OR_EQUALS) {
			return new GreaterThanValueValidator(targetType, valueType, valueName, fv,
					fv.operator() == ValidationOperator.GREATER_OR_EQUALS);
		} else if (fv.operator() == ValidationOperator.LESS || fv.operator() == ValidationOperator.LESS_OR_EQUALS) {
			return new LessThanValueValidator(targetType, valueType, valueName, fv,
					fv.operator() == ValidationOperator.LESS_OR_EQUALS);
		} else if (fv.operator() == ValidationOperator.REGEX_MATCH
				|| fv.operator() == ValidationOperator.REGEX_NOT_MATCH) {
			return new RegExMatchValueValidator(targetType, valueType, valueName, fv,
					fv.operator() == ValidationOperator.REGEX_NOT_MATCH);
		} else if (fv.operator() == ValidationOperator.REGEX_CONTAINS
				|| fv.operator() == ValidationOperator.REGEX_NOT_CONTAINS) {
			return new RegExContainsValueValidator(targetType, valueType, valueName, fv,
					fv.operator() == ValidationOperator.REGEX_NOT_CONTAINS);
		} else {
			return new MaxMinValueSizeValidator(targetType, valueType, valueName, fv);
		}
	}

	public static ValueValidator createValidator(String valueName, ValueValidationRule vvrule) {

		if (vvrule.getOperator() == ValidationOperator.NONE) {
			return new MaxMinValueSizeValidator(valueName, vvrule);
		} else if (vvrule.getOperator() == ValidationOperator.JAVA_BEAN) {
			return new NestedBeanValueValidator(valueName, vvrule);
		} else if (vvrule.getOperator() == ValidationOperator.EQUALS
				|| (vvrule.getOperator() == ValidationOperator.WITHIN && vvrule.getValues().length == 1)) {
			return new EqualsValueValidator(valueName, vvrule);
		} else if (vvrule.getOperator() == ValidationOperator.NOT_EQUALS
				|| (vvrule.getOperator() == ValidationOperator.NOT_WITHIN && vvrule.getValues().length == 1)) {
			return new WinthinValuesValidator(valueName, vvrule, true);
		} else if (vvrule.getOperator() == ValidationOperator.WITHIN
				|| vvrule.getOperator() == ValidationOperator.NOT_WITHIN) {
			return new WinthinValuesValidator(valueName, vvrule, vvrule.getOperator() == ValidationOperator.NOT_WITHIN);
		} else if (vvrule.getOperator() == ValidationOperator.BETWEEN
				|| vvrule.getOperator() == ValidationOperator.NOT_BETWEEN) {
			return new BetweenValueValidator(valueName, vvrule, vvrule.getOperator() == ValidationOperator.NOT_BETWEEN);
		} else if (vvrule.getOperator() == ValidationOperator.GREATER
				|| vvrule.getOperator() == ValidationOperator.GREATER_OR_EQUALS) {
			return new GreaterThanValueValidator(valueName, vvrule,
					vvrule.getOperator() == ValidationOperator.GREATER_OR_EQUALS);
		} else if (vvrule.getOperator() == ValidationOperator.LESS
				|| vvrule.getOperator() == ValidationOperator.LESS_OR_EQUALS) {
			return new LessThanValueValidator(valueName, vvrule,
					vvrule.getOperator() == ValidationOperator.LESS_OR_EQUALS);
		} else if (vvrule.getOperator() == ValidationOperator.REGEX_MATCH
				|| vvrule.getOperator() == ValidationOperator.REGEX_NOT_MATCH) {
			return new RegExMatchValueValidator(valueName, vvrule,
					vvrule.getOperator() == ValidationOperator.REGEX_NOT_MATCH);
		} else if (vvrule.getOperator() == ValidationOperator.REGEX_CONTAINS
				|| vvrule.getOperator() == ValidationOperator.REGEX_NOT_CONTAINS) {
			return new RegExContainsValueValidator(valueName, vvrule,
					vvrule.getOperator() == ValidationOperator.REGEX_NOT_CONTAINS);
		} else {
			return new MaxMinValueSizeValidator(valueName, vvrule);
		}
	}

}
