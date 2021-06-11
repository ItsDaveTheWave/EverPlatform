package com.dtw.course.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dtw.course.dto.AssignmentDto;

@FeignClient(name = "assignment-service")
public interface AssignmentClient {

	@GetMapping("/api/assignment/{id}")
	public AssignmentDto getOne(@PathVariable Long id);
}