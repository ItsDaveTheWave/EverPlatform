package com.dtw.homework.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.dtw.commons.dto.HomeworkDto;
import com.dtw.errorHandler.error.ApiError;
import com.dtw.homework.entity.Homework;
import com.dtw.homework.service.HomeworkService;

@RestController
@RequestMapping("/api/homework")
public class HomeworkController {

	@Autowired
	private HomeworkService homeworkService;

	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;
	
	@GetMapping
	public ResponseEntity<List<HomeworkDto>> getAll() {

		List<HomeworkDto> dtoList = homeworkService.toDtoList(homeworkService.getAll());
		
		if(dtoList.size() == 0) {
			return new ResponseEntity<List<HomeworkDto>>(HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.ok(dtoList);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getOne(@PathVariable Long id) {

		Optional<Homework> optHomework = homeworkService.getOne(id);
		
		if(optHomework.isPresent()) {
			return ResponseEntity.ok(conversionService.convert(optHomework.get(), HomeworkDto.class));
		}
		return ApiError.entityNotFound("Homework", "id", id).buildResponseEntity();
	}
	
	@GetMapping("/{id}/download")
	public ResponseEntity<?> donwloadOne(@PathVariable Long id) {

		Optional<Homework> optHomework = homeworkService.getOne(id);
		
		if(optHomework.isPresent()) {
			Homework homework = optHomework.get();
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("Content-Disposition", "attachment; filename=\"" + homework.getFileName() + "." + homework.getFileExtension() + "\"");
			
			return ResponseEntity.ok()
					.headers(responseHeaders)
					.body(new ByteArrayResource(homework.getBytes()));
		}
		return ApiError.entityNotFound("Homework", "id", id).buildResponseEntity();
	}
	
	@PostMapping("/{username}")
	public ResponseEntity<HomeworkDto> upload(@RequestParam MultipartFile file, @PathVariable String username) {
		
		try {
			String fileName = file.getOriginalFilename();
			Homework homework = new Homework(1L, file.getBytes(), fileName.substring(0, fileName.lastIndexOf(".")),
					fileName.substring(fileName.lastIndexOf(".") + 1), username);
			homework.setId(null);
			
			return new ResponseEntity<HomeworkDto>(conversionService.convert(homeworkService.create(homework), HomeworkDto.class), HttpStatus.OK);
		}
		catch (Exception e) {
			throw new MultipartException(e.getMessage());
		}
	}
	
	@DeleteMapping("/{id}") 
	public ResponseEntity<Void> delete(@PathVariable Long id) {
 		homeworkService.delete(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}