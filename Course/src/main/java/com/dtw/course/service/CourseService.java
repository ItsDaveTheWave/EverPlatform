package com.dtw.course.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import com.dtw.commons.dto.CourseDto;
import com.dtw.course.entity.Course;
import com.dtw.course.repo.CourseRepo;

@Service
public class CourseService {

	@Autowired
	private CourseRepo courseRepo;

	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;

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

	public List<CourseDto> toDtoList(List<Course> courses) {

		List<CourseDto> res = new ArrayList<>();
		for (Course course : courses) {
			res.add(conversionService.convert(course, CourseDto.class));
		}
		return res;
	}

	public List<Course> toEntityList(List<CourseDto> courses) {

		List<Course> res = new ArrayList<>();
		for (CourseDto course : courses) {
			res.add(conversionService.convert(course, Course.class));
		}
		return res;
	}
}