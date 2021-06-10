package com.dtw.course.controller;

import java.util.List;

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

import com.dtw.course.entity.Course;
import com.dtw.course.service.CourseService;

@RestController
@RequestMapping("/api/course")
public class CourseController {

	@Autowired
	private CourseService courseService;
	
	@GetMapping
	public ResponseEntity<List<Course>> getAll() {
		
		return ResponseEntity.ok(courseService.getAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Course> getOne(@PathVariable Long id) {
		
		return ResponseEntity.ok(courseService.getOne(id));
	}
	
	@PostMapping
	public ResponseEntity<Course> create(@RequestBody Course course) {
		
		return new ResponseEntity<Course>(courseService.create(course), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		
		courseService.delete(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}