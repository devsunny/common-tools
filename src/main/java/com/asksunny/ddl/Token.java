package com.asksunny.ddl;

public class Token {

	private long lineNumber = 1;
	private long columnNumber = 1;
	private String image = "";
	private long tokenIndex = 0;
	private TokenType type = TokenType.OTHER;

	public Token(TokenType type, long lineNumber, long columnNumber, String image, long tokenIndex) {
		super();

		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
		this.image = image;
		this.tokenIndex = tokenIndex;
	}

	public Token(TokenType type) {
		this.type = type;
	}

	public Token(TokenType type, long lineNumber, long columnNumber) {
		super();
		this.type = type;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	public Token(long lineNumber, long columnNumber, String image, long tokenIndex) {
		super();
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
		this.image = image;
		this.tokenIndex = tokenIndex;
	}

	public Token(long lineNumber, long columnNumber, String image) {
		super();
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
		this.image = image;
	}

	public long getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}

	public long getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(long columnNumber) {
		this.columnNumber = columnNumber;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public long getTokenIndex() {
		return tokenIndex;
	}

	public void setTokenIndex(long tokenIndex) {
		this.tokenIndex = tokenIndex;
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

}
