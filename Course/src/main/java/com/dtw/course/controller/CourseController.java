package com.dtw.course.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dtw.commons.dto.AssignmentDto;
import com.dtw.commons.dto.CourseDto;
import com.dtw.commons.dto.HomeworkDto;
import com.dtw.commons.enums.ReturnStatus;
import com.dtw.course.entity.Course;
import com.dtw.course.service.CourseService;
import com.dtw.errorHandler.error.ApiError;
import com.fasterxml.jackson.core.JsonProcessingException;

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
	public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("Authorization") String token) {

		courseService.delete(id, token);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	//assignment
	@GetMapping("/{id}/assignment")
	public ResponseEntity<?> getAllAssignmentFromCourse(@PathVariable Long id, @RequestHeader("Authorization") String token, 
			OAuth2Authentication auth) {
		
		Pair<Optional<List<AssignmentDto>>, ReturnStatus> pair = courseService.getAllAssignmentOfCourse(id, token, auth);
		
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
	public ResponseEntity<?> getOneAssignmentFromCourse(@PathVariable Long id, @PathVariable Long assignmentId, 
			@RequestHeader("Authorization") String token, OAuth2Authentication auth) {
		
		Pair<Optional<AssignmentDto>, ReturnStatus> pair = courseService.getOneAssignmentOfCourse(id, assignmentId, token, auth);
		
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
	
	@PostMapping("/{id}/assignment")
	public ResponseEntity<?> addAssignmentToCourse(@PathVariable Long id, @RequestBody @Valid AssignmentDto assignment,
			@RequestHeader("Authorization") String token, OAuth2Authentication auth) {
		
		Pair<Optional<Course>, ReturnStatus> pair = courseService.addAssignmentToCourse(id, assignment, token, auth);
		
		if(pair.getSecond() != ReturnStatus.OK) {
			if(pair.getSecond() == ReturnStatus.ENTITY_NOT_FOUND) {
				return ApiError.entityNotFound("Course", "id", id).buildResponseEntity();
			}
			if(pair.getSecond() == ReturnStatus.FORBIDDEN) {
				throw new AccessDeniedException("Access denied");
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok(conversionService.convert(pair.getFirst().get(), CourseDto.class));
	}
	
	@DeleteMapping("/{id}/assignment/{assignmentId}")
	public ResponseEntity<?> deleteAssignmentFromCourse(@PathVariable Long id, @PathVariable Long assignmentId, 
			@RequestHeader("Authorization") String token, OAuth2Authentication auth) {
		
		ReturnStatus returnStatus = courseService.deleteAssignmentOfCourse(id, assignmentId, token, auth);
		if(returnStatus != ReturnStatus.OK) {
			if(returnStatus == ReturnStatus.ENTITY_NOT_FOUND) {
				return ApiError.entityNotFound("Course", "id", id).buildResponseEntity();
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	
	//homework
	@GetMapping("/{id}/assignment/{assignmentId}/homework")
	public ResponseEntity<?> getAllHomeworkFromAssignmentOfCourse(@PathVariable Long id, @PathVariable Long assignmentId, 
			@RequestHeader("Authorization") String token, OAuth2Authentication auth) {
		
		Pair<Optional<List<HomeworkDto>>, ReturnStatus> pair = courseService.getAllHomeworkOfAssignmentOfCourse(id, assignmentId, token, auth);
		
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
		
		List<HomeworkDto> homeworkList = pair.getFirst().get();
		if(homeworkList.size() == 0) {
			return new ResponseEntity<List<HomeworkDto>>(HttpStatus.NO_CONTENT);
		}
		
		return ResponseEntity.ok(homeworkList);
	}
	
	@GetMapping("/{id}/assignment/{assignmentId}/homework/{homeworkId}")
	public ResponseEntity<?> getOneHomeworkFromAssignmentFromCourse(@PathVariable Long id, @PathVariable Long assignmentId, 
			@PathVariable Long homeworkId, @RequestHeader("Authorization") String token, OAuth2Authentication auth) {
		
		Pair<Optional<HomeworkDto>, ReturnStatus> pair = courseService.getOneHomeworkOfAssignmentFromCourse(id, assignmentId, homeworkId, token, auth);
		
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
	
	@GetMapping("/{id}/assignment/{assignmentId}/homework/{homeworkId}/download")
	public ResponseEntity<?> downloadOneHomeworkFromAssignmentFromCourse(@PathVariable Long id, @PathVariable Long assignmentId, 
			@PathVariable Long homeworkId, @RequestHeader("Authorization") String token, OAuth2Authentication auth) {
		
		Pair<Optional<ResponseEntity<ByteArrayResource>>, ReturnStatus> pair = courseService.downloadOneHomeworkOfAssignmentFromCourse(id, assignmentId, homeworkId, token, auth);
		
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
		
		ResponseEntity<ByteArrayResource> downloadResponse = pair.getFirst().get();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Disposition", downloadResponse.getHeaders().getFirst("Content-Disposition"));
		responseHeaders.add("Owner-Username", downloadResponse.getHeaders().getFirst("Owner-Username"));
		
		return ResponseEntity.ok()
				.headers(responseHeaders)
				.body(downloadResponse.getBody());
	}
	
	@PostMapping("/{id}/assignment/{assignmentId}/homework")
	public ResponseEntity<?> uploadHomeworkToAssignmentFromCourse(@PathVariable Long id, @PathVariable Long assignmentId, 
			@RequestParam MultipartFile file, @RequestHeader("Authorization") String token, OAuth2Authentication auth) {
		
		Pair<Optional<Course>, ReturnStatus> pair;
		try {
			pair = courseService.uploadHomeworkToAssignmentFromCourse(id, assignmentId, file, auth, token);
		} catch (JsonProcessingException e) {
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
			if(pair.getSecond() == ReturnStatus.ENTITY_ALREADY_CONTAINS_ENTITY) {
				return ApiError.entityAlreadyContainsEntity("Assignment", "Homework", "username", (String) auth.getPrincipal()).buildResponseEntity();
			}
			if(pair.getSecond() == ReturnStatus.FORBIDDEN) {
				throw new AccessDeniedException("Access denied");
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok(pair.getFirst().get());
	}
	
	@DeleteMapping("/{id}/assignment/{assignmentId}/homework/{homeworkId}")
	public ResponseEntity<?> deleteHomeworkFromAssignmentOfCourse(@PathVariable Long id, @PathVariable Long assignmentId, 
			@PathVariable Long homeworkId, @RequestHeader("Authorization") String token, OAuth2Authentication auth) {
		
		ReturnStatus returnStatus = courseService.deleteHomeworkFromAssignmentOfCourse(id, assignmentId, homeworkId, token, auth);
		if(returnStatus != ReturnStatus.OK) {
			if(returnStatus == ReturnStatus.ENTITY_NOT_FOUND) {
				return ApiError.entityNotFound("Course", "id", id).buildResponseEntity();
			}
			if(returnStatus == ReturnStatus.ENTITY_DOESNT_CONTAIN_ENTITY) {
				return ApiError.entityDoesntContainEntity("Course", "Assignment", "id", assignmentId).buildResponseEntity();
			}
			if(returnStatus == ReturnStatus.FORBIDDEN) {
				throw new AccessDeniedException("Access denied");
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	//user
	@GetMapping("/student/{username}")
	public ResponseEntity<List<CourseDto>> getAllCourseOfStudent(@PathVariable String username, OAuth2Authentication auth) {
		Optional<List<Course>> optCourseList = courseService.getAllCoursesOfStudent(username, auth);
		if(optCourseList.isEmpty()) {
			throw new AccessDeniedException("Access denied");
		}
		
		List<Course> courseList = optCourseList.get();
		if(courseList.size() == 0) {
			return new ResponseEntity<List<CourseDto>>(HttpStatus.NO_CONTENT);
		}
		
		return ResponseEntity.ok(courseService.toDtoList(courseList));
	}
	
	@GetMapping("/teacher/{username}")
	public ResponseEntity<List<CourseDto>> getAllCourseOfTeacher(@PathVariable String username, OAuth2Authentication auth) {
		Optional<List<Course>> optCourseList = courseService.getAllCoursesOfTeacher(username, auth);
		if(optCourseList.isEmpty()) {
			throw new AccessDeniedException("Access denied");
		}
		
		List<Course> courseList = optCourseList.get();
		if(courseList.size() == 0) {
			return new ResponseEntity<List<CourseDto>>(HttpStatus.NO_CONTENT);
		}
		
		return ResponseEntity.ok(courseService.toDtoList(courseList));
	}
}