package com.dtw.commons.dto;

import java.util.Set;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDto {

	@JsonProperty(access = Access.READ_ONLY)
	private Long id;
	@NotNull
	@Length(min = 4, max = 25)
	private String title;
	@Length(min = 0, max = 250)
	private String description;
	@JsonProperty(access = Access.READ_ONLY)
	private Set<Long> homeworkIds;
}