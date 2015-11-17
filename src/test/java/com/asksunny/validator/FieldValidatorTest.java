package com.asksunny.validator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		testChild.setTestCustom("If you can see this, you are good");
		Map<String, String> testMap = new HashMap<>();
		testMap.put("KEY1", "123456778A");
		testMap.put("KEY2", "ZXYFGDSA");
		testMap.put("KEY3", "abcdefg");
		testChild.setTestMapValidation(testMap);
		AnnotatedBeanValidator validator = new AnnotatedBeanValidator();
		List<ValidationResult> results = validator.validateValues(testChild);
		org.junit.Assert.assertEquals(11, results.size());
		for (ValidationResult validationResult : results) {
			if (validationResult.getFieldName().equals("testMapValidation")) {
				org.junit.Assert.assertFalse(validationResult.isSuccess());
			}else if (validationResult.getFieldName().equals("betweenTest")) {
				org.junit.Assert.assertFalse(validationResult.isSuccess());
			}else{
				org.junit.Assert.assertTrue(validationResult.isSuccess());
			}
		}
		List<ValidationResult> finalResults =   ValidationResult.flatFailedValidationResults(results);
		org.junit.Assert.assertEquals(2, finalResults.size());
		for (ValidationResult validationResult : finalResults) {
			System.out.println(validationResult);
		}
		
	}

}
