package com.asksunny.validator;

public class InvalidObjectTypeException extends RuntimeException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidObjectTypeException(String types) {
		super(String.format("Only the following types are supported[%s]", types));		
	}

	

}
