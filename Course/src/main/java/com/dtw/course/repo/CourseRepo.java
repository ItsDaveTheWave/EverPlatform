package com.dtw.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dtw.course.entity.Course;

public interface CourseRepo extends JpaRepository<Course, Long> {

}