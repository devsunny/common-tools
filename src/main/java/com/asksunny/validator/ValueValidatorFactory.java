package com.asksunny.validator;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.asksunny.validator.annotation.ValueValidation;

public final class ValueValidatorFactory {

	private static final Map<String, Class<?>> VALIDATOR_IMPLS = new HashMap<String, Class<?>>();

	static {
		VALIDATOR_IMPLS.put(ValidationOperator.NONE.toString(), MaxMinValueSizeValidator.class);
		VALIDATOR_IMPLS.put(ValidationOperator.JAVA_BEAN.toString(), NestedBeanValueValidator.class);

		VALIDATOR_IMPLS.put(ValidationOperator.EQUALS.toString(), EqualsValueValidator.class);
		VALIDATOR_IMPLS.put(ValidationOperator.NOT_EQUALS.toString(), NotEqualsValueValidator.class);

		VALIDATOR_IMPLS.put(ValidationOperator.WITHIN.toString(), WinthinValuesValidator.class);
		VALIDATOR_IMPLS.put(ValidationOperator.NOT_WITHIN.toString(), NotWinthinValuesValidator.class);

		VALIDATOR_IMPLS.put(ValidationOperator.BETWEEN.toString(), BetweenValueValidator.class);
		VALIDATOR_IMPLS.put(ValidationOperator.NOT_BETWEEN.toString(), NotBetweenValueValidator.class);

		VALIDATOR_IMPLS.put(ValidationOperator.GREATER.toString(), GreaterThanValueValidator.class);
		VALIDATOR_IMPLS.put(ValidationOperator.GREATER_OR_EQUALS.toString(), GreaterThanOrEqualsValueValidator.class);

		VALIDATOR_IMPLS.put(ValidationOperator.LESS.toString(), LessThanValueValidator.class);
		VALIDATOR_IMPLS.put(ValidationOperator.LESS_OR_EQUALS.toString(), LessThanOrEqualsValueValidator.class);

		VALIDATOR_IMPLS.put(ValidationOperator.REGEX_MATCH.toString(), RegExMatchValueValidator.class);
		VALIDATOR_IMPLS.put(ValidationOperator.REGEX_NOT_MATCH.toString(), RegExNotMatchValueValidator.class);

		VALIDATOR_IMPLS.put(ValidationOperator.REGEX_CONTAINS.toString(), RegExContainsValueValidator.class);
		VALIDATOR_IMPLS.put(ValidationOperator.REGEX_NOT_CONTAINS.toString(), RegExNotContainsValueValidator.class);

	}

	private ValueValidatorFactory() {

	}

	public static ValueValidator createValidator(Class<?> targetType, Class<?> valueType, String valueName,
			ValueValidation fv) {
		Class<?> clazz = null;
		if (fv.operator() == ValidationOperator.NONE && fv.custom().length() > 0) {
			try {
				clazz = Class.forName(fv.custom());
			} catch (ClassNotFoundException e) {
				throw new ValidationException(
						String.format("custom ValueValidator class [%s] not found in classpath", fv.custom()), e);
			}
		} else {
			clazz = VALIDATOR_IMPLS.get(fv.operator().toString());
			if (clazz == null) {
				throw new ValidationException(
						String.format("Unsupported Validation Operator[%s]", fv.operator().toString()));
			}
		}
		try {
			@SuppressWarnings("rawtypes")
			Constructor constr = clazz.getConstructor(Class.class, Class.class, String.class, ValueValidation.class);
			Object obj = constr.newInstance(targetType, valueType, valueName, fv);
			return (ValueValidator) obj;
		} catch (Exception ex) {
			StringBuilder buf = new StringBuilder();
			buf.append("Failed to instanciate ValueValidator class[").append(fv.custom()).append("]");
			buf.append("ValueValidator class has to extend com.asksunny.validator.ValueValidator;");
			buf.append(
					"ValueValidator class has to have contructor(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv).");
			throw new ValidationException(buf.toString(), ex);
		}

	}

	public static ValueValidator createValidator(String valueName, ValueValidationRule vvrule) {

		Class<?> clazz = null;
		if (vvrule.getOperator() == ValidationOperator.NONE && vvrule.getCustomValidator().length() > 0) {
			try {
				clazz = Class.forName(vvrule.getCustomValidator());
			} catch (ClassNotFoundException e) {
				throw new ValidationException(String.format("custom ValueValidator class [%s] not found in classpath",
						vvrule.getCustomValidator()), e);
			}
		} else {
			clazz = VALIDATOR_IMPLS.get(vvrule.getOperator().toString());
			if (clazz == null) {
				throw new ValidationException(
						String.format("Unsupported Validation Operator[%s]", vvrule.getOperator().toString()));
			}
		}
		try {
			@SuppressWarnings("rawtypes")
			Constructor constr = clazz.getConstructor(String.class, ValueValidationRule.class);
			Object obj = constr.newInstance(valueName, vvrule);
			return (ValueValidator) obj;
		} catch (Exception ex) {
			StringBuilder buf = new StringBuilder();
			buf.append("Failed to instanciate ValueValidator class[").append(vvrule.getCustomValidator()).append("]");
			buf.append("ValueValidator class has to extend com.asksunny.validator.ValueValidator;");
			buf.append(
					"ValueValidator class has to have contructor(Class<?> targetType, Class<?> fieldType, String fieldName, ValueValidation fv).");
			throw new ValidationException(buf.toString(), ex);
		}

	}

}
