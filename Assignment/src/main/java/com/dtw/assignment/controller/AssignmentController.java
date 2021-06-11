package com.dtw.assignment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dtw.assignment.entity.Assignment;
import com.dtw.assignment.service.AssignmentService;

@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {

	@Autowired
	private AssignmentService assignmentService;
	
	@GetMapping("/{id}")
	public ResponseEntity<Assignment> getOne(@PathVariable Long id) {
		
		return ResponseEntity.ok(assignmentService.getOne(id)); 
	}
	
	@PostMapping
	public ResponseEntity<Assignment> create(@RequestBody Assignment assignment) {
		return new ResponseEntity<Assignment>(assignmentService.create(assignment), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		assignmentService.delete(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}