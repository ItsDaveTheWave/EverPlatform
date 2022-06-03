package com.dtw.assignment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import feign.Response;

@FeignClient("homework-service")
public interface HomeworkClient {

	@GetMapping("/api/homework")
	public Response getAll(@RequestHeader("Authorization") String token);
	
	@GetMapping("/api/homework/{id}")
	public Response getOne(@PathVariable Long id, @RequestHeader("Authorization") String token);
	
	@GetMapping("/api/homework/{id}/download")
	public Response download(@PathVariable Long id, @RequestHeader("Authorization") String token);
	
	@PostMapping(value = "/api/homework/{username}", consumes = "multipart/form-data")
	public Response upload(@RequestPart MultipartFile file, @PathVariable String username, @RequestHeader("Authorization") String token);
	
	@DeleteMapping("/api/homework/{id}")
	public Response delete(@PathVariable Long id, @RequestHeader("Authorization") String token);
}