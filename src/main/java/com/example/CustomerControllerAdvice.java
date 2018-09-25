package com.example;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class CustomerControllerAdvice {

	private final MediaType vndErrorMediaType = MediaType.parseMediaType("application/vnd.error+json");
	
	@ExceptionHandler(CustomerNotFoundException.class)
	ResponseEntity<VndErrors> notFoundException(CustomerNotFoundException e) {
		return error(e, HttpStatus.NOT_FOUND, e.getCustomerId() + "");
	}
	
	private <E extends Exception> ResponseEntity<VndErrors> error(E error, HttpStatus status, String logref) {
		String msg = Optional.ofNullable(error.getMessage()).orElse(error.getClass().getSimpleName());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(vndErrorMediaType);
		return new ResponseEntity<>(new VndErrors(logref, msg), headers, status);
	}

}
