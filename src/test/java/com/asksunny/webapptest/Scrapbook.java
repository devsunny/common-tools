package com.asksunny.webapptest;

import java.io.OutputStreamWriter;
import java.io.Writer;

import org.json.JSONWriter;

public class Scrapbook {

	public Scrapbook() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		
		
		for(int i=32; i<127; i++){
			System.out.println(String.format("'%1$c',", i));
		}
		
		for(int i=16; i<21; i++){
			System.out.println(String.format("(char)%1$d,", i));
		}
		
		int j = 0; 		
		for(int i=32; i<127; i++){
			System.out.println(String.format("ALPHA_NUMERICS_POS_MAP.put(%1$d, %2$d);", i, j++));
		}
		for(int i=16; i<21; i++){
			System.out.println(String.format("ALPHA_NUMERICS_POS_MAP.put(%1$d, %2$d);", i, j++));
		}
		
		
		
		
	}

}
