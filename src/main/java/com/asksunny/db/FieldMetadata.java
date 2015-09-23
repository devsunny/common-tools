package com.asksunny.db;


public class FieldMetadata {

	String name;
	int index;
	int jdbcType;
	int precision;
	int scale;
	String entityName;
	int displaySize;
	
	//Extends attribute 
	long uniqueValues;
	long totalValidValues;
	long totalCount;
	double stddev;
	double mean;
	double max;
	double min;	
	
	
	public int getDisplaySize() {
		return displaySize;
	}
	public void setDisplaySize(int displaySize) {
		this.displaySize = displaySize;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getJdbcType() {
		return jdbcType;
	}
	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}
	public int getPrecision() {
		return precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public long getUniqueValues() {
		return uniqueValues;
	}
	public void setUniqueValues(long uniqueValues) {
		this.uniqueValues = uniqueValues;
	}
	public long getTotalValidValues() {
		return totalValidValues;
	}
	public void setTotalValidValues(long totalValidValues) {
		this.totalValidValues = totalValidValues;
	}
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public double getStddev() {
		return stddev;
	}
	public void setStddev(double stddev) {
		this.stddev = stddev;
	}
	public double getMean() {
		return mean;
	}
	public void setMean(double mean) {
		this.mean = mean;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	
	

}
