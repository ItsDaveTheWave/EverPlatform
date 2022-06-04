package com.dtw.course.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dtw.commons.dto.AssignmentDto;
import com.dtw.commons.dto.CourseDto;
import com.dtw.commons.enums.ReturnStatus;
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
	
	//assignment
	@GetMapping("/{id}/assignment")
	public ResponseEntity<?> getAllAssignmentOfCourse(@PathVariable Long id, @RequestHeader("Authorization") String token, 
			OAuth2Authentication auth) {
		
		Pair<Optional<List<AssignmentDto>>, ReturnStatus> pair;		
		try {
			pair = courseService.getAllAssignmentOfCourse(id, token, auth);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		if(pair.getSecond() != ReturnStatus.OK) {
			if(pair.getSecond() == ReturnStatus.ENTITY_NOT_FOUND) {
				return ApiError.entityNotFound("Course", "id", id).buildResponseEntity();
			}
			if(pair.getSecond() == ReturnStatus.FORBIDDEN) {
				throw new AccessDeniedException("Access denied");
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		List<AssignmentDto> assignmentList = pair.getFirst().get();
		if(assignmentList.size() == 0) {
			return new ResponseEntity<List<AssignmentDto>>(HttpStatus.NO_CONTENT);
		}
		
		return ResponseEntity.ok(assignmentList);
	}
	
	@GetMapping("/{id}/assignment/{assignmentId}")
	public ResponseEntity<?> getAllAssignmentOfCourse(@PathVariable Long id, @PathVariable Long assignmentId, 
			@RequestHeader("Authorization") String token, OAuth2Authentication auth) {
		
		Pair<Optional<AssignmentDto>, ReturnStatus> pair;
		try {
			pair = courseService.getOneAssignmentOfCourse(id, assignmentId, token, auth);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		if(pair.getSecond() != ReturnStatus.OK) {
			if(pair.getSecond() == ReturnStatus.ENTITY_NOT_FOUND) {
				return ApiError.entityNotFound("Course", "id", id).buildResponseEntity();
			}
			if(pair.getSecond() == ReturnStatus.ENTITY_DOESNT_CONTAIN_ENTITY) {
				return ApiError.entityDoesntContainEntity("Course", "Assignment", "id", assignmentId).buildResponseEntity();
			}
			if(pair.getSecond() == ReturnStatus.FORBIDDEN) {
				throw new AccessDeniedException("Access denied");
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok(pair.getFirst().get());
	}
}