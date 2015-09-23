package com.asksunny.dbdate;

public class DateFormatPart {
	public static enum Type {YY, YYYY, MONTH_MM, MONTH_MON, MONTH_MONTH, DAY, HOUR, MINUTE, SECOND, MILLISECOND, AMPM, SEPARATOR};

	private String value = "";
	private Type type = null;
	
	
	
	
	public DateFormatPart(Type type, String value) {
		this();
		this.value = value;
		this.type = type;
	}
	public DateFormatPart(Type type) {
		this();		
		this.type = type;
	}
	
	public DateFormatPart() {		
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString() {
		return this.value;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	
	
}
