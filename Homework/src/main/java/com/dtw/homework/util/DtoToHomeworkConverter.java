package com.dtw.homework.util;

import java.nio.charset.StandardCharsets;

import org.springframework.core.convert.converter.Converter;

import com.dtw.commons.dto.HomeworkDto;
import com.dtw.homework.entity.Homework;
public class DtoToHomeworkConverter implements Converter<HomeworkDto, Homework> {

	@Override
	public Homework convert(HomeworkDto source) {
		return new Homework(source.getId(), StandardCharsets.UTF_8.encode(source.getBytes()).array(), source.getFileName(), source.getFileExtension(), source.getUserId());
	}
}