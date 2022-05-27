package com.dtw.course.controller;

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

import com.dtw.commons.dto.CourseDto;
import com.dtw.course.entity.Course;
import com.dtw.course.service.CourseService;
import com.dtw.errorHandler.error.ApiError;

@RestController
@RequestMapping("/api/course")
@Validated
public class CourseController {

	@Autowired
	private CourseService courseService;

	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;
	
	@GetMapping
	public ResponseEntity<List<CourseDto>> getAll() {
		
		List<CourseDto> dtoList = courseService.toDtoList(courseService.getAll());
		
		if(dtoList.size() == 0) {
			return new ResponseEntity<List<CourseDto>>(HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.ok(dtoList);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getOne(@PathVariable Long id) {

		Optional<Course> optCourse = courseService.getOne(id);
		
		if(optCourse.isPresent()) {
			return ResponseEntity.ok(conversionService.convert(optCourse.get(), CourseDto.class));
		}
		return ApiError.entityNotFound("Course", "id", id).buildResponseEntity();
	}

	@PostMapping
	public ResponseEntity<CourseDto> create(@RequestBody @Valid CourseDto course) {

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