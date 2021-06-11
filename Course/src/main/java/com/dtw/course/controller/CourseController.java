package com.dtw.course.controller;

import java.util.List;

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

import com.dtw.course.dto.CourseDto;
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

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {

		courseService.delete(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}