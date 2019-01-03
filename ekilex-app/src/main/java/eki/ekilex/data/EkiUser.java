package eki.ekilex.data;

import java.io.Serializable;
import java.security.Principal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class EkiUser implements Principal, Serializable {

	private static final long serialVersionUID = 1L;

	private static final String[] HIDDEN_FIELD_NAMES = new String[] {"password", "activationKey"};

	private Long id;

	private String name;

	private String email;

	private String password;

	private String[] roles;

	private String activationKey;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	@Override
	public boolean equals(Object object) {
		return EqualsBuilder.reflectionEquals(this, object, HIDDEN_FIELD_NAMES);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, HIDDEN_FIELD_NAMES);
	}

	@Override
	public String toString() {
		ReflectionToStringBuilder reflectionToStringBuilder = new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		reflectionToStringBuilder.setExcludeFieldNames(HIDDEN_FIELD_NAMES);
		return reflectionToStringBuilder.toString();
	}

	public String getDescription() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append('[');
		sbuf.append(id);
		sbuf.append(", ");
		sbuf.append(name);
		sbuf.append(", ");
		sbuf.append(email);
		sbuf.append(']');
		return sbuf.toString();
	}
}
