package com.dtw.user.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
	
	public Pair<Optional<User>, ReturnStatus> create(User user, String role) {
		if(userRepo.findByUsername(user.getUsername()).isPresent()) {
			return Pair.of(Optional.empty(), ReturnStatus.USERNAME_ALREADY_EXISTS);
		}
		if(userRepo.findByEmail(user.getEmail()).isPresent()) {
			return Pair.of(Optional.empty(), ReturnStatus.EMAIL_ALREADY_EXISTS);
		}
		
		user.setRoles(Collections.singletonList(roleRepo.findByName(role).get()));
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return Pair.of(Optional.of(userRepo.save(user)), ReturnStatus.OK);
	}
	
	public Optional<User> findByUsername(String username) {
		return userRepo.findByUsername(username);
	}
}