package com.shparimi.aspireapp.exceptions;

public class WebApplicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WebApplicationException(String message) {
		super(message);
	}
}
