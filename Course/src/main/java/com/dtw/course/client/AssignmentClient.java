package com.dtw.course.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.dtw.commons.dto.AssignmentDto;

import feign.Response;

@FeignClient(name = "assignment-service")
public interface AssignmentClient {

	@GetMapping("/api/assignment")
	public Response getAll(@RequestHeader("Authorization") String token);
	
	@GetMapping("/api/assignment/{id}")
	public Response getOne(@PathVariable Long id, @RequestHeader("Authorization") String token);
	
	@PostMapping("/api/assignment")
	public Response create(@RequestBody AssignmentDto assignmentDto, @RequestHeader("Authorization") String token);
	
	@DeleteMapping("/api/assignment/{id}")
	public Response delete(@PathVariable Long id, @RequestHeader("Authorization") String token);
	
	//homework
	@GetMapping("/api/assignment/{id}/homework")
	public Response getAllHomeworkForAssignment(@PathVariable Long id, @RequestHeader("Authorization") String token);
	
	@GetMapping("/api/assignment/{id}/homework/{homeworkId}")
	public Response getOneHomeworkFromAssigment(@PathVariable Long id, @PathVariable Long homeworkId,
			@RequestHeader("Authorization") String token);
	
	@GetMapping("/api/assignment/{id}/homework/{homeworkId}/download")
	public Response downloadOneHomeworkFromAssignment(@PathVariable Long id, @PathVariable Long homeworkId,
			@RequestHeader("Authorization") String token);
	
	@PostMapping(value = "/api/assignment/{id}/homework/{username}", consumes = "multipart/form-data")
	public Response uploadHomeworkToAssignment(@PathVariable Long id, @PathVariable String username, @RequestPart MultipartFile file, 
			@RequestHeader("Authorization") String token);
	
	@DeleteMapping("/api/assignment/{id}/homework/{homeworkId}")
	public Response deleteHomeworkFromAssignment(@PathVariable Long id, @PathVariable Long homeworkId, 
			@RequestHeader("Authorization") String token);
}