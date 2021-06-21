package com.dtw.assignment.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dtw.assignment.entity.Homework;

public interface HomeworkRepo extends JpaRepository<Homework, Long> {

	Optional<Homework> findByAssignmentIdAndUserId(Long assignmentId, Long userId);
}