package com.asksunny.ddl;

import java.util.HashMap;
import java.util.Map;

public enum TokenType {

	CREATE(1), OR(2), REPLACE(81), ALTER(3), DROP(4), TABLE(5), ADD(6), CONSTRAINT(7), FOREIGN(8), REFERENCES(9), NOT(
			10), NULL(11), PRIMAY(12), KEY(13), IDENTITY(14), AUTO_INCREMENT(15), AUTOINCREMENT(15), DEFAULT(
					17), LPAREN(20), RPAREN(21), COMMA(22), DOUBLE_QUOTE(23), SINGLE_QUOTE(24), LBRACK(25), RBRACK(
							26), INT(31), INTEGER(31), TINYINT(33), SMALLINT(34), BIGINT(35), LONG(36), DOUBLE(
									41), FLOAT(42), REAL(43), NUMBER(44), DECIMAL(45), VARCHAR(51), VARCHAR2(51), CHAR(
											53), CHARACTER(53), DATE(61), TIME(62), TIMESTAMP(63), IDENTIFIER(
													88), OTHER(99), STRING_LITERAL(101), INT_LITERAL(
															102), DOUBLE_LITERAL(103), IGNOREABLE(999), SEMICOLON(27);

	public static final int TKN_CREATE_VAL = 1;
	public static final int TKN_OR_VAL = 2;
	public static final int TKN_REPLACE_VAL = 81;
	public static final int TKN_ALTER_VAL = 3;

	public static final int TKN_DROP_VAL = 4;
	public static final int TKN_TABLE_VAL = 5;

	public static final int TKN_ADD_VAL = 6;
	public static final int TKN_CONSTRAINT_VAL = 7;
	public static final int TKN_FOREIGN_VAL = 8;

	public static final int TKN_REFERENCES_VAL = 9;
	public static final int TKN_NOT_VAL = 10;
	public static final int TKN_NULL_VAL = 11;
	public static final int TKN_PRIMAY_VAL = 12;
	public static final int TKN_KEY_VAL = 13;
	public static final int TKN_IDENTITY_VAL = 14;
	public static final int TKN_AUTO_INCREMENT_VAL = 15;
	public static final int TKN_AUTOINCREMENT_VAL = 15;
	public static final int TKN_DEFAULT_VAL = 17;

	public static final int TKN_LPAREN_VAL = 20;
	public static final int TKN_RPAREN_VAL = 21;
	public static final int TKN_COMMA_VAL = 22;
	public static final int TKN_DOUBLE_QUOTE_VAL = 23;
	public static final int TKN_SINGLE_QUOTE_VAL = 24;
	public static final int TKN_LBRACK_VAL = 25;
	public static final int TKN_RBRACK_VAL = 26;
	public static final int TKN_SEMICOLON_VAL = 27;

	public static final int TKN_INT_VAL = 31;
	public static final int TKN_INTEGER_VAL = 31;
	public static final int TKN_TINYINT_VAL = 33;
	public static final int TKN_SMALLINT_VAL = 34;
	public static final int TKN_BIGINT_VAL = 35;
	public static final int TKN_LONG_VAL = 36;

	public static final int TKN_DOUBLE_VAL = 41;
	public static final int TKN_FLOAT_VAL = 42;
	public static final int TKN_REAL_VAL = 43;
	public static final int TKN_NUMBER_VAL = 44;
	public static final int TKN_DECIMAL_VAL = 45;

	public static final int TKN_VARCHAR_VAL = 51;
	public static final int TKN_VARCHAR2_VAL = 51;
	public static final int TKN_CHAR_VAL = 53;
	public static final int TKN_CHARACTER_VAL = 53;

	public static final int TKN_DATE_VAL = 61;
	public static final int TKN_TIME_VAL = 62;
	public static final int TKN_TIMESTAMP_VAL = 63;

	public static final int TKN_IDENTIFIER_VAL = 88;
	public static final int TKN_OTHER_VAL = 99;

	private static Map<String, Integer> tokens = new HashMap<>();

