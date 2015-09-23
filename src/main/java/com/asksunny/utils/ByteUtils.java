package com.asksunny.utils;

public class ByteUtils 
{
	
	public final static char[] HEX_VALUES = "0123456789ABCDEF".toCharArray();
	

	
	public static String toHex(byte[] bytes)
	{
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			int fnibble =   (b>>4);
			int snibble = 0x0f & b;
			buf.append(HEX_VALUES[fnibble]);
			buf.append(HEX_VALUES[snibble]);
		}
		return buf.toString();
	}
	
	public static byte[] fromHex(String bytesString)
	{
		int len = bytesString.length()/2;
		if(bytesString.length()%2 != 0){
			throw new IllegalArgumentException("Bytes Hex string length has to be even number");
		}
		byte[] out = new byte[len];
		for (int i = 0; i < len; i++) {
			int pos = i*2;
			byte b = (byte)Integer.valueOf(bytesString.substring(pos, pos+2), 16).intValue();
			out[i] = b;
		}		
		return out;		
	}
	
	
}
