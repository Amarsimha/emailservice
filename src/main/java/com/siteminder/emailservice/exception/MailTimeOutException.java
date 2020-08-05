package com.siteminder.emailservice.exception;

import com.siteminder.emailservice.utils.Constants;

public class MailTimeOutException extends RuntimeException {

	private static final long serialVersionUID = -5525415975776712187L;

	public MailTimeOutException() {
		super(Constants.TIMED_OUT);
	}
}
