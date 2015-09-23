package com.asksunny.data;

public class DoubleFieldSpec extends FieldSpec {
	private int precision;
	private String formatString = "";
	
	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
		if(precision>0){
			formatString = String.format("%%.%df", precision);
		}
	}
	
	public String toString(double value){
		if(precision==0){
			return Double.toString(value);
		}else{
			return String.format(formatString, value);
		}
		
	}

}
