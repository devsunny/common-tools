package com.asksunny.db;

public class FieldFormatSpec {

	public static enum Justify {LEFT, RIGHT, CENTER};
	
	char delimiterChar = '|';
	int minLength = -1;
	int maxLength = -1;
	char paddingChar = ' ';
	Justify justify = Justify.RIGHT;
	String nullVaue = null;
	
	
	public String getNullVaue() {
		return nullVaue;
	}

	public void setNullVaue(String nullVaue) {
		this.nullVaue = nullVaue;
	}

	public boolean isFixedLength()
	{
		return minLength!=-1 && minLength==maxLength;
	}
	
	public char getDelimiterChar() {
		return delimiterChar;
	}
	public FieldFormatSpec setDelimiterChar(char delimiterChar) {
		this.delimiterChar = delimiterChar;
		return this;
	}
	
	public FieldFormatSpec setDelimiterChar(String delimiterChar) {
		this.delimiterChar = CharUtils.toChar(delimiterChar);
		return this;
	}
	public int getMinLength() {
		return minLength;
	}
	public FieldFormatSpec setMinLength(int minLength) {
		this.minLength = minLength;
		return this;
	}
	public int getMaxLength() {
		return maxLength;
	}
	public FieldFormatSpec setMaxLength(int maxLength) {
		this.maxLength = maxLength;
		return this;
	}
	public char getPaddingChar() {
		return paddingChar;
	}
	public FieldFormatSpec setPaddingChar(char paddingChar) {
		this.paddingChar = paddingChar;
		return this;
	}
	
	public FieldFormatSpec setPaddingChar(String paddingChar) {
		this.paddingChar = CharUtils.toChar(paddingChar);
		return this;
	}
	
	
	public Justify getJustify() {
		return justify;
	}
	public FieldFormatSpec setJustify(Justify justify) {
		this.justify = justify;
		return this;
	}
	
	
	
}
