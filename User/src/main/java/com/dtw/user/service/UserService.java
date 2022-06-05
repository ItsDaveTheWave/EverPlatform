package com.dtw.user.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dtw.commons.dto.UserDto;
import com.dtw.commons.enums.ReturnStatus;
import com.dtw.user.entity.User;
import com.dtw.user.repo.RoleRepo;
import com.dtw.user.repo.UserRepo;

@Service
public class UserService {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;
	
	public List<User> getAll() {
		return userRepo.findAll();
	}
	
	public Optional<User> getOneByUsername(String username) {
		return userRepo.findByUsername(username);
	}
	
	public Pair<Optional<User>, ReturnStatus> create(User user, String role) {
		if(userRepo.findByUsername(user.getUsername()).isPresent()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_WITH_USERNAME_ALREADY_EXISTS);
		}
		if(userRepo.findByEmail(user.getEmail()).isPresent()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_WITH_EMAIL_ALREADY_EXISTS);
		}
		
		user.setRoles(Collections.singletonList(roleRepo.findByName(role).get()));
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return Pair.of(Optional.of(userRepo.save(user)), ReturnStatus.OK);
	}
	
	//util
	public List<UserDto> toDtoList(List<User> users) {
		List<UserDto> res = new ArrayList<>();
		for (User user : users) {
			res.add(conversionService.convert(user, UserDto.class));
		}
			return res;
	}

	public List<User> toEntityList(List<UserDto> users) {
		List<User> res = new ArrayList<>();
		for (UserDto user : users) {
			res.add(conversionService.convert(user, User.class));
		}
		return res;
	}
}