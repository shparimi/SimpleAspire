package com.shparimi.aspireapp.exceptions;

public class BadRequestException extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	public BadRequestException(String message) {
		super(message);
	}

}
