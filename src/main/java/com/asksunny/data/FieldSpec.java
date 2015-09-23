package com.asksunny.data;

import java.util.Calendar;

public class FieldSpec {

	public static final int INT = 0;
	public static final int LONG = 1;
	public static final int DOUBLE = 2;
	public static final int STRING = 3;
	public static final int DIGITS = 4;
	public static final int LETTERS = 5;
	public static final int DATE = 6;
	public static final int TIME = 7;
	public static final int TIMESTAMP_MILLI = 8;
	public static final int TIMESTAMP_NANO = 9;
	public static final int TIMESTAMP = 10;
	public static final int SEQUENCE = 11;
	public static final int FORMAT_DIGITS = 12;
	public static final int ENUM = 13;
	
	public static final int FIXED_VALUE = 99;
	public static final long YEAR_IN_MILLI = 1000L * 60L * 60L * 24L * 365L;

	private int type = STRING;
	private String max;
	private String min;
	private String name;

	private int minIntValue = Integer.MIN_VALUE;
	private int maxIntValue = Integer.MAX_VALUE;

	private long minLongValue = Long.MIN_VALUE;
	private long maxLongValue = Long.MAX_VALUE;

	private double minDoubleValue = Double.MIN_VALUE;
	private double maxDoubleValue = Double.MAX_VALUE;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasMin() {
		return this.min != null;
	}

	public boolean hasMax() {
		return this.max != null;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public int getMinIntValue() {
		if (hasMin() && minIntValue == Integer.MIN_VALUE) {
			this.minIntValue = Integer.valueOf(min);
		}
		return minIntValue;
	}

	public int getMaxIntValue() {

		if (hasMax() && maxIntValue == Integer.MAX_VALUE) {
			this.maxIntValue = Integer.valueOf(max);
		}
		return maxIntValue;
	}

	public long getMinLongValue() {
		if (hasMin()
				&& minLongValue == Long.MIN_VALUE
				&& (this.type == DATE || this.type == TIME
						|| this.type == TIMESTAMP
						|| this.type == TIMESTAMP_MILLI || this.type == TIMESTAMP_NANO)) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.valueOf(min));
			this.minLongValue = cal.getTimeInMillis();
		} else if (hasMin() && minLongValue == Long.MIN_VALUE) {
			this.minLongValue = Long.valueOf(min);
		}
		return minLongValue;
	}

	public long getMaxLongValue() {
		if (hasMin()
				&& maxLongValue == Long.MAX_VALUE
				&& (this.type == DATE || this.type == TIME
						|| this.type == TIMESTAMP
						|| this.type == TIMESTAMP_MILLI || this.type == TIMESTAMP_NANO)) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.valueOf(max));
			this.maxLongValue = cal.getTimeInMillis();
		} else if (hasMax() && maxLongValue == Long.MAX_VALUE) {
			this.maxLongValue = Long.valueOf(max);
		}
		return maxLongValue;
	}

	public double getMinDoubleValue() {

		if (hasMin() && minDoubleValue == Double.MIN_VALUE) {
			this.minDoubleValue = Double.valueOf(min);
		}
		return minDoubleValue;
	}

	public double getMaxDoubleValue() {
		if (hasMax() && maxDoubleValue == Double.MAX_VALUE) {
			this.maxDoubleValue = Double.valueOf(max);
		}
		return maxDoubleValue;
	}

	@Override
	public String toString() {
		return "FieldSpec [name=" + name + ", type=" + type + ", min=" + min
				+ ", max=" + max + "]";
	}

}
