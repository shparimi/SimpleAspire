package com.shparimi.aspireapp.exceptions;

public class PaymentException extends WebApplicationException{

	private static final long serialVersionUID = 1L;

	public PaymentException(String message) {
		super(message);
	}

}
