package com.asksunny.validator;

import java.util.ArrayList;
import java.util.List;

public class NestedBeanValidationResult extends ValidationResult {

	private List<ValidationResult> nestedResults;

	public NestedBeanValidationResult() {
	}

	public NestedBeanValidationResult(String validatorName, boolean success, Class<?> className, String fieldName,
			Object actualValue, String validationMessage) {
		super(validatorName, success, className, fieldName, actualValue, validationMessage);
	}

	public List<ValidationResult> getNestedResults() {
		return nestedResults;
	}

	public void setNestedResults(List<ValidationResult> nestedResults) {
		this.nestedResults = nestedResults;
	}
	
	@Override
	public List<ValidationResult> flat() {
		 List<ValidationResult> flatResults = new ArrayList<ValidationResult>();
		 if(nestedResults!=null && nestedResults.size()>0){
			for(ValidationResult vrs: nestedResults){
				flatResults.addAll(vrs.flat());
			}
		 }		 
		 return flatResults;
	}	

}
