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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dtw.assignment.client.HomeworkClient;
import com.dtw.assignment.entity.Assignment;
import com.dtw.assignment.repo.AssignmentRepo;
import com.dtw.commons.dto.AssignmentDto;
import com.dtw.commons.dto.HomeworkDto;

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
	
	public void delete(Long id) {
		if(assignmentRepo.findById(id).isPresent()) {
			assignmentRepo.deleteById(id);
		}
	}
	
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
	
	//homework
	public Optional<List<HomeworkDto>> getAllHomeworkOfAssignment(Long assignmentId, String token) {
		
		Optional<Assignment> optAssignment = assignmentRepo.findById(assignmentId);
		if(!optAssignment.isPresent()) {
			return Optional.empty();
		}
		
		List<HomeworkDto> homeworkList = new ArrayList<>();
		for(Long homeworkId : optAssignment.get().getHomeworkIds()) {
			homeworkList.add(homeworkClient.getOne(homeworkId, token));
		}
		
		return Optional.of(homeworkList);
	}
	
	public Pair<Optional<HomeworkDto>, String> getOneHomeworkFromAssignment(Long assignmentId, Long homeworkId, String token) {

		Optional<Assignment> optAssignment = assignmentRepo.findById(assignmentId);
		if(!optAssignment.isPresent()) {
			return Pair.of(Optional.empty(), "notFound");
		}
		
		if(!optAssignment.get().getHomeworkIds().contains(homeworkId)) {
			return Pair.of(Optional.empty(), "doesntContain");
		}
		
		HomeworkDto homework = homeworkClient.getOne(homeworkId, token);
		return Pair.of(Optional.of(homework), "ok");
	}
	
	public Pair<Optional<ByteArrayResource>, String> downloadOneHomeworkFromAssignment(Long assignmentId, Long homeworkId, String token) {
		
		Optional<Assignment> optAssignment = assignmentRepo.findById(assignmentId);
		if(!optAssignment.isPresent()) {
			return Pair.of(Optional.empty(), "notFound");
		}
		
		if(!optAssignment.get().getHomeworkIds().contains(homeworkId)) {
			return Pair.of(Optional.empty(), "doesntContain");
		}
		
		ByteArrayResource bytes = homeworkClient.download(homeworkId, token);		
		return Pair.of(Optional.of(bytes), "ok");
	}
	
	public Optional<Assignment> uploadHomeworkToAssignment(Long assignmentId, MultipartFile file, String username, String token) {
		
		Optional<Assignment> optAssignment = assignmentRepo.findById(assignmentId);
		if(!optAssignment.isPresent()) {
			return Optional.empty();
		}
		
		HomeworkDto homework = homeworkClient.upload(file, username, token);
		
		Assignment assignment = optAssignment.get();
		assignment.getHomeworkIds().add(homework.getId());
		assignmentRepo.save(assignment);
		
		return Optional.of(assignment);
	}
	
	public String deleteHomeworkFromAssignment(Long assignmentId, Long homeworkId, String token) {
		
		Optional<Assignment> optAssignment = assignmentRepo.findById(assignmentId);
		if(!optAssignment.isPresent()) {
			return "notFound";
		}
		
		Assignment assignment = optAssignment.get();
		if(!assignment.getHomeworkIds().contains(homeworkId)) {
			return "doesntContain";
		}
		
		homeworkClient.delete(homeworkId, token);
		
		Set<Long> homeworkIdSet = assignment.getHomeworkIds();
		homeworkIdSet.remove(homeworkId);
		assignment.setHomeworkIds(homeworkIdSet);
		assignmentRepo.save(assignment);
		
		return "ok";
	}
}