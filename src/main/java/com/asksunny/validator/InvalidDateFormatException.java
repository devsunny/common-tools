package com.asksunny.validator;

public class InvalidDateFormatException extends RuntimeException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidDateFormatException(String expectedFormat, String actualFormat) {
		super(String.format("Expect date form [%s] but actually getting [%s]", expectedFormat, actualFormat));		
	}

	

}
