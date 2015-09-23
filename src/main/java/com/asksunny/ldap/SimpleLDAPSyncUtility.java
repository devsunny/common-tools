package com.asksunny.ldap;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

import org.apache.commons.beanutils.BeanUtils;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPJSSESecureSocketFactory;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;

public class SimpleLDAPSyncUtility<Type> {

	private String ldapHost = null;
	private int ldapPort = 10389;
	private boolean secured = Boolean.FALSE;
	private String ldapBindDn = null;
	private String ldapBindCredential = null;
	private int maxSearchResults = 10000;
	private LDAPConnection connection = null;
	private Class<Type> objectClass = null;
	private String[] objectAttributes = null;
	private String[] objectClassAttributes = null;

	private SimpleLdapSyncSink<Type> syncSink = null;

	public SimpleLDAPSyncUtility(String ldapHost, int ldapPort,
			boolean secured, SimpleLdapSyncSink<Type> syncSink) {
		super();
		this.ldapHost = ldapHost;
		this.ldapPort = ldapPort;
		this.secured = secured;
		this.syncSink = syncSink;
	}

	public SimpleLDAPSyncUtility(String ldapHost, int ldapPort, boolean secured) {
		super();
		this.ldapHost = ldapHost;
		this.ldapPort = ldapPort;
		this.secured = secured;
	}

	public SimpleLDAPSyncUtility() {
		super();
	}

	public void open() throws LDAPException {

		if (connection != null)
			return;
		if (isSecured()) {
			connection = new LDAPConnection(new LDAPJSSESecureSocketFactory());
		} else {
			connection = new LDAPConnection();
		}
		connection.connect(this.ldapHost, this.ldapPort);
		if (isSecured()) {
			connection.startTLS();
		}
		connection.bind(LDAPConnection.LDAP_V3, this.ldapBindDn,
				this.ldapBindCredential.getBytes(Charset.defaultCharset()));
	}

	protected void bindGroup(LDAPEntry objectResult) throws LDAPException,
			InterruptedException, IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException {
		if (objectClass == null)
			return;
		Type obj = objectClass.newInstance();
		for (int i = 0; i < this.objectAttributes.length; i++) {
			LDAPAttribute attr = objectResult
					.getAttribute(this.objectAttributes[i]);
			if (attr != null) {
				BeanUtils.setProperty(obj, this.objectClassAttributes[i],
						attr.getStringValue());
			}
		}
		if (this.syncSink != null) {
			this.syncSink.syncObject(obj);
		}

	}

	public void syncObject(String queryBase, String queryString)
			throws LDAPException, InterruptedException, IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException {
		LDAPSearchConstraints constraint = new LDAPSearchConstraints();
		constraint.setMaxResults(maxSearchResults);
		LDAPSearchResults searchResults = connection.search(queryBase,
				LDAPConnection.SCOPE_SUB, queryString, this.objectAttributes,
				false, constraint);
		while (searchResults.hasMore()) {
			LDAPEntry objectResult = searchResults.next();
			bindGroup(objectResult);
		}

	}

	public int getMaxSearchResults() {
		return maxSearchResults;
	}

	public void setMaxSearchResults(int maxSearchResults) {
		this.maxSearchResults = maxSearchResults;
	}

	public void close() throws LDAPException {
		if (connection != null) {
			if (isSecured()) {
				connection.stopTLS();
			}
			connection.disconnect();
			connection = null;
		}
	}

	public String getLdapHost() {
		return ldapHost;
	}

	public void setLdapHost(String ldapHost) {
		this.ldapHost = ldapHost;
	}

	public int getLdapPort() {
		return ldapPort;
	}

	public void setLdapPort(int ldapPort) {
		this.ldapPort = ldapPort;
	}

	public boolean isSecured() {
		return secured;
	}

	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	public String getLdapBindDn() {
		return ldapBindDn;
	}

	public void setLdapBindDn(String ldapBindDn) {
		this.ldapBindDn = ldapBindDn;
	}

	public String getLdapBindCredential() {
		return ldapBindCredential;
	}

	public void setLdapBindCredential(String ldapBindCredential) {
		this.ldapBindCredential = ldapBindCredential;
	}

	public LDAPConnection getConnection() {
		return connection;
	}

	public void setConnection(LDAPConnection connection) {
		this.connection = connection;
	}

	public Class<Type> getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(Class<Type> objectClass) {
		this.objectClass = objectClass;
	}

	public String[] getObjectAttributes() {
		return objectAttributes;
	}

	public void setObjectAttributes(String[] objectAttributes) {
		this.objectAttributes = objectAttributes;
	}

	public String[] getObjectClassAttributes() {
		return objectClassAttributes;
	}

	public void setObjectClassAttributes(String[] objectClassAttributes) {
		this.objectClassAttributes = objectClassAttributes;
	}

	public SimpleLdapSyncSink<Type> getSyncSink() {
		return syncSink;
	}

	public void setSyncSink(SimpleLdapSyncSink<Type> syncSink) {
		this.syncSink = syncSink;
	}

}
