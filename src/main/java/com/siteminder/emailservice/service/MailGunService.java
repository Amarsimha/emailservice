package com.siteminder.emailservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;
import com.siteminder.emailservice.exception.SendMailException;
import com.siteminder.emailservice.model.EmailRequest;
import com.siteminder.emailservice.model.EmailResponse;
import com.siteminder.emailservice.model.EmailStatus;
import com.siteminder.emailservice.utils.Constants;

@Service(value = EmailService.MAILGUN)
public class MailGunService implements EmailService {

	private static final Logger LOG = LoggerFactory.getLogger(MailGunService.class);
	
	@Value("${mailgun.api.key}")
	private String apiKey;
	
	@Value("${mailgun.api.domain}")
	private String domain;
	
	@Value("${mailgun.api.endpoint}")
	private String endPoint;
	
	
	@Override
	public EmailResponse sendMail(EmailRequest request) {

		try {
			HttpResponse<JsonNode> mailgunResponse = sendRequest(request).asJson();
			LOG.debug("Response from mailgun: {} : {} ", mailgunResponse.getStatus(), mailgunResponse.getBody() );
			if(!HttpStatus.valueOf(mailgunResponse.getStatus()).is2xxSuccessful()) {
				throw new SendMailException(Constants.MAILGUN_ERROR_RESPONSE);
			}
		} catch(UnirestException ue) {
			LOG.error("Error sending mail through mailgun: {}", ue);
			throw new RuntimeException(ue.getMessage());
		}
		
		return new EmailResponse(EmailStatus.SUCCESS, Constants.SEND_SUCCESS);
	}

	private MultipartBody sendRequest(EmailRequest request) {
		MultipartBody httpRequest = Unirest.post(endPoint + domain + "/messages")
				.basicAuth("api", apiKey)
				.field("from", request.getFrom())
				.field("Subject", request.getSubject())
				.field("text", request.getContent());

		if (request.hasTo()) {
			httpRequest = httpRequest.field("to", request.getTo());
		}
		if (request.hasCc()) {
			httpRequest = httpRequest.field("cc", request.getCc());
		}
		if (request.hasBcc()) {
			httpRequest = httpRequest.field("bcc", request.getBcc());
		}
		return httpRequest;
	}
}
