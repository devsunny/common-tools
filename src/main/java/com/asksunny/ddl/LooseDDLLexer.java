package com.asksunny.ddl;

import java.io.IOException;
import java.io.Reader;

import com.asksunny.io.LookaheadReader;

public class LooseDDLLexer {
	private LookaheadReader lhreader = null;

	
	
	
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
			t = new Token(type, lineNumber, colPos);
			readToChar(imgBuff, '\'');	
			t.setImage(imgBuff.toString());				
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
			break;
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
			imgBuff.append((char)i);
			readToPunct(imgBuff);
			t = new Token(type, lineNumber, colPos);
			t.setImage(imgBuff.toString());			
			type = TokenType.resolveTokenType(imgBuff.toString().trim());
			t.setType(type);			
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
