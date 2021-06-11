package com.dtw.course.util;

import org.springframework.core.convert.converter.Converter;

import com.dtw.course.dto.CourseDto;
import com.dtw.course.entity.Course;

public class DtoToCourseConverter implements Converter<CourseDto, Course> {

	@Override
	public Course convert(CourseDto source) {
		return new Course(source.getId(), source.getName());
	}
}