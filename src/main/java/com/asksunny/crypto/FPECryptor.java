package com.asksunny.crypto;

import java.nio.charset.Charset;

public class FPECryptor extends AbstractCrypto {
	public final static int ROUND = 9;
	public final static Charset CHARSET_UTF8 = Charset.forName("UTF8");
	public final static String ALPHAS = "^[a-zA-Z0-9]+$";
	public final static String DIGITS = "[0-9]";

	public static String fpe_encrypt_text(String text, byte[] keys,
			byte[] tweak) throws Exception {
		String numstring = text;
		boolean formated = !text.matches(ALPHAS); 
		
		
		if(formated){
			int strlen = numstring.length();
			StringBuilder cleanBuf = new StringBuilder();	
			for (int j = 0; j < strlen; j++) {
				char c = numstring.charAt(j);
				if (ALPHA_NUMERIC_MAP.containsKey(c)) {
					cleanBuf.append(c);
				}
			}
			numstring = cleanBuf.toString();
		}	
		
		String encstr = numstring;		
		int len = encstr.length();
		if (len < 4) {
			encstr = fpe_encrypt_alpha(numstring, keys, tweak);
		}else{
			
			int left = len / 2;
			int right = len - left;
			if (left == right) {
				left = left + 1;
				right = right - 1;
			}
			
			for (int i = 0; i < ROUND; i++) {
				String lstr = encstr.substring(0, left);
				String rstr = encstr.substring(left);				
				String enc1 = fpe_encrypt_alpha(lstr, keys, tweak);
				String enc2 = fpe_encrypt_alpha(rstr, enc1.getBytes(CHARSET_UTF8),
						tweak);				
				encstr = enc2 + enc1;
				
			}
		}	
		
		if(formated){
			StringBuilder buf = new StringBuilder();
			buf.append(text);
			int jlen = text.length();
			int idx = 0;
			for (int j = 0; j < jlen; j++) {
				char c = buf.charAt(j);
				if (ALPHA_NUMERIC_MAP.containsKey(c)) {
					char pc = encstr.charAt(idx++);
					buf.setCharAt(j, pc);
				}
			}
			encstr = buf.toString();
		}
		return encstr;
	}

	public static String fpe_decrypt_text(String text, byte[] keys,
			byte[] tweak) throws Exception {

		String numstring = text;
		boolean formated = !text.matches(ALPHAS); 
		if(formated){
			int strlen = numstring.length();
			StringBuilder cleanBuf = new StringBuilder();	
			for (int j = 0; j < strlen; j++) {
				char c = numstring.charAt(j);
				if (ALPHA_NUMERIC_MAP.containsKey(c)) {
					cleanBuf.append(c);
				}
			}
			numstring = cleanBuf.toString();
		}	
		
		
		String decstr = numstring;
		
		
		int len = decstr.length();
		if (len < 4) {
			decstr = fpe_decrypt_alpha(numstring, keys, tweak);
		}else{
			int right = len / 2;
			int left = len - right;
			if (left == right) {
				left = left - 1;
				right = right + 1;
			}			
			for (int i = ROUND; i > 0; i--) {
				String lstr = decstr.substring(0, left);
				String rstr = decstr.substring(left);
				String dec1 = fpe_decrypt_alpha(lstr, rstr.getBytes(CHARSET_UTF8),
						tweak);
				String dec2 = fpe_decrypt_alpha(rstr, keys, tweak);							
				decstr = dec2 + dec1;
			}
		}
			
		if(formated){
			StringBuilder buf = new StringBuilder();
			buf.append(text);
			int jlen = text.length();
			int idx = 0;
			for (int j = 0; j < jlen; j++) {
				char c = buf.charAt(j);
				if (ALPHA_NUMERIC_MAP.containsKey(c)) {
					char pc = decstr.charAt(idx++);
					buf.setCharAt(j, pc);
				}
			}
			decstr = buf.toString();
		}
		
		
		return decstr;
	}

