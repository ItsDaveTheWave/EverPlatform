package com.dtw.course.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.dtw.commons.dto.AssignmentDto;
import com.dtw.commons.dto.HomeworkDto;

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
	
	//homework
	@GetMapping("/api/assignment/{id}/homework")
	public List<HomeworkDto> getAllHomeworkForAssignment(@PathVariable Long id, @RequestHeader("Authorization") String token);
	
	@GetMapping("/api/assignment/{id}/homework/{homeworkId}")
	public HomeworkDto getOneHomeworkFromAssigment(@PathVariable Long id, @PathVariable Long homeworkId,
			@RequestHeader("Authorization") String token);
	
	@GetMapping("/api/assignment/{id}/homework/{homeworkId}/download")
	public ResponseEntity<ByteArrayResource> downloadOneHomeworkFromAssignment(@PathVariable Long id, @PathVariable Long homeworkId,
			@RequestHeader("Authorization") String token);
	
	@PostMapping(value = "/api/assignment/{id}/homework/{username}", consumes = "multipart/form-data")
	public HomeworkDto uploadHomeworkToAssignment(@PathVariable Long id, @PathVariable String username, @RequestPart MultipartFile file, 
			@RequestHeader("Authorization") String token);
	
	@DeleteMapping("/api/assignment/{id}/homework/{homeworkId}")
	public Void deleteHomeworkFromAssignment(@PathVariable Long id, @PathVariable Long homeworkId, 
			@RequestHeader("Authorization") String token);
}