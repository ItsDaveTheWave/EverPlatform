package com.dtw.course.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtw.course.entity.Course;
import com.dtw.course.repo.CourseRepo;

@Service
public class CourseService {

	@Autowired
	private CourseRepo courseRepo;
	
	public List<Course> getAll() {
		return courseRepo.findAll();
	}
	
	public Course getOne(Long id) {
		return courseRepo.findById(id).get();
	}
	
	public Course create(Course course) {
		return courseRepo.save(course);
	}
	
	public void delete(Long id) {
		courseRepo.deleteById(id);
	}
}