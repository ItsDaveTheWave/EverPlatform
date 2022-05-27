package com.dtw.homework.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import com.dtw.commons.dto.HomeworkDto;
import com.dtw.homework.entity.Homework;
import com.dtw.homework.repo.HomeworkRepo;

@Service
public class HomeworkService {

	@Autowired
	private HomeworkRepo homeworkRepo;
	
	@Autowired
	@Qualifier("mvcConversionService")
	private ConversionService conversionService;
	
	public List<Homework> getAll() {
		return homeworkRepo.findAll();
	}
	
	public Optional<Homework> getOne(Long id) {
		return homeworkRepo.findById(id);
	}
	
	public Homework create(Homework homework) {
		return homeworkRepo.save(homework);
	}
	
	public void delete(Long id) {
		if(homeworkRepo.findById(id).isPresent()) {
			homeworkRepo.deleteById(id);
		}
	}
	
	public List<HomeworkDto> toDtoList(List<Homework> homeworks) {

		List<HomeworkDto> res = new ArrayList<>();
		for (Homework homework : homeworks) {
			res.add(conversionService.convert(homework, HomeworkDto.class));
		}
		return res;
	}

	public List<Homework> toEntityList(List<HomeworkDto> homeworks) {

		List<Homework> res = new ArrayList<>();
		for (HomeworkDto homework : homeworks) {
			res.add(conversionService.convert(homework, Homework.class));
		}
		return res;
	}
}