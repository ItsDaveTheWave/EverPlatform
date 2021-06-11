package com.dtw.assignment.util;

import org.springframework.core.convert.converter.Converter;

import com.dtw.assignment.dto.AssignmentDto;
import com.dtw.assignment.entity.Assignment;

public class DtoToAssignmentConverter implements Converter<AssignmentDto, Assignment> {

	@Override
	public Assignment convert(AssignmentDto source) {
		return new Assignment(source.getId(), source.getTitle(), source.getDescription());
	}
}