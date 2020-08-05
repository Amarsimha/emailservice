package com.siteminder.emailservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Response from the service to send mail")
public class EmailResponse {
	@ApiModelProperty(notes = "Status of the send mail operation")
	private EmailStatus status;
	@ApiModelProperty(notes = "Response message")
	private String message;
	
	EmailResponse() {
		
	}
	
	public EmailResponse(EmailStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public EmailStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return "EmailResponse: " + status + "\t" + message;
	}

}
