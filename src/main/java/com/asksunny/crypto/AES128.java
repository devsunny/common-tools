package com.asksunny.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * 
 * 
 * @author SunnyLiu
 *
 */


public class AES128 {

	public static final String CIPHER_ALGO = "AES/CBC/PKCS5Padding";
	public static final String CRYPTO_ALGO = "AES";
	public static final String CHARSET_UTF8 = "UTF-8";

	
	/**
	 * OpenSSL encrypt with AES 128<br/>
	 * echo -n "TEST" | openssl enc -aes-128-cbc -e -a -K ABCDEF0123456789ABCDEF0123456789 -iv ABCDEF0123456789ABCDEF0123456789
	 * 
	 * @param text
	 * @param keyHex
	 * @param ivHex
	 * @return
	 */
	public static String encrypt(String text, String keyHex, String ivHex) {
		String ret = null;
		try {
			byte[] key = hexStringToByteArray(keyHex);
			byte[] iv = hexStringToByteArray(ivHex);
			SecretKey sKey = new SecretKeySpec(key, CRYPTO_ALGO);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
			cipher.init(Cipher.ENCRYPT_MODE, sKey, new IvParameterSpec(iv));
			byte[] plain = cipher.doFinal(text.getBytes(CHARSET_UTF8));
			ret = Base64.encodeBase64String(plain).trim();
		} catch (Exception e) {
			throw new SecurityException("Failed to encrypt", e);
		}
		return ret;
	}
	
	/**
	 * openSSL decrypt with AES 128 <br/>
	 * echo  "5RvFodhuh3ET7LJpePZnGg==" | openssl enc -aes-128-cbc -d -a -K ABCDEF0123456789ABCDEF0123456789 -iv ABCDEF0123456789ABCDEF0123456789
	 * 
	 * @param base64Text
	 * @param keyHex
	 * @param ivHex
	 * @return
	 */
	
	public static String decrypt(String base64Text, String keyHex, String ivHex) {
		String ret = null;
		try {
			byte[] key = hexStringToByteArray(keyHex);
			byte[] iv = hexStringToByteArray(ivHex);
			SecretKey sKey = new SecretKeySpec(key, CRYPTO_ALGO);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
			cipher.init(Cipher.DECRYPT_MODE, sKey, new IvParameterSpec(iv));
			byte[] encrypted = Base64.decodeBase64(base64Text);
			byte[] text = cipher.doFinal(encrypted);
			ret = new String(text, CHARSET_UTF8);
		} catch (Exception e) {
			throw new SecurityException("Failed to decrypt", e);
		}
		return ret;
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	
}
