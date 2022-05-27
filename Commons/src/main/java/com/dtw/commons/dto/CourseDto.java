package com.dtw.commons.dto;

import java.util.Set;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {

	@JsonProperty(access = Access.READ_ONLY)
	private Long id;
	@NotNull
	@Length(min = 2, max = 20)
	private String name;
	@JsonProperty(access = Access.READ_ONLY)
	private Set<Long> assignments;
	@JsonProperty(access = Access.READ_ONLY)
	private Long teacherId;
	@JsonProperty(access = Access.READ_ONLY)
	private Set<Long> studentsIds;
	@JsonProperty(access = Access.READ_ONLY)
	private String joinPassword;
}