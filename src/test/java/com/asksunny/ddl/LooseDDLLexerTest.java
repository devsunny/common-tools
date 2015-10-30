package com.asksunny.ddl;

import static org.junit.Assert.*;

import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;

public class LooseDDLLexerTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws Exception{
		InputStreamReader ir = new InputStreamReader(getClass().getResourceAsStream("test.ddl.txt"));
		LooseDDLLexer lexer = new LooseDDLLexer(ir);
		
		while(lexer.nextToken()!=null){
			//
		};
		
		ir.close();
	}

}