	public static String fpe_encrypt_number(String text, byte[] keys,
			byte[] tweak) throws Exception {

		String numstring = text;
		boolean formated = !text.matches(DIGITS); 
				
		if(formated){
			int strlen = numstring.length();
			StringBuilder cleanBuf = new StringBuilder();	
			for (int j = 0; j < strlen; j++) {
				char c = numstring.charAt(j);
				if (c>=48 && c<=57) {
					cleanBuf.append(c);
				}
			}
			numstring = cleanBuf.toString();
		}	
		
		
		int len = numstring.length();
		if (len < 4) {
			return fpe_encrypt_numeric(numstring, keys, tweak);
		}
		int left = len / 2;
		int right = len - left;
		if (left == right) {
			left = left + 1;
			right = right - 1;
		}
		String encstr = numstring;
		for (int i = 0; i < ROUND; i++) {
			String lstr = encstr.substring(0, left);
			String rstr = encstr.substring(left);
			String enc1 = fpe_encrypt_numeric(lstr, keys, tweak);
			String enc2 = fpe_encrypt_numeric(rstr,
					enc1.getBytes(CHARSET_UTF8), tweak);
			encstr = enc2 + enc1;
		}
		
		if(formated){
			StringBuilder buf = new StringBuilder();
			buf.append(text);
			int jlen = text.length();
			int idx = 0;
			for (int j = 0; j < jlen; j++) {
				char c = buf.charAt(j);
				if (c>=48 && c<=57) {
					char pc = encstr.charAt(idx++);
					buf.setCharAt(j, pc);
				}
			}
			encstr = buf.toString();
		}		
		return encstr;
	}

	public static String fpe_decrypt_number(String text, byte[] keys,
			byte[] tweak) throws Exception {

		String numstring = text;
		boolean formated = !text.matches(DIGITS); 
				
		if(formated){
			int strlen = numstring.length();
			StringBuilder cleanBuf = new StringBuilder();	
			for (int j = 0; j < strlen; j++) {
				char c = numstring.charAt(j);
				if (c>=48 && c<=57) {
					cleanBuf.append(c);
				}
			}
			numstring = cleanBuf.toString();
		}	
		int len = numstring.length();
		if (len < 4) {
			return fpe_decrypt_numeric(numstring, keys, tweak);
		}
		int right = len / 2;
		int left = len - right;
		if (left == right) {
			left = left - 1;
			right = right + 1;
		}
		String decstr = numstring;
		for (int i = ROUND; i > 0; i--) {
			String lstr = decstr.substring(0, left);
			String rstr = decstr.substring(left);
			String dec1 = fpe_decrypt_numeric(lstr,
					rstr.getBytes(CHARSET_UTF8), tweak);
			String dec2 = fpe_decrypt_numeric(rstr, keys, tweak);
			decstr = dec2 + dec1;
		}
		
		if(formated){
			StringBuilder buf = new StringBuilder();
			buf.append(text);
			int jlen = text.length();
			int idx = 0;
			for (int j = 0; j < jlen; j++) {
				char c = buf.charAt(j);
				if (c>=48 && c<=57) {
					char pc = decstr.charAt(idx++);
					buf.setCharAt(j, pc);
				}
			}
			decstr = buf.toString();
		}		
		
		
		return decstr;
	}

