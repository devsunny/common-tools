package com.asksunny.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.asksunny.validator.annotation.ValueValidation;

public class AnnotatedBeanValidator {

	private boolean shortCircuit = false;
	private ValidationException error = null;

	public AnnotatedBeanValidator(boolean shortCircuit) {
		this.shortCircuit = shortCircuit;
	}

	public AnnotatedBeanValidator() {
		this(false);
	}

	public boolean validate(Object objInstance) throws ValidationException {
		return checkState(validateValues(objInstance));
	}

	public List<ValidationResult> validateValues(Object objInstance) throws ValidationException {
		if (objInstance == null) {
			throw new NullPointerException("Validator cannot validate NULL");
		}
		List<ValidationResult> results = new ArrayList<ValidationResult>();
		try {
			Field[] fields = getAllFields(objInstance.getClass(), new ArrayList<Field>(128)).toArray(new Field[0]);
			for (Field field : fields) {
				ValueValidation fv = field.getAnnotation(ValueValidation.class);
				if (fv == null) {
					continue;
				}
				Object obj = PropertyUtils.getProperty(objInstance, field.getName());
				validateValue(fv, objInstance.getClass(), field.getType(), field.getName(), obj, results);
			}

			Method[] methods = objInstance.getClass().getMethods();
			for (Method method : methods) {
				ValueValidation fv = method.getAnnotation(ValueValidation.class);
				if (fv == null || method.getParameters().length > 0) {
					continue;
				}
				Object obj = method.invoke(objInstance, new Object[0]);
				validateValue(fv, objInstance.getClass(), method.getReturnType(), method.getName(), obj, results);
			}
		} catch (Exception e) {
			throw new ValidationException("Failed to validate", e);
		}
		return results;
	}

	protected void validateValue(ValueValidation vvanno, Class<?> targetType, Class<?> valueType, String fieldName,
			Object value, List<ValidationResult> results) {

		if (value == null) {
			if (vvanno.notNull()) {
				results.add(new ValidationResult("NullableValidator", Boolean.FALSE, targetType, fieldName, null,
						vvanno.failedMessage()));
			} else {
				results.add(new ValidationResult("NullableValidator", Boolean.TRUE, targetType, fieldName, null,
						"Nullable field with null value"));
			}

		} else {
			ValueValidator validator = ValueValidatorFactory.createValidator(targetType, valueType, fieldName, vvanno);
			if (validator != null) {
				validator.setShortCircuit(isShortCircuit());
				ValidationResult result = validator.validate(value);
				results.add(result);
				if (this.shortCircuit && result.isSuccess() == false) {
					throw new ValidationException(targetType, valueType, fieldName, result);
				}
			} else {
				throw new NoMatchedValidatorFoundException(targetType, valueType, vvanno);
			}

		}

	}

	protected boolean checkState(List<ValidationResult> results) {
		for (ValidationResult validationResult : results) {
			if (validationResult.isSuccess() == false) {
				return false;
			}
		}
		return true;
	}

	protected List<Field> getAllFields(Class<?> type, List<Field> fields) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));
		if (type.getSuperclass() != null) {
			fields = getAllFields(type.getSuperclass(), fields);
		}
		return fields;
	}

	public boolean isShortCircuit() {
		return shortCircuit;
	}

	public void setShortCircuit(boolean shortCircuit) {
		this.shortCircuit = shortCircuit;
	}

	public ValidationException getError() {
		return error;
	}

	public void setError(ValidationException error) {
		this.error = error;
	}

}
