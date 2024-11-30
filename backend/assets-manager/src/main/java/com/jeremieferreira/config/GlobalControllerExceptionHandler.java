package com.jeremieferreira.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jeremieferreira.feature.util.ApplicationError;

@ControllerAdvice
class GlobalDefaultExceptionHandler extends ResponseEntityExceptionHandler  {

	@ExceptionHandler({ApplicationError.class})
	public ResponseEntity<Object> handleApplicationError(Exception e, WebRequest request) {
		return new ResponseEntity<Object>(e, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}