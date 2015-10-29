package com.asksunny.io;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

public class LookaheadReaderTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test3() throws IOException {
		LookaheadReader lreader = new LookaheadReader(3, new StringReader("ABCDEFGHIJK"));		
		assertTrue(lreader.lookaheadMatch("ABC".toCharArray(), false));
		char[] testb = new char[4];
		int rlen = lreader.read(testb, 0, 4);
		assertEquals(4, rlen);
		assertArrayEquals("ABCD".toCharArray(), testb);
		
		assertTrue(lreader.lookaheadMatch("EFG".toCharArray(), false));
		assertTrue(lreader.lookaheadMatch("efg".toCharArray(), true));
		assertTrue(lreader.lookaheadMatch("eF".toCharArray(), true));
		
		char[] testb2 = new char[8];
		int rlen2 = lreader.read(testb2, 0, 8);
		assertEquals(7, rlen2);
		assertArrayEquals("EFGHIJK".toCharArray(), new String(testb2, 0, 7).toCharArray());	
		
		lreader.close();
	}
	
	@Test
	public void test1() throws IOException {
		LookaheadReader lreader = new LookaheadReader(1, new StringReader("ABCDEFGHIJK"));		
		assertTrue(lreader.lookaheadMatch("A".toCharArray(), false));
		char[] testb = new char[4];
		int rlen = lreader.read(testb, 0, 4);
		assertEquals(4, rlen);
		assertArrayEquals("ABCD".toCharArray(), testb);		
		
		char[] testb2 = new char[8];
		int rlen2 = lreader.read(testb2, 0, 8);
		assertEquals(7, rlen2);
		assertArrayEquals("EFGHIJK".toCharArray(), new String(testb2, 0, 7).toCharArray());
		
		lreader.close();
	}
	
	

	@Test
	public void test2() throws IOException {
		LookaheadReader lreader = new LookaheadReader(1, new StringReader("ABCDEFGHIJK"));		
		char c1 = (char)-1;
		int x = c1;
		System.out.println(x);
		char[] ar = "ABCDEFGHIJK".toCharArray();
		
		for(int i=0; i<"ABCDEFGHIJK".length(); i++ ){
			int c = lreader.read();
			assertEquals(ar[i], c);			
		}
		assertEquals(-1, lreader.read());	
		lreader.close();
	}

}
