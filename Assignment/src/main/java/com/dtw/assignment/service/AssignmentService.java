package com.dtw.assignment.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import com.dtw.assignment.entity.Assignment;
import com.dtw.assignment.repo.AssignmentRepo;
import com.dtw.commons.dto.AssignmentDto;

@Service
public class AssignmentService {

	@Autowired
	private AssignmentRepo assignmentRepo;
	
	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;
	
	public List<Assignment> getAll() {
		return assignmentRepo.findAll();
	}
	
	public Assignment getOne(Long id) {
		return assignmentRepo.findById(id).get();
	}
	
	public Assignment create(Assignment assignment) {
		return assignmentRepo.save(assignment);
	}
	
	public void delete(Long id) {
		assignmentRepo.deleteById(id);
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
}