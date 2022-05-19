package com.dtw.assignment.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.dtw.assignment.entity.Assignment;
import com.dtw.assignment.entity.Homework;
import com.dtw.assignment.repo.HomeworkRepo;
import com.dtw.assignment.service.AssignmentService;
import com.dtw.commons.dto.AssignmentDto;

@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {

	@Autowired
	private AssignmentService assignmentService;
	
	@Autowired
	private HomeworkRepo homeworkRepo;

	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;
	
	@GetMapping
	public ResponseEntity<List<AssignmentDto>> getAll() {

		return ResponseEntity.ok(assignmentService.toDtoList(assignmentService.getAll()));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<AssignmentDto> getOne(@PathVariable Long id) {

		return ResponseEntity.ok(conversionService.convert(assignmentService.getOne(id), AssignmentDto.class));
	}

	@PostMapping
	public ResponseEntity<AssignmentDto> create(@RequestBody AssignmentDto assignment) {
		return new ResponseEntity<AssignmentDto>(conversionService.convert(
				assignmentService.create(conversionService.convert(assignment, Assignment.class)), AssignmentDto.class),
				HttpStatus.CREATED);
	}
	
	@GetMapping(value = "/homework/{assignmentId}/{userId}", produces = MediaType.ALL_VALUE)
	public ResponseEntity<Resource> downloadHomework(@PathVariable Long assignmentId, @PathVariable Long userId) {
		Homework homework = homeworkRepo.findByAssignmentIdAndUserId(assignmentId, userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return new ResponseEntity<Resource>(new ByteArrayResource(homework.getFile()), HttpStatus.OK);
	}
	
	@PostMapping("/homework/{assignmentId}/{userId}")
	public ResponseEntity<Void> uploadHomework(@RequestParam MultipartFile file, @PathVariable Long assignmentId, @PathVariable Long userId) throws IOException {
		Homework homework = Homework.builder().assignmentId(assignmentId).userId(userId).file(file.getBytes()).build();
		homeworkRepo.save(homework);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		assignmentService.delete(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}