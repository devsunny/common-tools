package com.asksunny.ldap;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LDAPExecutorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		LDAPUserGroupSyncUtility<Group, User, UserGroup> ldap = new LDAPUserGroupSyncUtility<Group, User, UserGroup>();
		ldap.setLdapHost("localhost");
		ldap.setLdapPort(10389);
		ldap.setLdapBindDn("uid=admin,ou=system");
		ldap.setLdapBindCredential("secret");
		ldap.setGroupClass(Group.class);
		ldap.setGroupAttributes(new String[]{"cn"});
		ldap.setGroupClassAttributes(new String[]{"name"});
		
		ldap.setUserClass(User.class);
		ldap.setUserAttributes(new String[]{"sn", "givenName", "uid"});
		ldap.setUserClassAttributes(new String[]{"lname", "fname", "uid"});
		
		
		ldap.setUserGroupClass(UserGroup.class);
		ldap.setUserGroupUserAttributes(new String[]{"uid"});
		ldap.setUserGroupUserClassAttributes(new String[]{"uid"});
		ldap.setUserGroupGroupAttributes(new String[]{"cn"});
		ldap.setUserGroupGroupClassAttributes(new String[]{"groupName"});
		
		
		ldap.setSyncSink(new LDAPSyncDumper());
		//ldap.setSecured(true);
		ldap.open();
		ldap.syncWithUniqueMember("(objectClass=groupOfUniqueNames)", "ou=groups,o=sevenSeas");
		
		//ldap.search("(objectClass=groupOfUniqueNames)", "ou=groups,o=sevenSeas", new String[]{});		
		//ldap.searchPeople("(objectClass=inetOrgPerson)", "ou=people,o=sevenSeas", new String[]{"cn", "uid"});
		
		
		
		
		
	}

}
