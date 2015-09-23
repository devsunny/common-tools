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
import com.novell.ldap.connectionpool.PoolManager;

public class LDAPUserGroupSyncUtility<GroupType, UserType, UserGroupType> {

	public static final String LDAP_ATTR_MEMBEROF = "memberOf";
	public static final String LDAP_ATTR_UNIQUEMEMBER = "uniquemember";

	private String ldapHost = null;
	private int ldapPort = 10389;
	private int maxConns = 5;
	private int maxSharedConns = 5;
	private boolean secured = Boolean.FALSE;
	private String ldapBindDn = null;
	private String ldapBindCredential = null;
	private LDAPConnection connection = null;

	private Class<GroupType> groupClass = null;
	private String[] groupAttributes = null;
	private String[] groupClassAttributes = null;

	private Class<UserType> userClass = null;
	private String[] userAttributes = null;
	private String[] userClassAttributes = null;

	private Class<UserGroupType> userGroupClass = null;
	private String[] userGroupUserClassAttributes = null;
	private String[] userGroupGroupClassAttributes = null;
	private String[] userGroupUserAttributes = null;
	private String[] userGroupGroupAttributes = null;

	private LDAPUserGroupSyncSink<GroupType, UserType, UserGroupType> syncSink = null;

	public LDAPUserGroupSyncUtility(String ldapHost, int ldapPort,
			boolean secured,
			LDAPUserGroupSyncSink<GroupType, UserType, UserGroupType> syncSink) {
		super();
		this.ldapHost = ldapHost;
		this.ldapPort = ldapPort;
		this.secured = secured;
		this.syncSink = syncSink;
	}

	public LDAPUserGroupSyncUtility(String ldapHost, int ldapPort,
			boolean secured) {
		super();
		this.ldapHost = ldapHost;
		this.ldapPort = ldapPort;
		this.secured = secured;
	}

