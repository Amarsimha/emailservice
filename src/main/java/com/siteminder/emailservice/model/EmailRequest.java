package com.siteminder.emailservice.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Payload to the service to send mail")
public class EmailRequest {
	@ApiModelProperty(notes = "List of \'to\' recipients")
	private Set<String> to = new HashSet<>();
	@ApiModelProperty(notes = "List of \'cc\' recipients")
	private Set<String> cc = new HashSet<>();
	@ApiModelProperty(notes = "List of \'bcc\' recipients")
	private Set<String> bcc = new HashSet<>();
	@ApiModelProperty(notes = "\'From\' address", required = true)
	private String from;
	@ApiModelProperty(notes = "Message subject")
	private String subject;
	@ApiModelProperty(notes = "Message body", required = true)
	private String content;
	
	public EmailRequest() {
		
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean hasFrom() {
		return from != null;
	}
	
	public Set<String> getTo() {
		return Collections.unmodifiableSet(to);
	}

	public boolean hasTo() {
		return to.size() > 0;
	}
	
	public Set<String> getCc() {
		return Collections.unmodifiableSet(cc);
	}

	public boolean hasCc() {
		return cc.size() > 0;
	}

	public Set<String> getBcc() {
		return Collections.unmodifiableSet(bcc);
	}

	public boolean hasBcc() {
		return bcc.size() > 0;
	}

	public void addTo(String recipient) {
		to.add(recipient);
	}
	
	public void addTo(Collection<String> recipients) {
		to.addAll(recipients);
	}
	
	public void addCc(String recipient) {
		cc.add(recipient);
	}

	public void addCc(Collection<String> recipients) {
		cc.addAll(recipients);
	}
	
	public void addBcc(String recipient) {
		bcc.add(recipient);
	}
	
	public void addBcc(Collection<String> recipients) {
		bcc.addAll(recipients);
	}
	
	@Override
	public String toString() {
		return "From: " + from + "\n"
				+ "To: " + String.join(",", to) + "\n"
				+ "Cc: " + String.join(",", cc) + "\n"
				+ "Bcc: " + String.join(",", bcc) + "\n"
				+ "Subject: " + subject + "\n"
				+ "Body: " + content + "\n";
	}
}
