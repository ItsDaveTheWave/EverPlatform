package com.dtw.course.util;

import org.springframework.core.convert.converter.Converter;

import com.dtw.course.dto.CourseDto;
import com.dtw.course.entity.Course;

public class CourseToDtoConverter implements Converter<Course, CourseDto> {

	@Override
	public CourseDto convert(Course source) {
		return new CourseDto(source.getId(), source.getName());
	}
}