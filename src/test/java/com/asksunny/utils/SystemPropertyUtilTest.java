package com.asksunny.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SystemPropertyUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		System.setProperty("TEST", "TEST1");
		System.setProperty("TEST3", "Hello tehre");
		System.setProperty("TEST2", "TEST2 value with ${TEST3}");
		String abc = SystemPropertyUtil.resolve("TEST ${TEST}");
		assertEquals("TEST TEST1", abc);
		String abc2 = SystemPropertyUtil.resolve("Hello ${TEST2}");
		assertEquals("Hello TEST2 value with Hello tehre", abc2);
	}

}
