
package com.siteminder.emailservice;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.siteminder.emailservice.model.EmailRequest;
import com.siteminder.emailservice.model.EmailResponse;
import com.siteminder.emailservice.model.EmailStatus;

@SpringBootTest(classes = EmailServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmailServiceApplicationTests {

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	@Test
	public void testSendMailSuccess() {
		EmailRequest request = new EmailRequest();
		request.setFrom("amarsimha@gmail.com");
		request.setSubject("Subject");
		request.setContent("Body of mail");
		request.addTo("amarsimha@gmail.com");
		
		HttpEntity<EmailRequest> entity = new HttpEntity<EmailRequest>(request);
		
		ResponseEntity<EmailResponse> response = restTemplate.exchange(
				createURLWithPort(), HttpMethod.POST, entity, EmailResponse.class);
		
		assertTrue(response.getStatusCode() == HttpStatus.OK);
		assertTrue(response.getBody().getStatus() == EmailStatus.SUCCESS);
	}
	
	@Test
	public void testSendMailInputError() {
		EmailRequest request = new EmailRequest();
		request.addTo("amarsimha@gmail.com");
		request.setSubject("Subject");
		request.setContent("Body of mail");

		HttpEntity<EmailRequest> entity = new HttpEntity<EmailRequest>(request);
		
		ResponseEntity<EmailResponse> response = restTemplate.exchange(
				createURLWithPort(), HttpMethod.POST, entity, EmailResponse.class);
		
		assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
		assertTrue(response.getBody().getStatus() == EmailStatus.FAILED);

	}

	@Test
	public void testSendMailInputError_NoRecipient() {
		EmailRequest request = new EmailRequest();
		request.setFrom("amarsimha@gmail.com");
		request.setSubject("Subject");
		request.setContent("Body of mail");

		HttpEntity<EmailRequest> entity = new HttpEntity<EmailRequest>(request);
		
		ResponseEntity<EmailResponse> response = restTemplate.exchange(
				createURLWithPort(), HttpMethod.POST, entity, EmailResponse.class);
		
		assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
		assertTrue(response.getBody().getStatus() == EmailStatus.FAILED);

	}

	@Test
	public void testSendMailInputError_NoBody() {
		EmailRequest request = new EmailRequest();
		request.setFrom("amarsimha@gmail.com");
		request.addTo("amarsimha@gmail.com");
		request.setSubject("Subject");

		HttpEntity<EmailRequest> entity = new HttpEntity<EmailRequest>(request);
		
		ResponseEntity<EmailResponse> response = restTemplate.exchange(
				createURLWithPort(), HttpMethod.POST, entity, EmailResponse.class);
		
		assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
		assertTrue(response.getBody().getStatus() == EmailStatus.FAILED);

	}
	
	private String createURLWithPort() {
		return "http://localhost:" + port + "/email/send";
	}
}
