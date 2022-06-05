package com.dtw.course.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.dtw.commons.dto.AssignmentDto;
import com.dtw.commons.dto.CourseDto;
import com.dtw.commons.dto.HomeworkDto;
import com.dtw.commons.enums.ReturnStatus;
import com.dtw.course.client.AssignmentClient;
import com.dtw.course.entity.Course;
import com.dtw.course.repo.CourseRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CourseService {

	@Autowired
	private CourseRepo courseRepo;

	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
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
			OAuth2Authentication auth) {
		
		Optional<Course> optCourse = courseRepo.findById(courseId);
		if(optCourse.isEmpty()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_NOT_FOUND);
		}
		
		if(!(isAdmin(auth) || ((isTeacher(auth) || isStudent(auth)) && isEnrolledInCourse(optCourse.get(), (String) auth.getPrincipal())))) {
			return Pair.of(Optional.empty(), ReturnStatus.FORBIDDEN);
		}
		
		List<AssignmentDto> assignmentList = new ArrayList<>();
		for(Long assignmentId : optCourse.get().getAssignments()) {
			assignmentList.add(assignmentClient.getOne(assignmentId, token));
		}
		
		return Pair.of(Optional.of(assignmentList), ReturnStatus.OK);
	}
	
	public Pair<Optional<AssignmentDto>, ReturnStatus> getOneAssignmentOfCourse(Long courseId, Long assignmentId, String token, 
			OAuth2Authentication auth) {
		
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
		
		AssignmentDto assignment = assignmentClient.getOne(assignmentId, token);
		return Pair.of(Optional.of(assignment), ReturnStatus.OK);
	}
	
	public Pair<Optional<Course>, ReturnStatus> addAssignmentToCourse(Long courseId, AssignmentDto assignmentDto, String token, 
			OAuth2Authentication auth) {
		
		Optional<Course> optCourse = courseRepo.findById(courseId);
		if(optCourse.isEmpty()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_NOT_FOUND);
		}
		
		if(!(isAdmin(auth) || (isTeacher(auth) && isEnrolledInCourse(optCourse.get(), (String) auth.getPrincipal())))) {
			return Pair.of(Optional.empty(), ReturnStatus.FORBIDDEN);
		}
		
		AssignmentDto savedAssignment = assignmentClient.create(assignmentDto, token);
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
			String token, OAuth2Authentication auth) {
		
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
		
		List<HomeworkDto> homeworkList = assignmentClient.getAllHomeworkForAssignment(assignmentId, token);
		return Pair.of(Optional.of(homeworkList), ReturnStatus.OK);
	}
	
	public Pair<Optional<HomeworkDto>, ReturnStatus> getOneHomeworkOfAssignmentFromCourse(Long courseId, Long assignmentId, Long homeworkId,
			String token, OAuth2Authentication auth) {
		
		Optional<Course> optCourse = courseRepo.findById(courseId);
		if(optCourse.isEmpty()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_NOT_FOUND);
		}
		
		Course course = optCourse.get();
		if(!course.getAssignments().contains(assignmentId)) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_DOESNT_CONTAIN_ENTITY);
		}
		
		HomeworkDto homework = assignmentClient.getOneHomeworkFromAssigment(assignmentId, homeworkId, token);
		if(!(isAdmin(auth) || (isTeacher(auth) && isEnrolledInCourse(course, (String) auth.getPrincipal())) || 
				(isStudent(auth) && isEnrolledInCourse(course, (String) auth.getPrincipal()) && 
						((String) auth.getPrincipal()).equals(homework.getUsername())))) {
			return Pair.of(Optional.empty(), ReturnStatus.FORBIDDEN);
		}
		
		return Pair.of(Optional.of(homework), ReturnStatus.OK);
	}
	
	public Pair<Optional<ResponseEntity<ByteArrayResource>>, ReturnStatus> downloadOneHomeworkOfAssignmentFromCourse(Long courseId, Long assignmentId, Long homeworkId,
			String token, OAuth2Authentication auth) {
		
		Optional<Course> optCourse = courseRepo.findById(courseId);
		if(optCourse.isEmpty()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_NOT_FOUND);
		}
		
		Course course = optCourse.get();
		if(!course.getAssignments().contains(assignmentId)) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_DOESNT_CONTAIN_ENTITY);
		}
		
		ResponseEntity<ByteArrayResource> response = assignmentClient.downloadOneHomeworkFromAssignment(assignmentId, homeworkId, token);
		if(!(isAdmin(auth) || (isTeacher(auth) && isEnrolledInCourse(course, (String) auth.getPrincipal())) || 
				(isStudent(auth) && isEnrolledInCourse(course, (String) auth.getPrincipal()) && 
						((String) auth.getPrincipal()).equals(response.getHeaders().getFirst("Owner-Username"))))) {
			return Pair.of(Optional.empty(), ReturnStatus.FORBIDDEN);
		}
		
		ResponseEntity<ByteArrayResource> responseEntity = ResponseEntity.status(response.getStatusCode())
				.headers(response.getHeaders())
				.body(response.getBody());
		return Pair.of(Optional.of(responseEntity), ReturnStatus.OK);
	}
	
	public Pair<Optional<Course>, ReturnStatus> uploadHomeworkToAssignmentFromCourse(Long courseId, Long assignmentId, MultipartFile file,
			OAuth2Authentication auth, String token) throws JsonMappingException, JsonProcessingException {
		
		Optional<Course> optCourse = courseRepo.findById(courseId);
		if(optCourse.isEmpty()) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_NOT_FOUND);
		}
		
		Course course = optCourse.get();
		if(!course.getAssignments().contains(assignmentId)) {
			return Pair.of(Optional.empty(), ReturnStatus.ENTITY_DOESNT_CONTAIN_ENTITY);
		}
		
		if(!(isAdmin(auth) || (isStudent(auth) && isEnrolledInCourse(course, (String) auth.getPrincipal())))) {
			return Pair.of(Optional.empty(), ReturnStatus.FORBIDDEN);
		}
		
		//check if user has already submited a homework for this assignment
		String adminToken = "Bearer " + getAdminToken();
		List<HomeworkDto> homeworkList = assignmentClient.getAllHomeworkForAssignment(assignmentId, adminToken);
		for(HomeworkDto homework : homeworkList) {
			if(homework.getUsername().equals((String) auth.getPrincipal())) {
				return Pair.of(Optional.empty(), ReturnStatus.ENTITY_ALREADY_CONTAINS_ENTITY);
			}
		}
		
		assignmentClient.uploadHomeworkToAssignment(assignmentId, (String) auth.getPrincipal(), file, token);
		return Pair.of(Optional.of(optCourse.get()), ReturnStatus.OK);
	}
	
	//util
	public String getAdminToken() throws JsonMappingException, JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		String url = "http://localhost:8282/oauth/token";
		String grantType = "password";
		String adminUsername = "admin";
		String adminPassword = "admin";
		
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("grant_type", grantType);
		map.add("username", adminUsername);
		map.add("password", adminPassword);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		
		return objectMapper.readTree(response.getBody()).path("access_token").asText();
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