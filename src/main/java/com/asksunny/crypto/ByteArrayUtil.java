package com.asksunny.crypto;

import java.util.Arrays;

public class ByteArrayUtil {
	
	public static enum ShiftDirection {LEFT, RIGHT};
	
	
	
	
	public static byte[] shift(byte[] src, int shiftBytes, ShiftDirection direction)
	{
		if(src==null || src.length<=1) throw new IllegalArgumentException("Input Src array can not be null or less than 2 elements.");
		if(shiftBytes>src.length) shiftBytes = shiftBytes % src.length;
		if(shiftBytes==0) return src;
		if(direction == ShiftDirection.LEFT){
			byte[] tmp1 = Arrays.copyOf(src, shiftBytes);
			byte[] tmp2 = Arrays.copyOfRange(src, shiftBytes, src.length);
			System.arraycopy(tmp2, 0, src, 0, tmp2.length);
			System.arraycopy(tmp1, 0, src, tmp2.length, tmp1.length);
		}else{
			byte[] tmp1 = Arrays.copyOf(src,  src.length - shiftBytes);			
			byte[] tmp2 = Arrays.copyOfRange(src, tmp1.length, src.length);
			System.arraycopy(tmp2, 0, src, 0, tmp2.length);
			System.arraycopy(tmp1, 0, src, tmp2.length, tmp1.length);
		}		
		return src;
	}
	
	
		
	public static byte[] concat(byte[] src1, byte[] src2)
	{
		byte[] ret = new byte[src1.length + src2.length];
		System.arraycopy(src1, 0, ret, 0, src1.length);
		System.arraycopy(src2, 0, ret, src1.length, src2.length);
		return ret;
		
	}
	
	public static byte[] concat(byte[] src1, byte[] src2, byte[]... bas)
	{
		int length = src1.length + src2.length;
		for(byte[] ba: bas)
		{
			length += ba.length;
		}		
		byte[] ret = new byte[length];
		System.arraycopy(src1, 0, ret, 0, src1.length);
		System.arraycopy(src2, 0, ret, src1.length, src2.length);
		int pos = src1.length + src2.length;
		for(byte[] ba: bas)
		{
			System.arraycopy(ba, 0, ret, pos, ba.length);
			pos += ba.length;
		}	
		return ret;		
	}
	
	
	private ByteArrayUtil() {
		
	}

	

}
