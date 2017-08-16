package eki.ekilex.data;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class EkiUser implements Authentication, Serializable {

	private static final long serialVersionUID = 1L;

	private static final String[] HIDDEN_FIELD_NAMES = new String[] {"password", "details", "authenticated"};

	private Long id;

	private String name;

	private String password;

	private WebAuthenticationDetails details;

	private boolean authenticated;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public Object getCredentials() {
		return password;
	}

	@Override
	public Object getDetails() {
		return details;
	}

	public void setDetails(WebAuthenticationDetails details) {
		this.details = details;
	}

	@Override
	public Object getPrincipal() {
		return this;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		this.authenticated = isAuthenticated;
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
		sbuf.append(']');
		return sbuf.toString();
	}
}
