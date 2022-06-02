package com.dtw.user.util;

import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;

import com.dtw.commons.dto.UserDto;
import com.dtw.user.entity.User;

public class UserToDtoConverter implements Converter<User, UserDto> {

	@Override
	public UserDto convert(User source) {
		return new UserDto(source.getId(), source.getUsername(), source.getPassword(), source.getEmail(), source.isEnabled(), source.isAccountNonExpired(), 
				source.isCredentialsNonExpired(), source.isAccountNonLocked(), 
				source.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()));
	}
}