package com.dtw.assignment.util;

import org.springframework.core.convert.converter.Converter;

import com.dtw.assignment.entity.Assignment;
import com.dtw.commons.dto.AssignmentDto;

public class AssignmentToDtoConverter implements Converter<Assignment, AssignmentDto> {

	@Override
	public AssignmentDto convert(Assignment source) {
		return new AssignmentDto(source.getId(), source.getTitle(), source.getDescription(), source.getHomeworkIds());
	}
}