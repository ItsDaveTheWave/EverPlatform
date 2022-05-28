package com.dtw.commons.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.dtw.commons.dto.HomeworkDto;

@FeignClient("homework-service")
public interface HomeworkClient {

	@GetMapping("/api/homework")
	public List<HomeworkDto> getAll(@RequestHeader("Authorization") String token);
	
	@GetMapping("/api/homework/{id}")
	public HomeworkDto getOne(@PathVariable Long id, @RequestHeader("Authorization") String token);
	
	@GetMapping("/api/homework/{id}/download")
	public ByteArrayResource download(@PathVariable Long id, @RequestHeader("Authorization") String token);
	
	@PostMapping("/api/homework")
	public HomeworkDto upload(@RequestParam MultipartFile file, @RequestParam Long userId, @RequestHeader("Authorization") String token);
	
	@DeleteMapping("/api/homework/{id}")
	public Void delete(@PathVariable Long id, @RequestHeader("Authorization") String token);
}