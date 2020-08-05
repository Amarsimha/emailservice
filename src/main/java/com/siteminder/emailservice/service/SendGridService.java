package com.siteminder.emailservice.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.siteminder.emailservice.exception.SendMailException;
import com.siteminder.emailservice.model.EmailRequest;
import com.siteminder.emailservice.model.EmailResponse;
import com.siteminder.emailservice.model.EmailStatus;
import com.siteminder.emailservice.utils.Constants;

@Service(value = EmailService.SENDGRID)
public class SendGridService implements EmailService {

	private static final Logger LOG = LoggerFactory.getLogger(SendGridService.class);
	
	private SendGrid sendGrid;
	
	SendGridService(@Value("${sendgrid.api.key}") String apiKey) {
		sendGrid = new SendGrid(apiKey);
	}
	
	@Override
	public EmailResponse sendMail(EmailRequest request) {
		Mail mail = new Mail();
		Personalization personalization = new Personalization();

		if (request.hasTo()) {
			request.getTo().stream()
				.forEach(elem -> personalization.addTo(new Email(elem)));
		}

		if (request.hasCc()) {
			request.getCc().stream()
				.forEach(elem -> personalization.addCc(new Email(elem)));
		}

		if (request.hasBcc()) {
			request.getBcc().stream()
				.forEach(elem -> personalization.addBcc(new Email(elem)));
		}

		mail.addPersonalization(personalization);
		mail.setFrom(new Email(request.getFrom()));
		mail.setSubject(request.getSubject());
		mail.addContent(new Content("text/plain", request.getContent()));


		Request sendGridRequest = new Request();

		try {
			sendGridRequest.setMethod(Method.POST);
			sendGridRequest.setEndpoint("mail/send");
			sendGridRequest.setBody(mail.build());
			LOG.debug("Sendgrid Request : {}", sendGridRequest.getBody());
			Response response = sendGrid.api(sendGridRequest);
			LOG.debug("Sendgrid Response: {} : {}", response.getStatusCode(), response.getBody());
			if (!HttpStatus.valueOf(response.getStatusCode()).is2xxSuccessful()) {
				LOG.info("Sendgrid failed");
				throw new SendMailException(Constants.SENDGRID_ERROR_RESPONSE);
			}
		} catch(IOException excp) {
			LOG.error("Error sending mail through SendGrid: {}", excp);
			throw new RuntimeException(excp.getMessage());
		}
		return new EmailResponse(EmailStatus.SUCCESS, Constants.SEND_SUCCESS);
	}

}
