package com.asksunny.fpe;

import java.security.Key;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.asksunny.crypto.ByteArrayUtil;


//http://csrc.nist.gov/groups/ST/toolkit/BCM/documents/comments/800-38_Series-Drafts/FPE/public_comments_draft_SP_800-38G.pdf

public class FPEAlgorithm {

	private final static String AES128 = "AES";
	private final static int AES_128_IN_BYTES = 16;
	private final static String AES128_CTR_NOPADDING = "AES/CTR/NoPadding";
	private final static String SHA1 = "SHA1";

	public static int num(int radix, byte[] numString) {
		int x = 0;
		int len = numString.length;
		for (int i = 0; i < len; i++) {
			x = x * radix + (int)numString[i];
		}
		return x;
	}

	public static byte[] str(int radix,  int m,  long number) {
		byte[] ou = new byte[m];		
		long x = number;		
		for (int i = 1; i<=m; i++) {
			long n = x % radix;
			x = x / radix;
			ou[m-i] = (byte)n;
		}
		return ou;
	}

	public static String rev(String str) {
		StringBuilder buf = new StringBuilder();
		buf.append(str);
		return buf.reverse().toString();
	}

	public static byte[] prf(byte[] data, byte[] keys, byte[] tweak)
			throws Exception {

		int m = data.length / AES_128_IN_BYTES; // multiple of 128 bits;

		byte[] y = new byte[16];
		for (int i = 0; i < m; i++) {
			byte[] x = Arrays.copyOfRange(data, i * AES_128_IN_BYTES, i
					* AES_128_IN_BYTES + AES_128_IN_BYTES);
			for (int j = 0; j < 16; j++) {
				y[j] = (byte) (y[j] ^ x[j]);
			}
			y = aes_encrypt(keys, tweak, y);
		}
		return y;
	}
	
	
	public static byte[] xor(byte[] data1, byte[] data2)
			throws Exception {

		int len = data1.length>data2.length?data2.length:data1.length;
		byte[] out = new byte[len];
		for (int i = 0; i < out.length; i++) {
			out[i] = (byte)(data1[i] ^ data2[i]);
		}
		return out;
	}
	

	public static double log2(int bits) {		
		return Math.log(bits)/Math.log(2);
	}

	public static Key toSecurityKey(byte[] key) throws Exception {
		Key ret = null;
		MessageDigest md = MessageDigest.getInstance("SHA1");
		byte[] mykeyval = md.digest(key);
		mykeyval = Arrays.copyOf(mykeyval, 16);
		ret = new SecretKeySpec(mykeyval, AES128);
		return ret;

	}

	public static byte[] aes_encrypt(byte[] keys, byte[] tweak, byte[] plainText)
			throws Exception {
		Cipher chiper = Cipher.getInstance(AES128_CTR_NOPADDING);
		chiper.init(Cipher.ENCRYPT_MODE, toSecurityKey(keys));
		byte[] encVal = chiper.doFinal(plainText);
		return encVal;
	}

	public static byte[] aes_decrypt(byte[] keys, byte[] tweak, byte[] encVal)
			throws Exception {
		Cipher chiper = Cipher.getInstance(AES128_CTR_NOPADDING);
		chiper.init(Cipher.DECRYPT_MODE, toSecurityKey(keys));
		byte[] decVal = chiper.doFinal(encVal);
		return decVal;
	}
	
	public static void ff1(byte[] keys, byte[] tweak, byte[] plainText,
			int radix) throws Exception {
		int n = plainText.length;
		int u = n / 2;
		int v = n - u;
		byte[] A = Arrays.copyOf(plainText, u);
		byte[] B = Arrays.copyOfRange(plainText, u, plainText.length);
		int b = (int) Math.ceil(Math.ceil(v * log2(10)) / 8);		
		int d = (int) (4 * Math.ceil(b / 4) + 4);
		
		byte[] P = new byte[] { (byte) 1, (byte) 2, (byte) 1, (byte) radix,
				(byte) radix, (byte) radix, (byte) 10, (byte) (u % 256),
				(byte) n, (byte) n, (byte) n, (byte) n, (byte) tweak.length,
				(byte) tweak.length, (byte) tweak.length, (byte) tweak.length };
				
		for(int i=0; i<10; i++)
		{
			int px = (tweak.length+b+1) % 16;
			byte[] p0 = new byte[px];
			byte[] p1 = new byte[b];
			Arrays.fill(p1, (byte)num(10, B));
			byte[] Q = ByteArrayUtil.concat(tweak, p0, new byte[]{(byte)i}, p1);
			byte[]  pq = ByteArrayUtil.concat(P, Q);
			byte[] R = prf(pq, keys, tweak);
			int d_16 = (int)Math.ceil(((double)d)/16);			
			byte[] s = R;
			for(int j=0; j< d_16; j++)
			{
				byte[] dr = new byte[16];
				Arrays.fill(dr, (byte)i);
				byte[]  xr = xor(R, dr);
				byte[] enc = aes_encrypt(keys, tweak, xr);
				s = ByteArrayUtil.concat(s, enc);
			}
			int y = num(2, s);
			int m = v;
			if(i%2==0){
				m = u;
			}			
			long c = num(10, A) % ((long)Math.pow(10, m));
			byte[] C = str(10, m, c);
			A = B;
			B = C;			
		}
		byte[] ret = ByteArrayUtil.concat(A, B);
		System.out.println(new String(ret));
	}

	public static void main(String[] args) throws Exception {
		toSecurityKey("test".getBytes());
		byte[] out = prf("1234567890123456".getBytes(), "test".getBytes(),
				"test".getBytes());

		ff1("test".getBytes(), "12345".getBytes(), "1234567890123456".getBytes(), 10);
	}

}
