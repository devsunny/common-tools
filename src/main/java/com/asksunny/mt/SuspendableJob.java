package com.asksunny.mt;

public interface SuspendableJob extends Runnable{

	String getName();
	int getPriority();
	void suspend();
	void resume();
	void cancel();	
	double getProgress();
	JobState getState();
}