	static {
		tokens.put("INT", TKN_INT_VAL);
		tokens.put("INTEGER", TKN_INTEGER_VAL);
		tokens.put("TINYINT", TKN_TINYINT_VAL);
		tokens.put("SMALLINT", TKN_SMALLINT_VAL);
		tokens.put("BIGINT", TKN_BIGINT_VAL);
		tokens.put("LONG", TKN_LONG_VAL);
		tokens.put("DOUBLE", TKN_DOUBLE_VAL);
		tokens.put("FLOAT", TKN_FLOAT_VAL);
		tokens.put("REAL", TKN_REAL_VAL);
		tokens.put("NUMBER", TKN_NUMBER_VAL);
		tokens.put("DECIMAL", TKN_DECIMAL_VAL);
		tokens.put("VARCHAR", TKN_VARCHAR_VAL);
		tokens.put("VARCHAR2", TKN_VARCHAR2_VAL);
		tokens.put("CHAR", TKN_CHAR_VAL);
		tokens.put("CHARACTER", TKN_CHARACTER_VAL);
		tokens.put("DATE", TKN_DATE_VAL);
		tokens.put("TIME", TKN_TIME_VAL);
		tokens.put("TIMESTAMP", TKN_TIMESTAMP_VAL);
		tokens.put("DEFAULT", TKN_DEFAULT_VAL);
		tokens.put("'", TKN_SINGLE_QUOTE_VAL);
		tokens.put("(", TKN_LPAREN_VAL);
		tokens.put(")", TKN_RPAREN_VAL);
		tokens.put(",", TKN_COMMA_VAL);
		tokens.put("\"", TKN_DOUBLE_QUOTE_VAL);
		tokens.put("[", TKN_LBRACK_VAL);
		tokens.put("]", TKN_RBRACK_VAL);
		tokens.put(";", TKN_SEMICOLON_VAL);
		tokens.put("CREATE", TKN_CREATE_VAL);
		tokens.put("OR", TKN_OR_VAL);
		tokens.put("REPLACE", TKN_REPLACE_VAL);
		tokens.put("TABLE", TKN_TABLE_VAL);
		tokens.put("DROP", TKN_DROP_VAL);
		tokens.put("ADD", TKN_ADD_VAL);
		tokens.put("FOREIGN", TKN_FOREIGN_VAL);
		tokens.put("CONSTRAINT", TKN_CONSTRAINT_VAL);
		tokens.put("REFERENCES", TKN_REFERENCES_VAL);
		tokens.put("NOT", TKN_NOT_VAL);
		tokens.put("NULL", TKN_NULL_VAL);
		tokens.put("KEY", TKN_KEY_VAL);
		tokens.put("AUTO_INCREMENT", TKN_AUTO_INCREMENT_VAL);
		tokens.put("AUTOINCREMENT", TKN_AUTOINCREMENT_VAL);
	}

	private int intValue = TKN_OTHER_VAL;

	private TokenType(int val) {
		this.intValue = val;
	}

	public static TokenType resolveTokenType(String value) {
		Integer t = tokens.get(value.toUpperCase());
		if (t != null) {
			return getTokenType(t);
		} else if (value.matches("^\\d+$")) {
			return TokenType.INT_LITERAL;
		} else if (value.matches("^(\\d*)?\\.\\d+$")) {
			return TokenType.DOUBLE_LITERAL;
		} else {
			return TokenType.OTHER;
		}

	}

	public static TokenType getTokenType(int val) {
		switch (val) {
		case TKN_CREATE_VAL:
			return CREATE;
		case TKN_OR_VAL:
			return OR;
		case TKN_REPLACE_VAL:
			return REPLACE;
		case TKN_ALTER_VAL:
			return ALTER;
		case TKN_DROP_VAL:
			return DROP;
		case TKN_TABLE_VAL:
			return TABLE;
		case TKN_ADD_VAL:
			return ADD;
		case TKN_CONSTRAINT_VAL:
			return CONSTRAINT;
		case TKN_FOREIGN_VAL:
			return FOREIGN;
		case TKN_REFERENCES_VAL:
			return REFERENCES;
		case TKN_NOT_VAL:
			return NOT;
		case TKN_NULL_VAL:
			return NULL;
		case TKN_PRIMAY_VAL:
			return PRIMAY;
		case TKN_KEY_VAL:
			return KEY;
		case TKN_IDENTITY_VAL:
			return IDENTITY;
		case TKN_AUTO_INCREMENT_VAL:
			return AUTO_INCREMENT;
		case TKN_DEFAULT_VAL:
			return DEFAULT;
		case TKN_LPAREN_VAL:
			return LPAREN;
		case TKN_RPAREN_VAL:
			return RPAREN;
		case TKN_COMMA_VAL:
			return COMMA;
		case TKN_DOUBLE_QUOTE_VAL:
			return DOUBLE_QUOTE;
		case TKN_SINGLE_QUOTE_VAL:
			return SINGLE_QUOTE;
		case TKN_LBRACK_VAL:
			return LBRACK;
		case TKN_RBRACK_VAL:
			return RBRACK;
		case TKN_SEMICOLON_VAL:
			return SEMICOLON;
		case TKN_INT_VAL:
			return INT;
		case TKN_TINYINT_VAL:
			return TINYINT;
		case TKN_SMALLINT_VAL:
			return SMALLINT;
		case TKN_BIGINT_VAL:
			return BIGINT;
		case TKN_LONG_VAL:
			return LONG;
		case TKN_DOUBLE_VAL:
			return DOUBLE;
		case TKN_FLOAT_VAL:
			return FLOAT;
		case TKN_REAL_VAL:
			return REAL;
		case TKN_NUMBER_VAL:
			return NUMBER;
		case TKN_DECIMAL_VAL:
			return DECIMAL;
		case TKN_VARCHAR_VAL:
			return VARCHAR;
		case TKN_CHAR_VAL:
			return CHAR;
		case TKN_DATE_VAL:
			return DATE;
		case TKN_TIME_VAL:
			return TIME;
		case TKN_TIMESTAMP_VAL:
			return TIMESTAMP;
		case TKN_IDENTIFIER_VAL:
			return IDENTIFIER;
		}
		return OTHER;
	}

	public int getIntValue() {
		return this.intValue;
	}

}
