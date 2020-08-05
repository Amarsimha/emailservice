package com.siteminder.emailservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.siteminder.emailservice.exception.InvalidInputException;
import com.siteminder.emailservice.exception.MailTimeOutException;
import com.siteminder.emailservice.exception.SendMailException;
import com.siteminder.emailservice.model.EmailRequest;
import com.siteminder.emailservice.model.EmailResponse;
import com.siteminder.emailservice.model.EmailStatus;
import com.siteminder.emailservice.service.EmailService;
import com.siteminder.emailservice.utils.Constants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/email")
@Api(value = "email")
public class EmailController {
	
	private static final Logger LOG = LoggerFactory.getLogger(EmailController.class); 
	
	@Autowired
	@Qualifier(value = EmailService.HANDLER)
	private EmailService emailService;
	
	@PostMapping(path="/send", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Send Text Mail",response = EmailResponse.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Message successfully sent"),
			@ApiResponse(code = 400, message = "Input parameters missing"),
			@ApiResponse(code = 500, message = "Internal Server error")
	})
    public ResponseEntity<EmailResponse> sendMail(@RequestBody EmailRequest request) {
		LOG.debug("Got request: {}", request);
		try {
			isRequestValid(request);
			ResponseEntity<EmailResponse> response = new ResponseEntity<EmailResponse>(emailService.sendMail(request), HttpStatus.OK);
			LOG.debug("Sending response : {}", response);
			return response;
			
		}catch(InvalidInputException e) {
			return new ResponseEntity<EmailResponse>(new EmailResponse(EmailStatus.FAILED, e.getMessage()), HttpStatus.BAD_REQUEST);
		}catch(MailTimeOutException te) {
			return new ResponseEntity<EmailResponse>(new EmailResponse(EmailStatus.FAILED, te.getMessage()), HttpStatus.GATEWAY_TIMEOUT);
		}catch(SendMailException se) {
			return new ResponseEntity<EmailResponse>(new EmailResponse(EmailStatus.FAILED, se.getMessage()), HttpStatus.BAD_GATEWAY);
		}
	}
	
	private void isRequestValid(EmailRequest request) throws InvalidInputException {
		if (!request.hasFrom()) {
			LOG.debug("From address missing in request");
			throw new InvalidInputException(Constants.NO_FROM_ADDRESS);
		}
		if (!request.hasTo() && !request.hasCc() && !request.hasBcc()) {
			LOG.debug("Recipients address missing in request");
			throw new InvalidInputException(Constants.NO_RECIPIENTS);
		}
		if (request.getContent() == null || request.getContent().isEmpty()) {
			LOG.debug("Mail body missing");
			throw new InvalidInputException(Constants.NO_MAIL_BODY);
		}
	}
}
