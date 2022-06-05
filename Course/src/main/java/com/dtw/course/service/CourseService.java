package com.dtw.course.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.util.Pair;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import com.dtw.commons.dto.AssignmentDto;
import com.dtw.commons.dto.CourseDto;
import com.dtw.commons.dto.HomeworkDto;
import com.dtw.commons.enums.ReturnStatus;
import com.dtw.course.client.AssignmentClient;
import com.dtw.course.entity.Course;
import com.dtw.course.repo.CourseRepo;
import com.google.common.reflect.TypeToken;

import feign.FeignException;
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

	public void delete(Long id, String token) {
		Optional<Course> optCourse = courseRepo.findById(id);
		if(optCourse.isPresent()) {
			for(Long assignmentId : optCourse.get().getAssignments()) {
				assignmentClient.delete(assignmentId, token);
			}
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
	
	public Pair<Optional<Course>, ReturnStatus> addAssignmentToCourse(Long courseId, AssignmentDto assignmentDto, String token, 
			OAuth2Authentication auth) throws IOException {
		
		Optional<Course> optCourse = courseRepo.findById(courseId);
		if(optCourse.isEmpty()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_NOT_FOUND);
		}
		
		if(!(isAdmin(auth) || (isTeacher(auth) && isEnrolledInCourse(optCourse.get(), (String) auth.getPrincipal())))) {
			return Pair.of(Optional.empty(), ReturnStatus.FORBIDDEN);
		}
		
		AssignmentDto savedAssignment = (AssignmentDto) gsonDecoder.decode(assignmentClient.create(assignmentDto, token), AssignmentDto.class);
		Course course = optCourse.get();
		course.getAssignments().add(savedAssignment.getId());
		
		return Pair.of(Optional.of(courseRepo.save(course)), ReturnStatus.OK);
	}
	
	public ReturnStatus deleteAssignmentOfCourse(Long courseId, Long assignmentId, String token, OAuth2Authentication auth) {
		
		Optional<Course> optCourse = courseRepo.findById(courseId);
		if(optCourse.isEmpty()) {
			return ReturnStatus.ENTITY_NOT_FOUND;
		}
		
		Course course = optCourse.get();
		if(!(isAdmin(auth) || (isTeacher(auth) && isEnrolledInCourse(course, (String) auth.getPrincipal())))) {
			return ReturnStatus.FORBIDDEN;
		}
		
		if(course.getAssignments().contains(assignmentId)) {
			assignmentClient.delete(assignmentId, token);
			
			Set<Long> assignmentSet = course.getAssignments();
			assignmentSet.remove(assignmentId);
			course.setAssignments(assignmentSet);
			courseRepo.save(course);
		}
		
		return ReturnStatus.OK;
	}
	
	//homework
	public Pair<Optional<List<HomeworkDto>>, ReturnStatus> getAllHomeworkOfAssignmentOfCourse(Long courseId, Long assignmentId,
			String token, OAuth2Authentication auth) throws IOException {
		
		Optional<Course> optCourse = courseRepo.findById(courseId);
		if(optCourse.isEmpty()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_NOT_FOUND);
		}
		
		Course course = optCourse.get();
		if(!(isAdmin(auth) || (isTeacher(auth) && isEnrolledInCourse(course, (String) auth.getPrincipal())))) {
			return Pair.of(Optional.empty(), ReturnStatus.FORBIDDEN);
		}
		
		if(!course.getAssignments().contains(assignmentId)) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_DOESNT_CONTAIN_ENTITY);
		}
		
		@SuppressWarnings("unchecked")
		List<HomeworkDto> homeworkList = (List<HomeworkDto>) gsonDecoder.decode(assignmentClient.getAllHomeworkForAssignment(assignmentId, token), 
				new TypeToken<List<HomeworkDto>>() {private static final long serialVersionUID = 1L;}.getType());
		return Pair.of(Optional.of(homeworkList), ReturnStatus.OK);
	}
	
	public Pair<Optional<HomeworkDto>, ReturnStatus> getOneHomeworkOfAssignmentFromCourse(Long courseId, Long assignmentId, Long homeworkId,
			String token, OAuth2Authentication auth) throws IOException, FeignException {
		
		Optional<Course> optCourse = courseRepo.findById(courseId);
		if(optCourse.isEmpty()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_NOT_FOUND);
		}
		
		Course course = optCourse.get();
		if(!course.getAssignments().contains(assignmentId)) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_DOESNT_CONTAIN_ENTITY);
		}
		
		HomeworkDto homework = (HomeworkDto) gsonDecoder.decode(assignmentClient.getOneHomeworkFromAssigment(assignmentId, homeworkId, token), HomeworkDto.class);
		if(!(isAdmin(auth) || (isTeacher(auth) && isEnrolledInCourse(course, (String) auth.getPrincipal())) || 
				(isStudent(auth) && isEnrolledInCourse(course, (String) auth.getPrincipal()) && 
						((String) auth.getPrincipal()).equals(homework.getUsername())))) {
			return Pair.of(Optional.empty(), ReturnStatus.FORBIDDEN);
		}
		
		return Pair.of(Optional.of(homework), ReturnStatus.OK);
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