package com.asksunny.mt;

public class DistributedPriorityJobFactory {

	private static DistributedPriorityJobFactory instance = new DistributedPriorityJobFactory();

	public static DistributedPriorityJob createJobInstance(
			DistJobDetail jobDetail) {
		return instance.createDistributedJobInstance(jobDetail);
	}

	private DistributedPriorityJob createDistributedJobInstance(
			DistJobDetail jobDetail) {
		return null;
	}

	private DistributedPriorityJobFactory() {
	}
}
