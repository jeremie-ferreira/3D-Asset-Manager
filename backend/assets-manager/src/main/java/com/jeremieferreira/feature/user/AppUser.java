package com.jeremieferreira.feature.user;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.jeremieferreira.feature.common.Constant.ObjectState;
import com.jeremieferreira.feature.file.AppFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class AppUser implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	public enum Role { ROLE_SUPERADMIN, ROLE_ADMIN, ROLE_INTEGRATOR, ROLE_TRAINER, ROLE_USER }

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;
	
	public ObjectState objectState;
	
	@Column(nullable=false, unique=true)
	private String email;

	@Column(nullable=false)
	private String firstname;

	@Column(nullable=false)
	private String lastname;
	
	private String displayName;
	
	@Column(nullable=false)
	private Role role;
	
	@Column(nullable=false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	
	private String phoneNumber;

	private String urlToken;
	
	@ManyToOne
	@JoinColumn(name = "profile_picture_id")
	private AppFile profilePicture;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public ObjectState getObjectState() {
		return objectState;
	}

	public void setObjectState(ObjectState objectState) {
		this.objectState = objectState;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public AppFile getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(AppFile profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getUrlToken() {
		return urlToken;
	}

	public void setUrlToken(String urlToken) {
		this.urlToken = urlToken;
	}

	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.commaSeparatedStringToAuthorityList(this.getRole().toString());
	}

	@JsonIgnore
	public String getUsername() {
		return this.getEmail();
	}

	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonIgnore
	public boolean isEnabled() {
		return true;
	}
}
