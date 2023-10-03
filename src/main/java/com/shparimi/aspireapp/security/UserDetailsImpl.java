package com.shparimi.aspireapp.security;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.shparimi.aspireapp.dao.models.Users;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	private int id;

	private String username;

	private String password;

	private String email;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(int id, String username, String password, String email,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.authorities = authorities;
		this.password = password;
	}

	public static UserDetailsImpl build(Users user) {
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().toString()));
		return new UserDetailsImpl(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}
}
