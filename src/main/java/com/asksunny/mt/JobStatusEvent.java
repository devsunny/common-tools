package com.asksunny.mt;

public class JobStatusEvent {
	private String jobIdentifier;
	private JobState jobState;
	private Object payload;

	public String getJobIdentifier() {
		return jobIdentifier;
	}

	public void setJobIdentifier(String jobIdentifier) {
		this.jobIdentifier = jobIdentifier;
	}

	public JobState getJobState() {
		return jobState;
	}

	public void setJobState(JobState jobState) {
		this.jobState = jobState;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

}
