package com.dtw.assignment.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dtw.assignment.client.HomeworkClient;
import com.dtw.assignment.entity.Assignment;
import com.dtw.assignment.repo.AssignmentRepo;
import com.dtw.commons.dto.AssignmentDto;
import com.dtw.commons.dto.HomeworkDto;
import com.dtw.commons.enums.ReturnStatus;

@Service
public class AssignmentService {

	@Autowired
	private AssignmentRepo assignmentRepo;
	
	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;
	
	@Autowired
	private HomeworkClient homeworkClient;
	
	
	public List<Assignment> getAll() {
		return assignmentRepo.findAll();
	}
	
	public Optional<Assignment> getOne(Long id) {
		return assignmentRepo.findById(id);
	}
	
	public Assignment create(Assignment assignment) {
		return assignmentRepo.save(assignment);
	}
	
	public void delete(Long id, String token) {
		Optional<Assignment> optAssignment = assignmentRepo.findById(id);
		if(optAssignment.isPresent()) {
			for(Long homeworkId : optAssignment.get().getHomeworkIds()) {
				homeworkClient.delete(homeworkId, token);
			}
			assignmentRepo.deleteById(id);
		}
	}
	
	//homework
	public Optional<List<HomeworkDto>> getAllHomeworkOfAssignment(Long assignmentId, String token) {
		
		Optional<Assignment> optAssignment = assignmentRepo.findById(assignmentId);
		if(optAssignment.isEmpty()) {
			return Optional.empty();
		}
		
		List<HomeworkDto> homeworkList = new ArrayList<>();
		for(Long homeworkId : optAssignment.get().getHomeworkIds()) {
			homeworkList.add(homeworkClient.getOne(homeworkId, token));
		}
		
		return Optional.of(homeworkList);
	}
	
	public Pair<Optional<HomeworkDto>, ReturnStatus> getOneHomeworkFromAssignment(Long assignmentId, Long homeworkId, String token) {

		Optional<Assignment> optAssignment = assignmentRepo.findById(assignmentId);
		if(optAssignment.isEmpty()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_NOT_FOUND);
		}
		
		if(!optAssignment.get().getHomeworkIds().contains(homeworkId)) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_DOESNT_CONTAIN_ENTITY);
		}
		
		HomeworkDto homework = homeworkClient.getOne(homeworkId, token);
		return Pair.of(Optional.of(homework), ReturnStatus.OK);
	}
	
	public Pair<Optional<ResponseEntity<ByteArrayResource>>, ReturnStatus> downloadOneHomeworkFromAssignment(Long assignmentId, Long homeworkId, String token) {
		
		Optional<Assignment> optAssignment = assignmentRepo.findById(assignmentId);
		if(optAssignment.isEmpty()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_NOT_FOUND);
		}
		
		if(!optAssignment.get().getHomeworkIds().contains(homeworkId)) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_DOESNT_CONTAIN_ENTITY);
		}
		
		ResponseEntity<ByteArrayResource> response = homeworkClient.download(homeworkId, token);
		ResponseEntity<ByteArrayResource> responseEntity = ResponseEntity.status(response.getStatusCode())
				.headers(response.getHeaders())
				.body(response.getBody());
		
		return Pair.of(Optional.of(responseEntity), ReturnStatus.OK);
	}
	
	public Pair<Optional<Assignment>, ReturnStatus> uploadHomeworkToAssignment(Long assignmentId, MultipartFile file, String username, String token) {
		
		Optional<Assignment> optAssignment = assignmentRepo.findById(assignmentId);
		if(optAssignment.isEmpty()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_NOT_FOUND);
		}

		//check if any of the assignment's homework has the user name already
		if(this.getAllHomeworkOfAssignment(assignmentId, token).get().stream().anyMatch(homework -> homework.getUsername().equals(username))) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_WITH_USERNAME_ALREADY_EXISTS);
		}
		
		HomeworkDto homework = homeworkClient.upload(file, username, token);
		
		Assignment assignment = optAssignment.get();
		assignment.getHomeworkIds().add(homework.getId());
		assignmentRepo.save(assignment);
		
		return Pair.of(Optional.of(assignment), ReturnStatus.OK);
	}
	
	public ReturnStatus deleteHomeworkFromAssignment(Long assignmentId, Long homeworkId, String token) {
		
		Optional<Assignment> optAssignment = assignmentRepo.findById(assignmentId);
		if(optAssignment.isEmpty()) {
			return ReturnStatus.ENTITY_NOT_FOUND;
		}
		
		Assignment assignment = optAssignment.get();
		if(assignment.getHomeworkIds().contains(homeworkId)) {
			homeworkClient.delete(homeworkId, token);
			
			Set<Long> homeworkIdSet = assignment.getHomeworkIds();
			homeworkIdSet.remove(homeworkId);
			assignment.setHomeworkIds(homeworkIdSet);
			assignmentRepo.save(assignment);
		}
		
		return ReturnStatus.OK;
	}
	
	
	//util
	public List<AssignmentDto> toDtoList(List<Assignment> assignments) {

		List<AssignmentDto> res = new ArrayList<>();
		for (Assignment assignment : assignments) {
			res.add(conversionService.convert(assignment, AssignmentDto.class));
		}
		return res;
	}

	public List<Assignment> toEntityList(List<AssignmentDto> assignments) {

		List<Assignment> res = new ArrayList<>();
		for (AssignmentDto assignment : assignments) {
			res.add(conversionService.convert(assignment, Assignment.class));
		}
		return res;
	}
}