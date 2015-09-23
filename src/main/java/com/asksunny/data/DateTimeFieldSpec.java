package com.asksunny.data;

public class DateTimeFieldSpec extends FieldSpec
{
	private long minLongValue = Long.MIN_VALUE;
	private long maxLongValue = Long.MAX_VALUE;
	
	
	
	
	public long getMinLongValue() {
		return minLongValue;
	}
	public void setMinLongValue(long minLongValue) {
		this.minLongValue = minLongValue;
	}
	public long getMaxLongValue() {
		return maxLongValue;
	}
	public void setMaxLongValue(long maxLongValue) {
		this.maxLongValue = maxLongValue;
	}
	
	
}
