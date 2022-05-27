package com.dtw.assignment.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dtw.assignment.entity.Assignment;
import com.dtw.assignment.service.AssignmentService;
import com.dtw.commons.dto.AssignmentDto;
import com.dtw.errorHandler.error.ApiError;

@RestController
@RequestMapping("/api/assignment")
@Validated
public class AssignmentController {

	@Autowired
	private AssignmentService assignmentService;

	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;
	
	@GetMapping
	public ResponseEntity<List<AssignmentDto>> getAll() {
		
		List<AssignmentDto> dtoList = assignmentService.toDtoList(assignmentService.getAll());
		
		if(dtoList.size() == 0) {
			return new ResponseEntity<List<AssignmentDto>>(HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.ok(dtoList);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getOne(@PathVariable Long id) {

		Optional<Assignment> optAssignment = assignmentService.getOne(id);
		
		if(optAssignment.isPresent()) {
			return ResponseEntity.ok(conversionService.convert(optAssignment.get(), AssignmentDto.class));
		}
		return ApiError.entityNotFound("Assignment", "id", id).buildResponseEntity();
	}

	@PostMapping
	public ResponseEntity<AssignmentDto> create(@RequestBody @Valid AssignmentDto assignment) {
		
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