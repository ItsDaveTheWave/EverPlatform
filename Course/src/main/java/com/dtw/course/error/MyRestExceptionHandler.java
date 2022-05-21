package com.dtw.course.error;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.dtw.errorHandler.RestExceptionHandler;
import com.dtw.errorHandler.error.ApiError;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class MyRestExceptionHandler extends RestExceptionHandler {

	// Access denied exception
	@ExceptionHandler(AccessDeniedException.class)
	protected ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
		ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
		apiError.setMessage(ex.getMessage());
		return super.buildResponseEntityApiError(apiError);
	}
}