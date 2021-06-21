package com.dtw.oauth.controller;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dtw.oauth.entity.Role;
import com.dtw.oauth.entity.User;
import com.dtw.oauth.enums.RoleEnum;
import com.dtw.oauth.repo.UserRepo;

@RestController
@RequestMapping("/oauth/admin")
public class AdminController {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping
	public ResponseEntity<User> create(@Valid @RequestBody User user) {
		Set<Role> roles = new HashSet<>();
		roles.add(Role.builder().id(2L).name(RoleEnum.ROLE_ADMIN).build());
		user.setRoles(roles);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return ResponseEntity.ok(userRepo.save(user));
	}
}