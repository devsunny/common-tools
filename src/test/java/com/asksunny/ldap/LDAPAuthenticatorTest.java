package com.asksunny.ldap;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LDAPAuthenticatorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test()  throws Exception{
		LDAPAuthenticator ldap = new LDAPAuthenticator();
		
		ldap.setLdapHost("localhost");
		ldap.setLdapPort(10389);
		ldap.setLdapBindDn("uid=admin,ou=system");
		ldap.setLdapBindCredential("secret");
		ldap.setLookupBeforeAuthentication(true);
		ldap.setMemberAttributeName("uniquemember");
		ldap.setGroupNameAttribute("cn");
		ldap.open();
		LDAPSubject subject = ldap.authenticate("cbuckley", "test", null);
		
		System.out.println(subject);
		
		
		ldap.close();
	}

}
