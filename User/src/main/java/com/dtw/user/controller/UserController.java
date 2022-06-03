package com.dtw.user.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dtw.commons.dto.UserDto;
import com.dtw.commons.enums.ReturnStatus;
import com.dtw.errorHandler.error.ApiError;
import com.dtw.user.entity.User;
import com.dtw.user.service.UserService;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;
	
	@GetMapping("/{username}")
	public ResponseEntity<?> getUser(@PathVariable String username, OAuth2Authentication auth) {
		
		if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_admin")) ||
				username.equals(auth.getPrincipal())) {
			Optional<User> optUser = userService.findByUsername(username);
			if(optUser.isPresent()) {
				return ResponseEntity.ok(conversionService.convert(optUser.get(), UserDto.class));
			}
			else {
				return ApiError.entityNotFound("User", "username", username).buildResponseEntity();
			}
		}

		throw new AccessDeniedException("Access denied");
	}
	
	@PostMapping("/admin")
	public ResponseEntity<?> createAdmin(@RequestBody @Valid UserDto userDto) {
		
		Pair<Optional<User>, ReturnStatus> pair = userService.create(conversionService.convert(userDto, User.class), "ROLE_admin");
		if(!pair.getFirst().isPresent()) {
			if(pair.getSecond() == ReturnStatus.USERNAME_NOT_FOUND) {
				return ApiError.entityAlreadyExists("User", "username", userDto.getUsername()).buildResponseEntity();
			}
			if(pair.getSecond() == ReturnStatus.EMAIL_NOT_FOUND) {
				return ApiError.entityAlreadyExists("User", "email", userDto.getEmail()).buildResponseEntity();		
			}
		}
		
		return new ResponseEntity<UserDto>(conversionService.convert(pair.getFirst().get(), UserDto.class), HttpStatus.CREATED);
	}
	
	@PostMapping("/student")
	public ResponseEntity<?> createStudent(@RequestBody @Valid UserDto userDto) {
		
		Pair<Optional<User>, ReturnStatus> pair = userService.create(conversionService.convert(userDto, User.class), "ROLE_student");
		if(!pair.getFirst().isPresent()) {
			if(pair.getSecond() == ReturnStatus.USERNAME_NOT_FOUND) {
				return ApiError.entityAlreadyExists("User", "username", userDto.getUsername()).buildResponseEntity();
			}
			if(pair.getSecond() == ReturnStatus.EMAIL_NOT_FOUND) {
				return ApiError.entityAlreadyExists("User", "email", userDto.getEmail()).buildResponseEntity();		
			}
		}
		
		return new ResponseEntity<UserDto>(conversionService.convert(pair.getFirst().get(), UserDto.class), HttpStatus.CREATED);
	}
	
	@PostMapping("/teacher")
	public ResponseEntity<?> createTeacher(@RequestBody @Valid UserDto userDto) {
		
		Pair<Optional<User>, ReturnStatus> pair = userService.create(conversionService.convert(userDto, User.class), "ROLE_teacher");
		if(!pair.getFirst().isPresent()) {
			if(pair.getSecond() == ReturnStatus.USERNAME_NOT_FOUND) {
				return ApiError.entityAlreadyExists("User", "username", userDto.getUsername()).buildResponseEntity();
			}
			if(pair.getSecond() == ReturnStatus.EMAIL_NOT_FOUND) {
				return ApiError.entityAlreadyExists("User", "email", userDto.getEmail()).buildResponseEntity();		
			}
		}
		
		return new ResponseEntity<UserDto>(conversionService.convert(pair.getFirst().get(), UserDto.class), HttpStatus.CREATED);
	}
}
