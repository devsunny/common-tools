package com.asksunny.validator;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ValueValidatorNestedTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		TestParentObject obj = new TestParentObject();
		TestChildObject testChild = new TestChildObject("NY", "S02 this is a good message", 16, 21,
				Arrays.asList(new String[] { "socer", "football" }));
		testChild.setId(123456);
		testChild.setBetweenTest(99L);
		testChild.setK12Age(19);
		testChild.setK5Age(12);
		obj.setChildObj(testChild);
		AnnotatedBeanValidator validator = new AnnotatedBeanValidator();
		List<ValidationResult> results = validator.validateValues(obj);
		for (ValidationResult validationResult : results) {
			System.out.println(validationResult);
		}
		assertFalse(validator.validate(obj));
		List<ValidationResult> failed = ValidationResult.flatFailedValidationResults(results);
		assertEquals(1, failed.size());
		for (ValidationResult validationResult : failed) {
			System.out.println(validationResult);
		}
		List<ValidationResult> allrs = ValidationResult.flatValidationResults(results);
		assertEquals(10, allrs.size());
		for (ValidationResult validationResult : allrs) {
			System.out.println(validationResult);
		}

	}

	@Test(expected = ValidationException.class)
	public void testShortCircuit() {
		TestParentObject obj = new TestParentObject();
		TestChildObject testChild = new TestChildObject("NY", "S02 this is a good message", 16, 21,
				Arrays.asList(new String[] { "socer", "football" }));
		testChild.setId(123456);
		testChild.setBetweenTest(99L);
		testChild.setK12Age(19);
		testChild.setK5Age(12);
		obj.setChildObj(testChild);
		AnnotatedBeanValidator validator = new AnnotatedBeanValidator(true);
		try {
			validator.validate(obj);
		} catch (ValidationException vex) {
			vex.printStackTrace();
			throw vex;
		}

	}

}
