package com.asksunny.mt;

public interface MultiStepsJob 
{
	String getName();
	int getPriority();
	boolean next();	
	void cancel();
	double getProgress();
	JobState getState();
}
