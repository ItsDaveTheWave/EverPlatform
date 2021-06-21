package com.dtw.course.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dtw.commons.dto.AssignmentDto;
import com.dtw.commons.dto.CourseDto;
import com.dtw.course.client.AssignmentClient;
import com.dtw.course.entity.Course;
import com.dtw.course.service.CourseService;

@RestController
@RequestMapping("/api/course")
public class CourseController {

	@Autowired
	private CourseService courseService;

	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;
	
	@Autowired
	private AssignmentClient assignmentClient;

	@GetMapping
	public ResponseEntity<List<CourseDto>> getAll() {

		return ResponseEntity.ok(courseService.toDtoList(courseService.getAll()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<CourseDto> getOne(@PathVariable Long id) {

		return ResponseEntity.ok(conversionService.convert(courseService.getOne(id), CourseDto.class));
	}

	@PostMapping
	public ResponseEntity<CourseDto> create(@RequestBody CourseDto course) {

		return new ResponseEntity<CourseDto>(conversionService
				.convert(courseService.create(conversionService.convert(course, Course.class)), CourseDto.class),
				HttpStatus.CREATED);
	}
	
	@PatchMapping("/addTeacher/{courseId}/{teacherId}")
	public ResponseEntity<CourseDto> addTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
		Set<Long> teachers = new HashSet<>();
		teachers.add(teacherId);
		Course course = courseService.getOne(courseId);
		course.setTeachers(teachers);
		return ResponseEntity.ok(conversionService.convert(courseService.create(course), CourseDto.class));
	}
	
	@PatchMapping("/addStudent/{courseId}/{studentId}")
	public ResponseEntity<CourseDto> addStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
		Set<Long> students = new HashSet<>();
		students.add(studentId);
		Course course = courseService.getOne(courseId);
		course.setStudents(students);
		return ResponseEntity.ok(conversionService.convert(courseService.create(course), CourseDto.class));
	}
	
	@DeleteMapping("/removeTeacher/{courseId}/{teacherId}")
	public ResponseEntity<CourseDto> removeTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
		Course course = courseService.getOne(courseId);
		course.getTeachers().remove(teacherId);
		return ResponseEntity.ok(conversionService.convert(courseService.create(course), CourseDto.class));
	}
	
	@DeleteMapping("/removeStudent/{courseId}/{studentId}")
	public ResponseEntity<CourseDto> removeStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
		Course course = courseService.getOne(courseId);
		course.getStudents().remove(studentId);
		return ResponseEntity.ok(conversionService.convert(courseService.create(course), CourseDto.class));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {

		courseService.delete(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/assignment/{id}")
	public ResponseEntity<AssignmentDto> getOneAssignment(@PathVariable Long id) {
		return ResponseEntity.ok(assignmentClient.getOne(id));
	}
}