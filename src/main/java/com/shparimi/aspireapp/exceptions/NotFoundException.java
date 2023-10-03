package com.shparimi.aspireapp.exceptions;

public class NotFoundException extends WebApplicationException{

	private static final long serialVersionUID = 1L;

	public NotFoundException(String message) {
		super(message);
	}

}
