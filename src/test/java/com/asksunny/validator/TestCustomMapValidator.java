package com.asksunny.validator;

import java.util.Map;

import com.asksunny.validator.annotation.ValueValidation;

public class TestCustomMapValidator extends ValueValidator {

	public TestCustomMapValidator(Class<?> fieldType, String fieldName, ValueValidation fv) {
		super(fieldType, fieldName, fv);
	}

	public TestCustomMapValidator(Class<?> targetType, Class<?> valueType, String fieldName, ValueValidation fv) {
		super(targetType, valueType, fieldName, fv);
	}

	public TestCustomMapValidator(String fieldName, ValueValidationRule rule) {
		super(fieldName, rule);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ValidationResult validate(Object value) {
		Map<String, String> map = ((Map<String, String>) value);
		String msg = "";
		boolean valid = map.containsKey("KEY1") && map.get("KEY1") != null && map.get("KEY1").matches("^\\d+$");
		msg = (valid)?"ALL good":"KEY1 does not exist or does not have all digits";
		if(valid){
			valid = valid && map.containsKey("KEY2") && map.get("KEY2") != null && map.get("KEY2").matches("^[A-Z]+$");
			msg = (valid)?"ALL good":"KEY2 does not exist or does not have all Upper case letter";
		}
		
		if(valid){
			valid = valid && map.containsKey("KEY3") && map.get("KEY3") != null && map.get("KEY3").matches("^[a-z]+$");
			msg = (valid)?"ALL good":"KEY2 does not exist or does not have all lower case letter";
		}	
		return new ValidationResult(getClass().getName(), valid, getValidationRule().getTargetType(), getFieldName(),
				value, msg);
	}

}
