package com.siteminder.emailservice.service;

import com.siteminder.emailservice.exception.MailTimeOutException;
import com.siteminder.emailservice.model.EmailRequest;
import com.siteminder.emailservice.model.EmailResponse;

public interface EmailService {
	static final String HANDLER = "handler";
	static final String MAILGUN = "mailgun";
	static final String SENDGRID = "sendgrid";
	
	EmailResponse sendMail(EmailRequest request);
}
