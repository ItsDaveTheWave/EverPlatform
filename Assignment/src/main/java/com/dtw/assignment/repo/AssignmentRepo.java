package com.dtw.assignment.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dtw.assignment.entity.Assignment;

public interface AssignmentRepo extends JpaRepository<Assignment, Long>{

}