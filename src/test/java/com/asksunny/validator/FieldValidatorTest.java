package com.asksunny.validator;

import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FieldValidatorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws Exception {
		TestChildObject testChild = new TestChildObject("NY", "S02 this is a good message", 16, 21,
				Arrays.asList(new String[] { "socer", "football" }));
		testChild.setId(123456);
		testChild.setBetweenTest(99L);		
		testChild.setK12Age(19);
		testChild.setK5Age(12);
		AnnotatedBeanValidator validator = new AnnotatedBeanValidator();		
		List<ValidationResult> results = validator.validateValues(testChild);
		//assertEquals(3, results.size());
		for (ValidationResult validationResult : results) {
			System.out.println(validationResult);
		}	
				
		
	}

}
