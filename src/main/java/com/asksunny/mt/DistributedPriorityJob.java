package com.asksunny.mt;

public interface DistributedPriorityJob extends Runnable {
	/**
	 * The name is used to display on UI
	 * @return
	 */
	String getName();

	/**
	 * Thread execution priority, by default all jobs ae assigned with Thread.NORM_PRIORITY
	 * @return
	 */
	int getPriority();

	/**
	 * Suspend the job execution, it is effectively controlled by underline job implementation, for instance this method will not have 
	 * any effect for CLI batch job.
	 */
	void suspend();

	/**
	 * This method exists solely for use with suspend() 
	 */
	void resume();

	/**
	 * This method provides a nice way to cancel the running job.
	 */
	void cancel();

	double getProgress();

	JobState getState();

	/**
	 * This is internal identifier to be used by framework, we recommend use UUID,
	 * @return
	 */
	String getIdentifier();

	/**
	 * Notify the monitor for Job State Change.
	 * 
	 * @param monitor
	 */
	void setJobMonitor(JobMonitor monitor);

}
