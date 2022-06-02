package com.dtw.commons.dto;

import javax.validation.constraints.NotNull;

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
public class HomeworkDto {

	@JsonProperty(access = Access.READ_ONLY)
	private Long id;
	@NotNull
	@JsonProperty(access = Access.READ_ONLY)
	private String bytes;
	@NotNull
	@JsonProperty(access = Access.READ_ONLY)
	private String fileName;
	@NotNull
	@JsonProperty(access = Access.READ_ONLY)
	private String fileExtension;
}