package com.dtw.course.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.dtw.commons.dto.UserDto;

import feign.Response;

@FeignClient(name = "user-service")
public interface UserClient {

	@GetMapping("/api/user")
	public Response getAllUser(@RequestHeader("Authorization") String token);
	
	@GetMapping("/api/user/{username}")
	public Response getOneUserByUsername(@PathVariable String username, OAuth2Authentication auth, @RequestHeader("Authorization") String token);
	
	@PostMapping("/api/user/admin")
	public Response createAdmin(@RequestBody UserDto userDto, @RequestHeader("Authorization") String token);

	@PostMapping("/api/user/student")
	public Response createStudent(@RequestBody UserDto userDto, @RequestHeader("Authorization") String token);
	
	@PostMapping("/api/user/teacher")
	public Response createTeacher(@RequestBody UserDto userDto, @RequestHeader("Authorization") String token);
}