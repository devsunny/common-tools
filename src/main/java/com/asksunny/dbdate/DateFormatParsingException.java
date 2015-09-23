package com.asksunny.dbdate;

public class DateFormatParsingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DateFormatParsingException() {
		
	}

	public DateFormatParsingException(String message) {
		super(message);
		
	}

	public DateFormatParsingException(Throwable cause) {
		super(cause);
	
	}

	public DateFormatParsingException(String message, Throwable cause) {
		super(message, cause);
		
	}

	

}