	public LDAPUserGroupSyncUtility() {
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

	public String[] getUserGroupUserAttributes() {
		return userGroupUserAttributes;
	}

	public void setUserGroupUserAttributes(String[] userGroupUserAttributes) {
		this.userGroupUserAttributes = userGroupUserAttributes;
	}

	public String[] getUserGroupGroupAttributes() {
		return userGroupGroupAttributes;
	}

	public void setUserGroupGroupAttributes(String[] userGroupGroupAttributes) {
		this.userGroupGroupAttributes = userGroupGroupAttributes;
	}

	public String[] parserDn(String dnString) {
		String[] dns = new String[2];
		int idx = 0;
		int len = dnString.length();
		for (idx = 0; idx < len; idx++) {
			char c = dnString.charAt(idx);
			if (c == ',' && idx > 0 && dnString.charAt(idx - 1) != '\\') {
				break;
			}
		}
		dns[0] = dnString.substring(0, idx);
		if (idx < len - 1) {
			dns[1] = dnString.substring(idx + 1).trim();
		} else {
			dns[1] = "";
		}
		return dns;
	}

	protected String[] getSearchAttributes(String[] src, String additional) {
		boolean hasit = Boolean.FALSE;
		if (src != null) {
			for (String str : src) {
				if (str.equalsIgnoreCase(additional)) {
					hasit = Boolean.TRUE;
					break;
				}
			}
		}
		if (hasit) {
			return src;
		} else if (src == null) {
			return new String[] { additional };
		} else {
			String[] ret = new String[src.length + 1];
			System.arraycopy(src, 0, ret, 0, src.length);
			ret[src.length] = additional;
			return ret;
		}
	}

	public void syncWithUniqueMember(String queryString, String base)
			throws LDAPException, InterruptedException, IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException {

		LDAPSearchConstraints constraint = new LDAPSearchConstraints();
		constraint.setMaxResults(100000);
		LDAPSearchResults searchResults = connection.search(
				base,
				LDAPConnection.SCOPE_SUB,
				queryString,
				getSearchAttributes(this.getGroupAttributes(),
						LDAP_ATTR_UNIQUEMEMBER), false, constraint);
		while (searchResults.hasMore()) {
			LDAPEntry groupResult = searchResults.next();
			bindGroup(groupResult);
			LDAPAttribute ldapAttr = groupResult
					.getAttribute(LDAP_ATTR_UNIQUEMEMBER);
			if (ldapAttr != null) {
				String[] members = ldapAttr.getStringValueArray();
				for (int i = 0; i < members.length; i++) {
					String[] mdns = parserDn(members[i]);
					LDAPSearchResults personResults = connection.search(
							mdns[1], LDAPConnection.SCOPE_SUB, mdns[0],
							this.userAttributes, false, constraint);
					if (personResults.hasMore()) {
						LDAPEntry personResult = personResults.next();
						bindPerson(personResult);
						bindPersonGroup(groupResult, personResult);
					}
				}
			}

		}

	}

	public void syncWithMemberOf(String queryString, String base)
			throws LDAPException, InterruptedException, IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException {

		LDAPSearchConstraints constraint = new LDAPSearchConstraints();
		constraint.setMaxResults(100000);
		LDAPSearchResults personResults = connection.search(base,
				LDAPConnection.SCOPE_SUB, queryString,
				this.getGroupAttributes(), false, constraint);
		while (personResults.hasMore()) {
			LDAPEntry personResult = personResults.next();
			bindPerson(personResult);
			LDAPAttribute memberOfAttr = personResult
					.getAttribute(LDAP_ATTR_MEMBEROF);
			if (memberOfAttr != null) {
				String[] groups = memberOfAttr.getStringValueArray();
				for (int i = 0; i < groups.length; i++) {
					String[] mdns = parserDn(groups[i]);
					LDAPSearchResults groupResults = connection.search(mdns[1],
							LDAPConnection.SCOPE_SUB, mdns[0],
							this.userAttributes, false, constraint);
					if (groupResults.hasMore()) {
						LDAPEntry groupResult = groupResults.next();
						bindGroup(groupResult);
						bindPersonGroup(groupResult, personResult);
					}

				}
			}

		}

	}

	protected void bindGroup(LDAPEntry groupResult) throws LDAPException,
			InterruptedException, IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException {
		if (groupClass == null)
			return;
		GroupType group = groupClass.newInstance();
		for (int i = 0; i < this.groupAttributes.length; i++) {
			LDAPAttribute attr = groupResult
					.getAttribute(this.groupAttributes[i]);
			if (attr != null) {
				BeanUtils.setProperty(group, this.groupClassAttributes[i],
						attr.getStringValue());
			}
		}
		if (this.syncSink != null) {
			this.syncSink.syncGroup(group);
		}

	}

	protected void bindPerson(LDAPEntry personResult) throws LDAPException,
			InterruptedException, IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException {
		if (userClass == null)
			return;
		UserType user = userClass.newInstance();
		for (int j = 0; j < this.userAttributes.length; j++) {
			LDAPAttribute attr = personResult
					.getAttribute(this.userAttributes[j]);
			if (attr != null) {
				BeanUtils.setProperty(user, this.userClassAttributes[j],
						attr.getStringValue());
			}
		}
		if (this.syncSink != null) {
			this.syncSink.syncUser(user);
		}

	}

	protected void bindPersonGroup(LDAPEntry groupResult, LDAPEntry personResult)
			throws LDAPException, InterruptedException, IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException {
		if (userGroupClass == null)
			return;
		UserGroupType ugObj = userGroupClass.newInstance();
		for (int k = 0; k < this.userGroupGroupAttributes.length; k++) {
			LDAPAttribute attr = groupResult
					.getAttribute(this.userGroupGroupAttributes[k]);
			if (attr != null) {
				BeanUtils.setProperty(ugObj,
						this.userGroupGroupClassAttributes[k],
						attr.getStringValue());
			}

		}
		for (int l = 0; l < this.userGroupUserAttributes.length; l++) {

			LDAPAttribute attr = personResult
					.getAttribute(this.userGroupUserAttributes[l]);
			if (attr != null) {
				BeanUtils.setProperty(ugObj,
						this.userGroupUserClassAttributes[l],
						attr.getStringValue());
			}

		}
		if (this.syncSink != null) {
			this.syncSink.syncUserGroup(ugObj);
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

	public int getMaxConns() {
		return maxConns;
	}

	public void setMaxConns(int maxConns) {
		this.maxConns = maxConns;
	}

	public int getMaxSharedConns() {
		return maxSharedConns;
	}

	public void setMaxSharedConns(int maxSharedConns) {
		this.maxSharedConns = maxSharedConns;
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

	public Class<GroupType> getGroupClass() {
		return groupClass;
	}

	public void setGroupClass(Class<GroupType> groupClass) {
		this.groupClass = groupClass;
	}

	public String[] getGroupAttributes() {
		return groupAttributes;
	}

	public void setGroupAttributes(String[] groupAttributes) {
		this.groupAttributes = groupAttributes;
	}

	public String[] getGroupClassAttributes() {
		return groupClassAttributes;
	}

	public void setGroupClassAttributes(String[] groupClassAttributes) {
		this.groupClassAttributes = groupClassAttributes;
	}

	public Class<UserType> getUserClass() {
		return userClass;
	}

	public void setUserClass(Class<UserType> userClass) {
		this.userClass = userClass;
	}

	public String[] getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(String[] userAttributes) {
		this.userAttributes = userAttributes;
	}

	public String[] getUserClassAttributes() {
		return userClassAttributes;
	}

	public void setUserClassAttributes(String[] userClassAttributes) {
		this.userClassAttributes = userClassAttributes;
	}

	public Class<UserGroupType> getUserGroupClass() {
		return userGroupClass;
	}

	public void setUserGroupClass(Class<UserGroupType> userGroupClass) {
		this.userGroupClass = userGroupClass;
	}

	public String[] getUserGroupUserClassAttributes() {
		return userGroupUserClassAttributes;
	}

	public void setUserGroupUserClassAttributes(
			String[] userGroupUserClassAttributes) {
		this.userGroupUserClassAttributes = userGroupUserClassAttributes;
	}

	public String[] getUserGroupGroupClassAttributes() {
		return userGroupGroupClassAttributes;
	}

	public void setUserGroupGroupClassAttributes(
			String[] userGroupGroupClassAttributes) {
		this.userGroupGroupClassAttributes = userGroupGroupClassAttributes;
	}

	public LDAPUserGroupSyncSink<GroupType, UserType, UserGroupType> getSyncSink() {
		return syncSink;
	}

	public void setSyncSink(
			LDAPUserGroupSyncSink<GroupType, UserType, UserGroupType> syncSink) {
		this.syncSink = syncSink;
	}

}
