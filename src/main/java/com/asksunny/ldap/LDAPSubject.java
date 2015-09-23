package com.asksunny.ldap;

import java.util.ArrayList;
import java.util.List;

public class LDAPSubject {

	private String username;
	private List<String> roles = new ArrayList<>();
	private String firstName;
	private String lastName;
	private String email;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "LDAPSubject [username=" + username + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + ", roles="
				+ roles + "]";
	}
	
	

}
