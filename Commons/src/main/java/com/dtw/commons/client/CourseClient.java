package com.dtw.commons.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.dtw.commons.dto.CourseDto;

@FeignClient("course-service")
public interface CourseClient {
	
	@GetMapping("/api/course")
	public List<CourseDto> getAll(@RequestHeader("Authorization") String token);
	
	@GetMapping("/api/course/{id}")
	public CourseDto getOne(@PathVariable Long id, @RequestHeader("Authorization") String token);
	
	@PostMapping("/api/course")
	public CourseDto create(@RequestBody CourseDto courseDto, @RequestHeader("Authorization") String token);
	
	@DeleteMapping("/api/course/{id}")
	public Void delete(@PathVariable Long id, @RequestHeader("Authorization") String token);
}