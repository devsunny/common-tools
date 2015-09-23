package com.asksunny.ldap;

public interface SimpleLdapSyncSink<Type> 
{
	void syncObject(Type instanceOfType);	
}