	protected static String fpe_decrypt_numeric(String numstring, byte[] keys,
			byte[] tweak) throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append(numstring);
		byte[] seed = keys;
		if (tweak != null) {
			seed = ByteArrayUtil.concat(tweak, keys);
		}
		byte[] encBytes = aes_encrypt(seed, keys, tweak);
		long chksum = checksum(encBytes);
		int len = numstring.length();
		for (int j = 0; j < len; j++) {
			char c = buf.charAt(j);
			int mi = (int) (((j+1) * (j+1) * chksum * chksum) % NUMERIC_MATRIX.length);
			int enc = c - 48;
			int idx = 0;
			for (; idx < NUMERIC_MATRIX[mi].length; idx++) {
				if (NUMERIC_MATRIX[mi][idx] == enc) {
					break;
				}
			}
			buf.setCharAt(j, (char) (idx + 48));
		}
		return buf.toString();
	}

	protected static String fpe_encrypt_numeric(String numstring, byte[] keys,
			byte[] tweak) throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append(numstring);
		byte[] seed = keys;
		if (tweak != null) {
			seed = ByteArrayUtil.concat(tweak, keys);
		}
		byte[] encBytes = aes_encrypt(seed, keys, tweak);
		long chksum = checksum(encBytes);
		int len = numstring.length();

		for (int j = 0; j < len; j++) {
			char c = buf.charAt(j);
			int mi = (int) (((j+1) * (j+1) * chksum * chksum) % NUMERIC_MATRIX.length);
			int idx = c - 48;
			buf.setCharAt(j, (char) (NUMERIC_MATRIX[mi][idx] + 48));
		}
		return buf.toString();
	}

	protected static String fpe_encrypt_alpha(String numstring, byte[] keys,
			byte[] tweak) throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append(numstring);
		byte[] seed = keys;
		if (tweak != null) {
			seed = ByteArrayUtil.concat(tweak, keys);
		}
		byte[] encBytes = aes_encrypt(seed, keys, tweak);
		long chksum = checksum(encBytes);
		int len = numstring.length();
		for (int j = 0; j < len; j++) {
			char c = buf.charAt(j);
			int mi = (int) (((j+1) * (j+1) * chksum * chksum) % ALPHA_NUMERIC_MATRIX.length);
			int idx = ALPHA_NUMERIC_MAP.get(c);
			buf.setCharAt(j, (char) (ALPHA_NUMERIC_MATRIX[mi][idx]));
		}
		return buf.toString();
	}

	protected static String fpe_decrypt_alpha(String numstring, byte[] keys,
			byte[] tweak) throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append(numstring);
		byte[] seed = keys;
		if (tweak != null) {
			seed = ByteArrayUtil.concat(tweak, keys);
		}
		byte[] encBytes = aes_encrypt(seed, keys, tweak);
		long chksum = checksum(encBytes);
		int len = numstring.length();
		for (int j = 0; j < len; j++) {
			char c = buf.charAt(j);
			int mi = (int) (((j+1) * (j+1) * chksum * chksum) % ALPHA_NUMERIC_MATRIX.length);
			int idx = 0;
			for (; idx < ALPHA_NUMERIC_MATRIX[mi].length; idx++) {
				if (ALPHA_NUMERIC_MATRIX[mi][idx] == c) {
					break;
				}
			}
			if (idx >= 52 && idx <= 61) {
				char pc = (char) (idx - 4);
				buf.setCharAt(j, pc);
			} else if (idx >= 0 && idx <= 25) {
				char pc = (char) (idx + 65);
				buf.setCharAt(j, pc);
			} else if (idx >= 26 && idx <= 50) {
				char pc = (char) (idx + 71);
				buf.setCharAt(j, pc);
			} else {
				buf.setCharAt(j, c);
			}
		}
		return buf.toString();
	}

	public static void main(String[] args) throws Exception {
		String enc = fpe_encrypt_number("1234-5678-9012-3456", "test".getBytes(),
				"test".getBytes());
		System.out.println(enc);
		String dec = fpe_decrypt_number(enc, "test".getBytes(),
				"test".getBytes());
		System.out.println(dec);

		enc = fpe_encrypt_text("105 seaman", "test".getBytes(),
				"test".getBytes());
		System.out.println(enc);
		dec = fpe_decrypt_text(enc, "test".getBytes(), "test".getBytes());
		System.out.println(dec);
	
		

	}

}
