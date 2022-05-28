package com.dtw.courseAggregator.error;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.dtw.errorHandler.RestExceptionHandler;
import com.dtw.errorHandler.error.ApiError;
import com.dtw.errorHandler.error.ApiInternalCallError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class MyRestExceptionHandler extends RestExceptionHandler {

	@Autowired
	private ObjectMapper objectMapper;
	
	// Path not found error
	@ExceptionHandler(FeignException.class)
	protected ResponseEntity<ApiError> handleFeign(FeignException ex) {
		Object body = null;
		if(ex.responseBody().isPresent()) {
			try {
				body = objectMapper.readTree(StandardCharsets.UTF_8.decode(ex.responseBody().get()).toString());
			} catch (JsonProcessingException e) {
				body = StandardCharsets.UTF_8.decode(ex.responseBody().get()).toString();
			}
		}
		
		ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
		apiError.setMessage("Error while calling internal service");
		apiError.addSubError(new ApiInternalCallError(ex.status(), ex.request().httpMethod().toString(), ex.request().url(), body));
		
		return apiError.buildResponseEntity();
	}
}