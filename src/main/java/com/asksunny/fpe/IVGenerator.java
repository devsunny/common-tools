package com.asksunny.fpe;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

public class IVGenerator {

	public IVGenerator() {
	}

	public static void main(String[] args) {
		char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
		char[] DIGITS = "0123456789".toCharArray();
		SecureRandom rand = new SecureRandom(UUID.randomUUID().toString().getBytes());
		System.out.print("static final char[][] LETTERS_IV={");
		for (int i = 0; i < 100; i++) {
			List<Character> chars = new ArrayList<>();
			for (int j = 0; j < LETTERS.length; j++) {
				chars.add(LETTERS[j]);
			}
			Collections.shuffle(chars, rand);
			System.out.print("{");
			for (int j = 0; j < LETTERS.length; j++) {
				System.out.print(String.format("'%c'", chars.get(j)));
				if (j < LETTERS.length - 1) {
					System.out.print(",");
				}
			}
			if (i < 99) {
				System.out.println("},");
			} else {
				System.out.println("}");
			}
		}
		System.out.println("};");

		System.out.print("static final char[][] DIGITS_IV={");
		for (int i = 0; i < 100; i++) {
			List<Character> chars = new ArrayList<>();
			for (int j = 0; j < DIGITS.length; j++) {
				chars.add(DIGITS[j]);
			}
			Collections.shuffle(chars, rand);
			System.out.print("{");
			for (int j = 0; j < DIGITS.length; j++) {
				System.out.print(String.format("'%c'", chars.get(j)));
				if (j < DIGITS.length - 1) {
					System.out.print(",");
				}
			}
			if (i < 99) {
				System.out.println("},");
			} else {
				System.out.println("}");
			}
		}
		System.out.println("};");

		
		byte[] keyBytes = new byte[32];
		rand.nextBytes(keyBytes);
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
		keyBytes = key.getEncoded();
		System.out.println(keyBytes.length);
		System.out.print("static final byte[] CRYPTO_IV_256 = {");
		for (int i = 0; i < keyBytes.length; i++) {
			System.out.print(String.format("(byte)%d", (keyBytes[i] & 0xEF)));
			if (i < keyBytes.length - 1) {
				System.out.print(",");
			}
		}
		System.out.print("};");
	}

}
