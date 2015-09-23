package com.asksunny.crypto;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class CharacterRank {

	public final static char[] ALPHA_NUMERICS = new char[] { '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1',
			'2', '3', '4', '5', '6', '7' };

	public static final HashMap<Character, Integer> ALPHA_NUMERIC_MAP = new HashMap<Character, Integer>();
	static {
		ALPHA_NUMERIC_MAP.put('0', 0);
		ALPHA_NUMERIC_MAP.put('1', 1);
		ALPHA_NUMERIC_MAP.put('2', 2);
		ALPHA_NUMERIC_MAP.put('3', 3);
		ALPHA_NUMERIC_MAP.put('4', 4);
		ALPHA_NUMERIC_MAP.put('5', 5);
		ALPHA_NUMERIC_MAP.put('6', 6);
		ALPHA_NUMERIC_MAP.put('7', 7);
		ALPHA_NUMERIC_MAP.put('8', 8);
		ALPHA_NUMERIC_MAP.put('9', 9);
		ALPHA_NUMERIC_MAP.put('A', 10);
		ALPHA_NUMERIC_MAP.put('B', 11);
		ALPHA_NUMERIC_MAP.put('C', 12);
		ALPHA_NUMERIC_MAP.put('D', 13);
		ALPHA_NUMERIC_MAP.put('E', 14);
		ALPHA_NUMERIC_MAP.put('F', 15);
		ALPHA_NUMERIC_MAP.put('G', 16);
		ALPHA_NUMERIC_MAP.put('H', 17);
		ALPHA_NUMERIC_MAP.put('I', 18);
		ALPHA_NUMERIC_MAP.put('J', 19);
		ALPHA_NUMERIC_MAP.put('K', 20);
		ALPHA_NUMERIC_MAP.put('L', 21);
		ALPHA_NUMERIC_MAP.put('M', 22);
		ALPHA_NUMERIC_MAP.put('N', 23);
		ALPHA_NUMERIC_MAP.put('O', 24);
		ALPHA_NUMERIC_MAP.put('P', 25);
		ALPHA_NUMERIC_MAP.put('Q', 26);
		ALPHA_NUMERIC_MAP.put('R', 27);
		ALPHA_NUMERIC_MAP.put('S', 28);
		ALPHA_NUMERIC_MAP.put('T', 29);
		ALPHA_NUMERIC_MAP.put('U', 30);
		ALPHA_NUMERIC_MAP.put('V', 31);
		ALPHA_NUMERIC_MAP.put('W', 32);
		ALPHA_NUMERIC_MAP.put('X', 33);
		ALPHA_NUMERIC_MAP.put('Y', 34);
		ALPHA_NUMERIC_MAP.put('Z', 35);
		ALPHA_NUMERIC_MAP.put('a', 36);
		ALPHA_NUMERIC_MAP.put('b', 37);
		ALPHA_NUMERIC_MAP.put('c', 38);
		ALPHA_NUMERIC_MAP.put('d', 39);
		ALPHA_NUMERIC_MAP.put('e', 40);
		ALPHA_NUMERIC_MAP.put('f', 41);
		ALPHA_NUMERIC_MAP.put('g', 42);
		ALPHA_NUMERIC_MAP.put('h', 43);
		ALPHA_NUMERIC_MAP.put('i', 44);
		ALPHA_NUMERIC_MAP.put('j', 45);
		ALPHA_NUMERIC_MAP.put('k', 46);
		ALPHA_NUMERIC_MAP.put('l', 47);
		ALPHA_NUMERIC_MAP.put('m', 48);
		ALPHA_NUMERIC_MAP.put('n', 49);
		ALPHA_NUMERIC_MAP.put('o', 50);
		ALPHA_NUMERIC_MAP.put('p', 51);
		ALPHA_NUMERIC_MAP.put('q', 52);
		ALPHA_NUMERIC_MAP.put('r', 53);
		ALPHA_NUMERIC_MAP.put('s', 54);
		ALPHA_NUMERIC_MAP.put('t', 55);
		ALPHA_NUMERIC_MAP.put('u', 56);
		ALPHA_NUMERIC_MAP.put('v', 57);
		ALPHA_NUMERIC_MAP.put('w', 58);
		ALPHA_NUMERIC_MAP.put('x', 59);
		ALPHA_NUMERIC_MAP.put('y', 60);
		ALPHA_NUMERIC_MAP.put('z', 61);
	}
	
	
	protected static long toLong(String char_string, int radix,
			HashMap<Character, Integer> char_index_map) {
		int len = char_string.length();
		long val = 0;
		long base = 0;
		for (int i = len - 1, j = 0; i >= 0; i--, j++) {
			char c = char_string.charAt(i);			
			int idx = char_index_map.get(c);			
			
			if (j == 0) {
				base = 0;
				val = idx;
			} else if (j == 1) {
				base = radix;
				val += idx * base;
			} else {
				base = base * radix;
				val += idx * base;
			}
			System.out.println(String.format("m=%d val=%d c=%c", idx, val, c));
		}
		return val;
	}

	protected static  String toCharString(long val, int radix, char[] char_array) {
		
		System.out.println("LONG   IN<<" + val);
		StringBuilder buf = new StringBuilder();
		long l = val;
		while (l > 0) {
			long m = l % radix;			
			l = l / radix;			
			char c = char_array[(int) m];
			System.out.println(String.format("m=%d l=%d c=%c", m, l, c));
			buf.append(c);
		}
		String ret = buf.reverse().toString();
		System.out.println("CHAR  OUT<<" + ret);
		return ret;
	}
	
	
	public CharacterRank() 
	{
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SecureRandom rand = new SecureRandom(new Date().toString().getBytes());
		
		String NUMERIC = "0123456789";
		ArrayList<Character> numchars = new ArrayList<Character>();		
		int len1 = NUMERIC.length();
		for (int i = 0; i < len1; i++) {
			numchars.add(NUMERIC.charAt(i));
		}	
		
		System.out.println("public final static int[][] NUMERIC_MATRIX = {");
		for (int i = 0; i < len1 * 10; i++) {
			Collections.shuffle(numchars, rand);
			System.out.print("{");
			for (int j = 0; j < len1; j++) {					
				System.out.print(String.format("%c", numchars.get(j)));
				if(j<len1-1){
					System.out.print(",");
				}else{
					
				}
			}	
			System.out.print("}");
			if(i<(len1 * 10)-1){
				System.out.println(",");
			}else{
				System.out.println("");
			}
		}	
		System.out.println("};");
		
		
		
		String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		ArrayList<Character> chars = new ArrayList<Character>();		
		int len = ALPHA_NUMERIC.length();
		
		for (int i = 0; i < len; i++) {
			chars.add(ALPHA_NUMERIC.charAt(i));	
			System.out.println(String.format("ALPHA_NUMERIC_MAP.put('%c', %d);", ALPHA_NUMERIC.charAt(i), i));
		}		
		
		System.out.println("public final static char[][] ALPHA_NUMERIC_MATRIX = {");
		for (int i = 0; i < len * 2; i++) {
			Collections.shuffle(chars, rand);
			System.out.print("{");
			for (int j = 0; j < len; j++) {					
				System.out.print(String.format("'%c'", chars.get(j)));
				if(j<len-1){
					System.out.print(",");
				}else{
					
				}
			}	
			System.out.print("}");
			if(i<(len * 2)-1){
				System.out.println(",");
			}else{
				System.out.println("");
			}
		}	
		System.out.println("};");
		
		
		
	}

}
