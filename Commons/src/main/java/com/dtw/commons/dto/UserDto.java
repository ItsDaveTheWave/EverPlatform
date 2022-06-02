package com.dtw.commons.dto;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

	@JsonProperty(access = Access.READ_ONLY)
	private Long id;
	@NotNull
	@Length(min = 8, max = 16)
	private String username;
	@NotNull
	@Length(min = 8, max = 16)
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	@Email
	@NotNull
	private String email;
	@JsonProperty(access = Access.READ_ONLY)
	@Builder.Default
	private boolean enabled = true;
	@JsonProperty(access = Access.READ_ONLY)
	@Builder.Default
	private boolean accountNonExpired = true;
	@JsonProperty(access = Access.READ_ONLY)
	@Builder.Default
	private boolean credentialsNonExpired = true;
	@JsonProperty(access = Access.READ_ONLY)
	@Builder.Default
	private boolean accountNonLocked = true;
	@JsonProperty(access = Access.READ_ONLY)
	@Builder.Default
	private Set<String> roles = new HashSet<>();
}