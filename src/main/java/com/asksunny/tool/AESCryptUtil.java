package com.asksunny.tool;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESCryptUtil {

	SecretKeySpec secretKey = null;
	int keyLength = 128;
	String cipherTransformation = "AES";

	// Transformation AES/ECB/PKCS5Padding only support 128 Bit Keys;

	public AESCryptUtil(int keySize, String keyString) {
		if (keySize != 128 && keySize != 192 && keySize != 256) {
			throw new IllegalArgumentException(
					"Only keysize 128, 192 or 256 allowed");
			// 192 or 256 only works if Unlimited Strength Jurisdiction Policy
			// installed.
			// DEFAULT
		}
		this.keyLength = keySize;
		try {
			setKey(keyString);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to encrypt", ex);
		}
		
	}

	protected void setKey(String keyString) throws Exception {
		MessageDigest sha256 = null;
		byte[] keyBin = keyString.getBytes(Charset.forName("UTF8"));
		sha256 = MessageDigest.getInstance("SHA-256");
		keyBin = sha256.digest(keyBin);
		keyBin = Arrays.copyOf(keyBin, keyLength / 8);
		System.out.println(keyBin.length);
		secretKey = new SecretKeySpec(keyBin, "AES");
	}

	public String encrypt(String plainText) {

		try {

			Cipher cipher = Cipher.getInstance(cipherTransformation);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			String base64Encrypted = org.apache.commons.codec.binary.Base64
					.encodeBase64String(cipher.doFinal(plainText
							.getBytes("UTF-8")));
			return base64Encrypted;
		} catch (Exception ex) {
			throw new RuntimeException("Failed to encrypt", ex);
		}

	}

	public String decrypt(String encryptedText) {

		try {			
			Cipher cipher = Cipher.getInstance(cipherTransformation);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			String plaintext = new String(
					cipher.doFinal(org.apache.commons.codec.binary.Base64
							.decodeBase64(encryptedText)));
			return plaintext;
		} catch (Exception ex) {
			throw new RuntimeException("Failed to encrypt", ex);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AESCryptUtil util = new AESCryptUtil(192, "this is a secret key no one should know it");
		String encrypted = util.encrypt("hello");
		System.out.println(encrypted);
		String plaintext = util.decrypt( encrypted);
		System.out.println(plaintext);
	}

}
