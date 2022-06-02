package com.dtw.commons.dto;

import java.util.HashSet;
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
	@Builder.Default
	private Set<Long> assignments = new HashSet<>();
	@JsonProperty(access = Access.READ_ONLY)
	private String joinPassword;
}