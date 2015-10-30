package com.asksunny.ddl;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.asksunny.io.LookaheadReader;

public class LooseDDLLexer {
	private LookaheadReader lhreader = null;

	public static final int CREATE = 1;
	public static final int OR = 2;
	public static final int REPLACE = 3;
	public static final int ALTER = 3;

	public static final int DROP = 4;
	public static final int TABLE = 5;

	public static final int ADD = 6;
	public static final int CONSTRAINT = 7;
	public static final int FOREIGN = 8;

	public static final int REFERENCES = 9;
	public static final int NOT = 10;
	public static final int NULL = 11;
	public static final int PRIMAY = 12;
	public static final int KEY = 13;
	public static final int IDENTITY = 14;
	public static final int AUTO_INCREMENT = 15;
	public static final int AUTOINCREMENT = 15;
	public static final int DEFAULT = 17;

	public static final int LPAREN = 20;
	public static final int RPAREN = 21;
	public static final int COMMA = 22;
	public static final int DOUBLE_QUOTE = 23;
	public static final int SINGLE_QUOTE = 24;
	public static final int LBRACK = 25;
	public static final int RBRACK = 26;

	public static final int INT = 31;
	public static final int INTEGER = 31;
	public static final int TINYINT = 33;
	public static final int SMALLINT = 34;
	public static final int BIGINT = 35;
	public static final int LONG = 36;

	public static final int DOUBLE = 41;
	public static final int FLOAT = 42;
	public static final int REAL = 43;
	public static final int NUMBER = 44;
	public static final int DECIMAL = 45;

	public static final int VARCHAR = 51;
	public static final int VARCHAR2 = 51;
	public static final int CHAR = 53;
	public static final int CHARACTER = 53;

	public static final int DATE = 61;
	public static final int TIME = 62;
	public static final int TIMESTAMP = 63;

	public static Map<String, Integer> tokens = new HashMap<>();

	static {
		tokens.put("INT", INT);
		tokens.put("INTEGER", INTEGER);
		tokens.put("TINYINT", TINYINT);
		tokens.put("SMALLINT", SMALLINT);
		tokens.put("BIGINT", BIGINT);
		tokens.put("LONG", LONG);
		tokens.put("DOUBLE", DOUBLE);
		tokens.put("FLOAT", FLOAT);
		tokens.put("REAL", REAL);
		tokens.put("NUMBER", NUMBER);
		tokens.put("DECIMAL", DECIMAL);
		tokens.put("VARCHAR", VARCHAR);
		tokens.put("VARCHAR2", VARCHAR2);
		tokens.put("CHAR", CHAR);
		tokens.put("CHARACTER", CHARACTER);
		tokens.put("DATE", DATE);
		tokens.put("TIME", TIME);
		tokens.put("TIMESTAMP", TIMESTAMP);
		tokens.put("DEFAULT", DEFAULT);
		tokens.put("'", SINGLE_QUOTE);
		tokens.put("(", LPAREN);
		tokens.put(")", RPAREN);
		tokens.put(",", COMMA);
		tokens.put("\"", DOUBLE_QUOTE);
		tokens.put("[", LBRACK);
		tokens.put("]", RBRACK);
		tokens.put("CREATE", CREATE);
		tokens.put("OR", OR);
		tokens.put("REPLACE", REPLACE);
		tokens.put("TABLE", TABLE);
		tokens.put("DROP", DROP);
		tokens.put("ADD", ADD);
		tokens.put("FOREIGN", FOREIGN);
		tokens.put("CONSTRAINT", CONSTRAINT);
		tokens.put("REFERENCES", REFERENCES);
		tokens.put("NOT", NOT);
		tokens.put("NULL", NULL);
		tokens.put("KEY", KEY);
		tokens.put("AUTO_INCREMENT", AUTO_INCREMENT);
		tokens.put("AUTOINCREMENT", AUTOINCREMENT);
	}
	
	private long colPos = 0;
	private long lineNumber = 1;
	private long tokenIndex = 0;

