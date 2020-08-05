package com.siteminder.emailservice.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.siteminder.emailservice.exception.MailTimeOutException;
import com.siteminder.emailservice.exception.SendMailException;
import com.siteminder.emailservice.model.EmailRequest;
import com.siteminder.emailservice.model.EmailResponse;
import com.siteminder.emailservice.utils.Constants;

@Service(value = EmailService.HANDLER)
public class EmailServiceHandler implements EmailService {

	private static final Logger LOG = LoggerFactory.getLogger(EmailServiceHandler.class);
	
	@Autowired
	@Qualifier(EmailService.MAILGUN)
	private EmailService mailgun;
	
	@Autowired
	@Qualifier(EmailService.SENDGRID)
	private EmailService sendGrid;
	
	@Value("${gateway.timeout}") 
	private Long timeOut;
	
	@Override
	public EmailResponse sendMail(EmailRequest request) {
		/*
		 * Try sending mail through SendGrid.
		 * If it fails due to an error/invalid response try sending mail through mailgun.
		 */
		CompletableFuture<EmailResponse> responseFuture =
				CompletableFuture.supplyAsync(() -> sendGrid.sendMail(request))
				.handle((output, excp) -> {
					if (excp != null) {
						LOG.info("Sending failed through SendGrid....trying Mailgun now");
						return mailgun.sendMail(request);
					}
					return output;
				});

		try {
			// Wait for the configured duration to get the response.
			return responseFuture.get(timeOut, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException e) {
			//Send failed through all providers.
			LOG.error("Error sending email: ", e);
			//Wrap all exceptions into one Send_Failed exception
			throw new SendMailException(Constants.SEND_FAILED);
		} catch (TimeoutException e) {
			// Did not get any response before timeout
			LOG.info("Timed out");
			throw new MailTimeOutException();
		}
	}
	
	/*
	 * To be used only for testing.
	 */
	void setTimeOut(Long timeOut) {
		this.timeOut = timeOut;
	}
}
