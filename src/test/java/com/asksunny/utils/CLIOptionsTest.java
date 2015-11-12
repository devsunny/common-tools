package com.asksunny.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CLIOptionsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		CLIOptions opts = new CLIOptions(new String[]{"-key1", "val1", "-Dtest.p1=test123345", "-D", "-key2", "val2", "-D", "another.on=12345"});
		assertEquals("test123345", System.getProperty("test.p1"));
		assertEquals("12345", System.getProperty("another.on"));
		assertEquals("val1", opts.getOption("key1"));
		assertEquals("val2", opts.getOption("key2"));
	}

}
