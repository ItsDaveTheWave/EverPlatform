package com.dtw.oauth.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dtw.oauth.repo.UserRepo;

import com.dtw.oauth.entity.User;

@RestController
@RequestMapping("/oauth/user")
public class UserController {

	@Autowired
	private UserRepo userRepo;
	
	@PostMapping
	public ResponseEntity<User> create(@Valid @RequestBody User user) {
		return ResponseEntity.ok(userRepo.save(user));
	}
}