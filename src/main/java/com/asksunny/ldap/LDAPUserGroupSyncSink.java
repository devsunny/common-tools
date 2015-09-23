package com.asksunny.ldap;

public interface LDAPUserGroupSyncSink<GroupType, UserType, UserGroupType> 
{
	void syncGroup(GroupType groupObject);
	void syncUser(UserType userObject);
	void syncUserGroup(UserGroupType userGroupObject);
}
