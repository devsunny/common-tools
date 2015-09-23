package com.asksunny.io.linux;

public class INotifyEvent {

	protected int eventType = -1;
	protected String targetFilename = null;
	
	/**
	 * This value only available when IN_MOVE_FROM and IN_MOVE_TO event;
	 */
	protected  String sourceFilename = null;
	
	public INotifyEvent() 
	{
		
	}

	public int getEventType() {
		return eventType;
	}

	public INotifyEvent setEventType(int eventType) {
		this.eventType = eventType;
		return this;
	}

	public String getTargetFilename() {
		return targetFilename;
	}

	public INotifyEvent setTargetFilename(String targetFilename) {
		this.targetFilename = targetFilename;
		return this;
	}

	public String getSourceFilename() {
		return sourceFilename;
	}

	public INotifyEvent setSourceFilename(String sourceFilename) {
		this.sourceFilename = sourceFilename;
		return this;
	}
	
	
	

	
}
