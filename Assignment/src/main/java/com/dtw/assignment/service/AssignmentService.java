package com.dtw.assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtw.assignment.entity.Assignment;
import com.dtw.assignment.repo.AssignmentRepo;

@Service
public class AssignmentService {

	@Autowired
	private AssignmentRepo assignmentRepo;
	
	public Assignment getOne(Long id) {
		return assignmentRepo.findById(id).get();
	}
	
	public Assignment create(Assignment assignment) {
		return assignmentRepo.save(assignment);
	}
	
	public void delete(Long id) {
		assignmentRepo.deleteById(id);
	}
}