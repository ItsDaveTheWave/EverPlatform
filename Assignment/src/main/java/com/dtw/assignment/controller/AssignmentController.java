package com.dtw.assignment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dtw.assignment.dto.AssignmentDto;
import com.dtw.assignment.entity.Assignment;
import com.dtw.assignment.service.AssignmentService;

@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {

	@Autowired
	private AssignmentService assignmentService;

	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;

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

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		assignmentService.delete(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}