package com.asksunny.tool;

import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;

public class ObjectTranslator {

	
	
	public void translate(Class<?> obj)
	{
		
		Method[] methods = obj.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			System.out.println(methods[i].getName());
		}		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ObjectTranslator translator = new ObjectTranslator();		
		translator.translate(DatabaseMetaData.class);
	}

}
