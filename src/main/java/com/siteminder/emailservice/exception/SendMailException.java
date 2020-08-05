package com.siteminder.emailservice.exception;

public class SendMailException extends RuntimeException {

	private static final long serialVersionUID = -9094705668913565703L;

	public SendMailException(String message) {
		super(message);
	}
}
