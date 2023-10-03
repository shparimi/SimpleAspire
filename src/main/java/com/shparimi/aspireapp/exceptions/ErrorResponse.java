package com.shparimi.aspireapp.exceptions;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
	private HttpStatus httpStatus;
	private String error;
	private String message;
}