	public LooseDDLLexer(Reader reader) throws IOException {
		lhreader = new LookaheadReader(1, reader);
	}

	public Token nextToken() throws IOException {
		int i = lhreader.read();
		if (i == -1) {
			return null;
		}
		colPos++;
		Token t = null;
		StringBuilder imgBuff = new StringBuilder();
		TokenType type = null;
		switch (i) {
		case -1:
			return null;
		case '"':
			type = TokenType.IDENTIFIER;
			t = new Token(type, lineNumber, colPos);
			readToChar(imgBuff, '"');
			t.setImage(imgBuff.toString());			
			break;
		case '[':
			type = TokenType.IDENTIFIER;
			t = new Token(type, lineNumber, colPos);
			readToChar(imgBuff, ']');
			t.setImage(imgBuff.toString());		
			break;
		case '\'':
			type = TokenType.STRING_LITERAL;
			readToChar(imgBuff, '\'');			
			break;
		
		case '\n':
			type = TokenType.IGNOREABLE;
			t = new Token(type, lineNumber, colPos);
			imgBuff.append((char)i);
			lineNumber++;
			colPos=0;			
			readToNonWS(imgBuff);
			t.setImage(imgBuff.toString());	
			break;
		case ' ':			
		case '\t':			
		case '\r':
			type = TokenType.IGNOREABLE;
			t = new Token(type, lineNumber, colPos);
			imgBuff.append((char)i);					
			readToNonWS(imgBuff);
			t.setImage(imgBuff.toString());	
		case ',':			
			type = TokenType.COMMA;
			imgBuff.append((char)i);
			t = new Token(type, lineNumber, colPos);
			t.setImage(imgBuff.toString());
			break;		
		case '(':
			type = TokenType.LPAREN;
			imgBuff.append((char)i);
			t = new Token(type, lineNumber, colPos);
			t.setImage(imgBuff.toString());
			break;
		case ')':
			type = TokenType.RPAREN;
			imgBuff.append((char)i);
			t = new Token(type, lineNumber, colPos);
			t.setImage(imgBuff.toString());
			break;
		case ';':
			type = TokenType.SEMICOLON;
			imgBuff.append((char)i);
			t = new Token(type, lineNumber, colPos);
			t.setImage(imgBuff.toString());
			break;
		default:
			type = TokenType.OTHER;
			imgBuff.append((char)i);
			readToPunct(imgBuff);
			t = new Token(type, lineNumber, colPos);
			t.setImage(imgBuff.toString());
			
			
			
			
			break;
		}
		if(t!=null){
			t.setTokenIndex(tokenIndex++);			
		}		
		imgBuff.setLength(0);
		return t;

	}
	
	protected void readToPunct(StringBuilder imgBuff) throws IOException
	{
		int i = -1;
		while((i=lhreader.peek(0))!=-1){			
			char c = (char)i;			
			if(Character.isJavaIdentifierPart(c) || c=='$'){				
				lhreader.read();
				colPos++;
				imgBuff.append(c);
			}else{
				break;
			}
		}
	}
	
	protected void readToNonWS(StringBuilder imgBuff) throws IOException
	{
		int i = -1;		
		while((i=lhreader.peek(0))!=-1){			
			char c = (char)i;			
			if(Character.isWhitespace(c)){				
				lhreader.read();				
				if(c=='\n'){
					lineNumber++;
					colPos=0;
				}else{
					colPos++;
				}
				imgBuff.append(c);
			}else{
				break;
			}
		}
	}
	
	
	protected void readToChar(StringBuilder imgBuff, char cmpChar) throws IOException
	{
		int i = -1;
		while((i=lhreader.peek(0))!=-1){			
			char c = (char)i;			
			if(cmpChar != c){				
				lhreader.read();
				colPos++;
				imgBuff.append(c);
			}else{
				lhreader.read();
				colPos++;
				break;
			}
		}
	}
	
	
	
	

}
