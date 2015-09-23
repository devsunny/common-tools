package com.asksunny.ldap;

public class LDAPSyncDumper implements
		LDAPUserGroupSyncSink<Group, User, UserGroup> {

	@Override
	public void syncGroup(Group groupObject) {
		System.out.println(groupObject);
		
	}

	@Override
	public void syncUser(User userObject) {
		System.out.println(userObject);
		
	}

	@Override
	public void syncUserGroup(UserGroup userGroupObject) {
		System.out.println(userGroupObject);
		
	}

}
