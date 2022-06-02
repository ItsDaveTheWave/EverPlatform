package com.dtw.homework.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.springframework.core.convert.converter.Converter;

import com.dtw.commons.dto.HomeworkDto;
import com.dtw.homework.entity.Homework;

public class HomeworkToDtoConverter implements Converter<Homework, HomeworkDto> {

	@Override
	public HomeworkDto convert(Homework source) {
		return new HomeworkDto(source.getId(), StandardCharsets.UTF_8.decode(ByteBuffer.wrap(source.getBytes())).toString(), source.getFileName(), source.getFileExtension(), source.getUsername());
	}
}