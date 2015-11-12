package com.asksunny.io;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

public class FileValidatorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws Exception {
		
		URL url = getClass().getResource("mytest.txt");
		File path = new File(url.toURI());		
		FileValidator validator = new FileValidator(path.toString(), "C:\\sunny\\ITpubBook\\itpubforum\\downloaded");
		validator.doValidation();
		
	}

}
