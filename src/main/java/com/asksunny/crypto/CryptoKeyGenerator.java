package com.asksunny.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;

public class CryptoKeyGenerator {

	public static final String[] KEYGEN_ALGOS = new String[] { "AES",
			"ARCFOUR", "Blowfish", "DES", "DESede", "HmacMD5", "HmacSHA1",
			"HmacSHA256", "HmacSHA384", "HmacSHA512", "RC2" };

	public static byte[] generateKey(String keyGenAlgo, int keySize)
			throws NoSuchAlgorithmException {

		KeyGenerator kg = KeyGenerator.getInstance(keyGenAlgo);
		kg.init(keySize, new SecureRandom(UUID.randomUUID().toString().getBytes()));	
		SecretKey secretKey = kg.generateKey();
		byte[] val = secretKey.getEncoded();
		return val;
	}

	public static String generateKey2HexString(String keyGenAlgo, int keySize)
			throws NoSuchAlgorithmException {
		return Hex.encodeHexString(generateKey(keyGenAlgo, keySize));
	}

	//0140eed728850c274036b2a709414516
	//6f49d64e3caeeb80f9ed4e0ee23bd800

	public static void main(String[] args) throws Exception {
		if (args.length == 0 || args[0].equalsIgnoreCase("--help")
				|| args[0].equalsIgnoreCase("-h")) {
			System.err
					.println("CryptoKeyGenerator takes Key Generator Algorithm and key size as parameter, generate key in Hex format");
			System.err
					.println("Usage: CryptoKeyGenerator <keyGenAlgo> <key_size>");
			System.err.println("\tKey Generation Algorithms:");
			for (int i = 0; i < KEYGEN_ALGOS.length; i++) {
				System.err.println(String.format("\t\t%s", KEYGEN_ALGOS[i]));
			}
			System.err
					.println("\tKey size: 56, 64, 128, 192, 256, 384, 512, 1024, 2048 etc.");
			System.err.println("Examples:");
			System.err.println("\tCryptoKeyGenerator HmacSHA256 128");
			System.err.println("\tCryptoKeyGenerator AES 256");
			System.exit(1);
		}else{
			String keyAlgo = null;
			int keySize = 128;
			if(args.length==1  ){				
				if(args[0].matches("^\\d+$")){
					keySize = Integer.valueOf(args[0]);
					keyAlgo = "AES";
				}else{
					keyAlgo = args[0];					
				}
			}else{
				if(args[0].matches("^\\d+$")){
					keySize = Integer.valueOf(args[0]);
					keyAlgo = args[1];		
				}else{
					keySize = Integer.valueOf(args[1]);
					keyAlgo = args[0];	
				}				
			}			
			System.out.println(generateKey2HexString(keyAlgo, keySize));
		}

	}

}
