package com.asksunny.ldap;

import java.util.ArrayList;
import java.util.List;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPJSSESecureSocketFactory;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;

public class LDAPAuthenticator {

	public static final String USER_ATTR_GIVENNAME = "givenName";
	public static final String USER_ATTR_SN = "sn";
	public static final String USER_ATTR_UID = "uid";
	public static final String USER_ATTR_MAIL = "mail";
	public static final String[] USER_ATTRNAMES = new String[] {
			USER_ATTR_GIVENNAME, USER_ATTR_SN, USER_ATTR_UID, USER_ATTR_MAIL };

	private String membershipAttributeName = null;
	private String memberAttributeName = "member";

	private String ldapHost = null;
	private int ldapPort = 10389;
	private boolean secured = Boolean.FALSE;
	private String ldapBindDn = null;
	private String ldapBindCredential = null;
	private LDAPConnection connection = null;

	private boolean lookupBeforeAuthentication = false;
	private String userLookupBase = "";
	private String groupLookupBase = "";
	private String userLookupPattern = "uid=%s";
	private String userDirectAuthzPattern = "uid=%s,%s";
	private String groupSearchPattern = "%s=%s";
	private String groupNameAttribute = "cn";

	public synchronized void open() throws LDAPException {

		if (connection != null)
			return;
		if (isSecured()) {
			connection = new LDAPConnection(new LDAPJSSESecureSocketFactory());
		} else {
			connection = new LDAPConnection();
		}
		connection.connect(this.ldapHost, this.ldapPort);
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

	protected String lookupUserDn(String username, String diffBase)
			throws Exception {
		LDAPSearchConstraints constraint = new LDAPSearchConstraints();
		constraint.setMaxResults(1);
		String base = diffBase == null ? getUserLookupBase() : diffBase;
		LDAPSearchResults searchResults = connection.search(base,
				LDAPConnection.SCOPE_SUB,
				String.format(this.userLookupPattern, username),
				new String[] {}, false, constraint);
		if (searchResults != null && searchResults.hasMore()) {
			return searchResults.next().getDN();
		} else {
			return null;
		}

	}

	protected List<String> lookupUserGroupMemberShip(String userDn)
			throws Exception {
		List<String> roles = new ArrayList<>();
		LDAPSearchConstraints constraint = new LDAPSearchConstraints();
		constraint.setMaxResults(1000);
		LDAPSearchResults searchResults = connection.search(
				getGroupLookupBase(), LDAPConnection.SCOPE_SUB, String.format(
						groupSearchPattern, getMemberAttributeName(), userDn),
				new String[] { getGroupNameAttribute() }, false, constraint);
		while (searchResults.hasMore()) {
			LDAPEntry groupResult = searchResults.next();
			LDAPAttribute att = groupResult
					.getAttribute(getGroupNameAttribute());
			if (att != null) {
				roles.add(att.getStringValue());
			}
		}
		return roles;
	}

	public synchronized LDAPSubject authenticate(String username,
			String password) throws Exception {
		return authenticate(username, password, null);
	}

	public synchronized LDAPSubject authenticate(String username,
			String password, String diffBase) throws Exception {

		if (!connection.isConnected()) {
			connection.connect(this.ldapHost, this.ldapPort);
		}
		String userDn = null;

		String base = diffBase == null ? getUserLookupBase() : diffBase;

		if (this.lookupBeforeAuthentication) {
			userDn = lookupUserDn(username, diffBase);
		} else {
			userDn = String.format(this.userDirectAuthzPattern, username, base);
		}

		if (userDn == null) {
			throw new SecurityException("User does not exist");
		}
		LDAPSubject subject = null;
		try {
			connection
					.bind(LDAPConnection.LDAP_V3, userDn, password.getBytes());
			LDAPSearchConstraints constraint = new LDAPSearchConstraints();
			subject = new LDAPSubject();
			subject.setUsername(username);
			constraint.setMaxResults(1);
			String[] userDns = parserDn(userDn);

			String[] searchAttrFilter = null;
			if (getMembershipAttributeName() != null) {
				searchAttrFilter = new String[] { USER_ATTR_GIVENNAME,
						USER_ATTR_SN, USER_ATTR_UID, USER_ATTR_MAIL,
						getMemberAttributeName() };
			} else {
				searchAttrFilter = USER_ATTRNAMES;
			}

			LDAPSearchResults searchResults = connection.search(
					getGroupLookupBase(), LDAPConnection.SCOPE_SUB, userDns[0],
					searchAttrFilter, false, constraint);
			if (searchResults.hasMore()) {
				LDAPEntry personResult = searchResults.next();

				LDAPAttribute att = personResult
						.getAttribute(USER_ATTR_GIVENNAME);
				if (att != null) {
					subject.setFirstName(att.getStringValue());
				}

				att = personResult.getAttribute(USER_ATTR_SN);
				if (att != null) {
					subject.setLastName(att.getStringValue());
				}

				att = personResult.getAttribute(USER_ATTR_MAIL);
				if (att != null) {
					subject.setEmail(att.getStringValue());
				}

				if (getMembershipAttributeName() != null) {
					att = personResult
							.getAttribute(getMembershipAttributeName());
					if (att != null) {
						String[] memberships = att.getStringValueArray();
						for (int i = 0; i < memberships.length; i++) {
							String[] gdns = parserDn(memberships[i]);
							if(gdns[0].length()>3){
								subject.getRoles().add(gdns[0].substring(3));
							}
						}
					}

				}

			}
		} catch (LDAPException e) {
			throw new SecurityException("Authentication failed");
		}

		if (membershipAttributeName == null) {
			List<String> roles = lookupUserGroupMemberShip(userDn);
			subject.getRoles().addAll(roles);
		}
		return subject;
	}

	public void close() throws LDAPException {
		connection.disconnect();
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

	public String getMembershipAttributeName() {
		return membershipAttributeName;
	}

	public void setMembershipAttributeName(String membershipAttributeName) {
		this.membershipAttributeName = membershipAttributeName;
	}

	public String getMemberAttributeName() {
		return memberAttributeName;
	}

	public void setMemberAttributeName(String memberAttributeName) {
		this.memberAttributeName = memberAttributeName;
	}

	public boolean isLookupBeforeAuthentication() {
		return lookupBeforeAuthentication;
	}

	public void setLookupBeforeAuthentication(boolean lookupBeforeAuthentication) {
		this.lookupBeforeAuthentication = lookupBeforeAuthentication;
	}

	public String getUserLookupBase() {
		return userLookupBase;
	}

	public void setUserLookupBase(String userLookupBase) {
		this.userLookupBase = userLookupBase;
	}

	public String getUserLookupPattern() {
		return userLookupPattern;
	}

	public void setUserLookupPattern(String userLookupPattern) {
		this.userLookupPattern = userLookupPattern;
	}

	public String getUserDirectAuthzPattern() {
		return userDirectAuthzPattern;
	}

	public void setUserDirectAuthzPattern(String userDirectAuthzPattern) {
		this.userDirectAuthzPattern = userDirectAuthzPattern;
	}

	public String getGroupLookupBase() {
		return groupLookupBase;
	}

	public void setGroupLookupBase(String groupLookupBase) {
		this.groupLookupBase = groupLookupBase;
	}

	public String getGroupSearchPattern() {
		return groupSearchPattern;
	}

	public void setGroupSearchPattern(String groupSearchPattern) {
		this.groupSearchPattern = groupSearchPattern;
	}

	public String getGroupNameAttribute() {
		return groupNameAttribute;
	}

	public void setGroupNameAttribute(String groupNameAttribute) {
		this.groupNameAttribute = groupNameAttribute;
	}

}
