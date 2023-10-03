package com.shparimi.aspireapp.exceptions;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(WebApplicationException.class)
	public ResponseEntity<ErrorResponse> webApplicationException(WebApplicationException ex) {
		ErrorResponse er = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage());
		return new ResponseEntity<ErrorResponse>(er, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> genericException(Exception ex) {
		log.error("Generic error handler",ex);
		ErrorResponse er = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage());
		return new ResponseEntity<ErrorResponse>(er, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> userExistsException(BadRequestException ex) {
		ErrorResponse er = new ErrorResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
				ex.getMessage());
		return new ResponseEntity<ErrorResponse>(er, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> accessDeniedException(AccessDeniedException ex) {
		ErrorResponse er = new ErrorResponse(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase(),
				ex.getMessage());
		return new ResponseEntity<ErrorResponse>(er, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> authenticationException(AuthenticationException ex) {
		ErrorResponse er = new ErrorResponse(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase(),
				ex.getMessage());
		return new ResponseEntity<ErrorResponse>(er, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> notFoundException(NotFoundException ex) {
		ErrorResponse er = new ErrorResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(),
				ex.getMessage());
		return new ResponseEntity<ErrorResponse>(er, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(PaymentException.class)
	public ResponseEntity<ErrorResponse> paymentException(PaymentException ex) {
		ErrorResponse er = new ErrorResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
				ex.getMessage());
		return new ResponseEntity<ErrorResponse>(er, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> validationException(MethodArgumentNotValidException ex) {
		BindingResult result = ex.getBindingResult();
		List<org.springframework.validation.FieldError> fieldErrors = result.getFieldErrors();
		ErrorResponse er = new ErrorResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
				processFieldErrors(fieldErrors));
		return new ResponseEntity<ErrorResponse>(er, HttpStatus.BAD_REQUEST);
	}

	private String processFieldErrors(List<org.springframework.validation.FieldError> fieldErrors) {
		StringBuilder sb = new StringBuilder();
		for (org.springframework.validation.FieldError fieldError : fieldErrors) {
			sb.append(fieldError.getField()).append("-").append(fieldError.getDefaultMessage())
					.append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}

}
