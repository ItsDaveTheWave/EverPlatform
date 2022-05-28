package com.dtw.courseAggregator.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dtw.commons.client.AssignmentClient;
import com.dtw.commons.dto.AssignmentDto;

@RestController
@RequestMapping("/api/courseAggregator")
public class CourseAggregatorController {

	@Autowired
	private AssignmentClient assignmentClient;
	
	@GetMapping("/test")
	public ResponseEntity<?> test(@RequestBody @Valid AssignmentDto assignmentDto, @RequestHeader("Authorization") String token) {
		
		return ResponseEntity.ok(assignmentClient.create(assignmentDto, token));
	}
}