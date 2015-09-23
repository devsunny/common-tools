package com.asksunny.db;

public class CharUtils {

	public static char toChar(String delimiter)
	{
		if(delimiter.length()==1){
			return delimiter.charAt(0);
		}else if(delimiter.toLowerCase().startsWith("0x") && delimiter.length()==4){
			return (char)Integer.parseInt(delimiter.substring(2), 16);
		}else if(delimiter.toLowerCase().startsWith("\\0") && delimiter.length()==4){
			return (char)Integer.parseInt(delimiter.substring(2));			
		}else if(Character.isDigit(delimiter.charAt(0))){
			return (char)Integer.valueOf(delimiter).intValue();
		}else{
			throw new IllegalArgumentException("Unrecognized delimiter format:" + delimiter);
		}
	}
	
	
	private CharUtils() {
		
	}

}
