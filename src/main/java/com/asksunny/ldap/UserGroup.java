package com.asksunny.ldap;

public class UserGroup {
	String groupName;
	String uid;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "UserGroup [groupName=" + groupName + ", uid=" + uid + "]";
	}

	
}
