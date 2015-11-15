package com.asksunny.validator;

public class ValidationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationException() {
		super("ValidationException");
	}

	public ValidationException(String msg, Throwable t) {
		super(msg, t);
	}

	public ValidationException(Class<?> targetType, Class<?> valueType, String fieldName, ValidationResult result) {
		super(String.format("Validation failed for %s.%s type of %s \nReason: %s \nActual value: %s", targetType.getName(),
				fieldName, valueType.getName(), result.getValidationMessage(), result.getActualValue()));
	}
}
