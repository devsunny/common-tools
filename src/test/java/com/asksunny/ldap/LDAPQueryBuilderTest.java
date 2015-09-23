package com.asksunny.ldap;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LDAPQueryBuilderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		String q = LDAPQueryBuilder.newInstance().filter("objectClass", "inetOrgPerson").build();
		assertEquals("(objectClass=inetOrgPerson)", q);
		String str = LDAPQueryBuilder.newInstance().filter("objectClass", "inetOrgPerson").and("cn", "Sunny Liu").build();
		assertEquals("(&(objectClass=inetOrgPerson)(cn=Sunny Liu))", str);
	}

}
