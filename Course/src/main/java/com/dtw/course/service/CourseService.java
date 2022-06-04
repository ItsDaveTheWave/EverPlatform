package com.dtw.course.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.util.Pair;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import com.dtw.commons.dto.AssignmentDto;
import com.dtw.commons.dto.CourseDto;
import com.dtw.commons.enums.ReturnStatus;
import com.dtw.course.client.AssignmentClient;
import com.dtw.course.entity.Course;
import com.dtw.course.repo.CourseRepo;

import feign.gson.GsonDecoder;

@Service
public class CourseService {

	@Autowired
	private CourseRepo courseRepo;

	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;
	
	@Autowired
	private GsonDecoder gsonDecoder;
	
	@Autowired
	private AssignmentClient assignmentClient;
	

	public List<Course> getAll() {
		return courseRepo.findAll();
	}

	public Optional<Course> getOne(Long id) {
		return courseRepo.findById(id);
	}

	public Course create(Course course) {
		return courseRepo.save(course);
	}

	public void delete(Long id) {
		if(courseRepo.findById(id).isPresent()) {
			courseRepo.deleteById(id);
		}
	}
	
	
	//assignment
	public Pair<Optional<List<AssignmentDto>>, ReturnStatus> getAllAssignmentOfCourse(Long courseId, String token, 
			OAuth2Authentication auth) throws IOException {
		
		Optional<Course> optCourse = courseRepo.findById(courseId);
		if(optCourse.isEmpty()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_NOT_FOUND);
		}
		
		if(!(isAdmin(auth) || ((isTeacher(auth) || isStudent(auth)) && isEnrolledInCourse(optCourse.get(), (String) auth.getPrincipal())))) {
			return Pair.of(Optional.empty(), ReturnStatus.FORBIDDEN);
		}
		
		List<AssignmentDto> assignmentList = new ArrayList<>();
		for(Long assignmentId : optCourse.get().getAssignments()) {
			assignmentList.add((AssignmentDto) gsonDecoder.decode(assignmentClient.getOne(assignmentId, token), AssignmentDto.class));
		}
		
		return Pair.of(Optional.of(assignmentList), ReturnStatus.OK);
	}
	
	public Pair<Optional<AssignmentDto>, ReturnStatus> getOneAssignmentOfCourse(Long courseId, Long assignmentId, String token, 
			OAuth2Authentication auth) throws IOException {
		
		Optional<Course> optCourse = courseRepo.findById(courseId);
		if(optCourse.isEmpty()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_NOT_FOUND);
		}
		
		if(!(isAdmin(auth) || ((isTeacher(auth) || isStudent(auth)) && isEnrolledInCourse(optCourse.get(), (String) auth.getPrincipal())))) {
			return Pair.of(Optional.empty(), ReturnStatus.FORBIDDEN);
		}
		
		if(!optCourse.get().getAssignments().contains(assignmentId)) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_DOESNT_CONTAIN_ENTITY);
		}
		
		AssignmentDto assignment = (AssignmentDto) gsonDecoder.decode(assignmentClient.getOne(assignmentId, token), AssignmentDto.class);
		return Pair.of(Optional.of(assignment), ReturnStatus.OK);
	}
	
	
	//util
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
	
	private boolean isAdmin(OAuth2Authentication auth) {
		return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_admin"));
	}
	
	private boolean isTeacher(OAuth2Authentication auth) {
		return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_teacher"));
	}
	
	private boolean isStudent(OAuth2Authentication auth) {
		return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_student"));
	}
	
	private boolean isEnrolledInCourse(Course course, String username) {
		return course.getTeacher().equals(username) || course.getStudents().contains(username);
	}
}