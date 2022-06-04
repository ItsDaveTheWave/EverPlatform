package com.dtw.assignment.controller;

import java.io.IOException;
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

import com.dtw.assignment.entity.Assignment;
import com.dtw.assignment.service.AssignmentService;
import com.dtw.commons.dto.AssignmentDto;
import com.dtw.commons.dto.HomeworkDto;
import com.dtw.commons.enums.ReturnStatus;
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
	public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("Authorization") String token) {
		
		assignmentService.delete(id, token);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	//homework
	@GetMapping("/{id}/homework")
	public ResponseEntity<?> getAllHomeworkForAssignment(@PathVariable Long id, @RequestHeader("Authorization") String token) {

		Optional<List<HomeworkDto>> optList;
		try {
			optList = assignmentService.getAllHomeworkOfAssignment(id, token);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		if(optList.isEmpty()) {
			return ApiError.entityNotFound("Assignment", "id", id).buildResponseEntity();
		}
		
		List<HomeworkDto> homeworkList = optList.get();
		if(homeworkList.size() == 0) {
			return new ResponseEntity<List<HomeworkDto>>(HttpStatus.NO_CONTENT);
		}
		
		return ResponseEntity.ok(homeworkList);
	}
	
	@GetMapping("/{id}/homework/{homeworkId}")
	public ResponseEntity<?> getOneHomeworkFromAssigment(@PathVariable Long id, @PathVariable Long homeworkId,
			@RequestHeader("Authorization") String token) {
		
		Pair<Optional<HomeworkDto>, ReturnStatus> pair;
		try {
			pair = assignmentService.getOneHomeworkFromAssignment(id, homeworkId, token);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		if(pair.getSecond() != ReturnStatus.OK) {
			if(pair.getSecond() == ReturnStatus.ENTITY_NOT_FOUND) {
				return ApiError.entityNotFound("Assignment", "id", id).buildResponseEntity();
			}
			if(pair.getSecond() == ReturnStatus.ENTITY_DOESNT_CONTAIN_ENTITY) {
				return ApiError.entityDoesntContainEntity("Assignment", "Homework", "id", homeworkId).buildResponseEntity();
			}	
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok(pair.getFirst().get());
	}
	
	@GetMapping("/{id}/homework/{homeworkId}/download")
	public ResponseEntity<?> downloadOneHomeworkFromAssignment(@PathVariable Long id, @PathVariable Long homeworkId,
			@RequestHeader("Authorization") String token) {
		
		Pair<Optional<ResponseEntity<ByteArrayResource>>, ReturnStatus> pair;
		try {
			pair = assignmentService.downloadOneHomeworkFromAssignment(id, homeworkId, token);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		if(pair.getSecond() != ReturnStatus.OK) {
			if(pair.getSecond() == ReturnStatus.ENTITY_NOT_FOUND) {
				return ApiError.entityNotFound("Assignment", "id", id).buildResponseEntity();
			}
			if(pair.getSecond() == ReturnStatus.ENTITY_DOESNT_CONTAIN_ENTITY) {
				return ApiError.entityDoesntContainEntity("Assignment", "Homework", "id", homeworkId).buildResponseEntity();
			}	
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		ResponseEntity<ByteArrayResource> downloadResponse = pair.getFirst().get();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Disposition", downloadResponse.getHeaders().getFirst("Content-Disposition"));
		
		return ResponseEntity.ok()
				.headers(responseHeaders)
				.body(pair.getFirst().get().getBody());
	}
	
	@PostMapping("/{id}/homework/{username}")
	public ResponseEntity<?> uploadHomeworkToAssignment(@PathVariable Long id, @PathVariable String username, @RequestParam MultipartFile file, 
			@RequestHeader("Authorization") String token) {
		
		Pair<Optional<Assignment>, ReturnStatus> pair;
		try {
			pair = assignmentService.uploadHomeworkToAssignment(id, file, username, token);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		if(pair.getSecond() != ReturnStatus.OK) {
			if(pair.getSecond() == ReturnStatus.ENTITY_NOT_FOUND) {
				return ApiError.entityNotFound("Assignment", "id", id).buildResponseEntity();
			}
			if(pair.getSecond() == ReturnStatus.USERNAME_ALREADY_EXISTS) {
				return ApiError.entityAlreadyContainsEntity("Assignment", "Homework", "username", username).buildResponseEntity();
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(conversionService.convert(pair.getFirst().get(), AssignmentDto.class));
	}
	
	@DeleteMapping("/{id}/homework/{homeworkId}")
	public ResponseEntity<?> deleteHomeworkFromAssignment(@PathVariable Long id, @PathVariable Long homeworkId, 
			@RequestHeader("Authorization") String token) {
		
		ReturnStatus returnStatus = assignmentService.deleteHomeworkFromAssignment(id, homeworkId, token);
		if(returnStatus != ReturnStatus.OK) {
			if(returnStatus == ReturnStatus.ENTITY_NOT_FOUND) {
				return ApiError.entityNotFound("Assignment", "id", id).buildResponseEntity();
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}