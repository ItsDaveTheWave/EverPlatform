package com.dtw.courseAggregator.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.dtw.commons.dto.AssignmentDto;

@FeignClient(name = "assignment-service")
public interface AssignmentClient {

	@GetMapping("/api/assignment")
	public List<AssignmentDto> getAll(@RequestHeader("Authorization") String token);
	
	@GetMapping("/api/assignment/{id}")
	public AssignmentDto getOne(@PathVariable Long id, @RequestHeader("Authorization") String token);
	
	@PostMapping("/api/assignment")
	public AssignmentDto create(@RequestBody AssignmentDto assignmentDto, @RequestHeader("Authorization") String token);
	
	@DeleteMapping("/api/assignment/{id}")
	public Void delete(@PathVariable Long id, @RequestHeader("Authorization") String token);
}