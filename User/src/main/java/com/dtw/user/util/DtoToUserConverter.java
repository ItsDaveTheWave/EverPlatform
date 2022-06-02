package com.dtw.user.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import com.dtw.commons.dto.UserDto;
import com.dtw.user.entity.Role;
import com.dtw.user.entity.User;
import com.dtw.user.repo.RoleRepo;

public class DtoToUserConverter implements Converter<UserDto, User> {

	@Autowired
	private RoleRepo roleRepo;
	
	@Override
	public User convert(UserDto source) {
		
		List<Role> roles = new ArrayList<>();
		for(String roleName : source.getRoles()) {
			Optional<Role> optRole = roleRepo.findByName(roleName);
			if(optRole.isPresent()) {
				roles.add(optRole.get());
			}
		}
		
		return new User(source.getId(), source.getUsername(), source.getPassword(), source.getEmail(), source.isEnabled(), 
				source.isAccountNonExpired(), source.isCredentialsNonExpired(), source.isAccountNonLocked(), 
				roles);
	}
}