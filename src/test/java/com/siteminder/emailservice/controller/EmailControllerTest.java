package com.siteminder.emailservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.siteminder.emailservice.exception.MailTimeOutException;
import com.siteminder.emailservice.exception.SendMailException;
import com.siteminder.emailservice.model.EmailRequest;
import com.siteminder.emailservice.model.EmailResponse;
import com.siteminder.emailservice.model.EmailStatus;
import com.siteminder.emailservice.service.EmailService;
import com.siteminder.emailservice.utils.Constants;

@WebMvcTest
@RunWith(SpringRunner.class)
public class EmailControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	@Qualifier(EmailService.HANDLER)
	private EmailService handler;


	@Test
	public void testSuccess() throws Exception {
		String requestJson = "{" 
				+ "\"from\":\"test@example.com\","
				+ "\"to\": [\"test1@example.com\", \"test2@example.com\", \"test@test.com\"],"
				+ "\"subject\": \"Testing\","
				+ "\"content\": \"Hello from Email Service\""
				+ "}";

		Mockito.when(handler.sendMail(ArgumentMatchers.any(EmailRequest.class)))
		.thenReturn(new EmailResponse(EmailStatus.SUCCESS, "Message sent"));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/email/send")
				.accept(MediaType.APPLICATION_JSON).content(requestJson)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		String expected = "{\"status\":\"SUCCESS\",\"message\":\"Message sent\"}";
		JSONAssert.assertEquals(expected, response.getContentAsString(), false);
	}

	@Test 
	public void testMissingFromAddress() throws Exception {
		String requestJson = "{" 
				+ "\"to\": [\"test1@example.com\", \"test2@example.com\", \"test@test.com\"],"
				+ "\"subject\": \"Testing\","
				+ "\"content\": \"Hello from Email Service\""
				+ "}";


		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/email/send")
				.accept(MediaType.APPLICATION_JSON).content(requestJson)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		String expected = "{\"status\":\"FAILED\",\"message\":\"" + Constants.NO_FROM_ADDRESS + "\"}";
		JSONAssert.assertEquals(expected, response.getContentAsString(), false);

	}

	@Test 
	public void testMissingRecipients() throws Exception {
		String requestJson = "{" 
				+ "\"from\": \"test1@example.com\","
				+ "\"subject\": \"Testing\","
				+ "\"content\": \"Hello from Email Service\""
				+ "}";


		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/email/send")
				.accept(MediaType.APPLICATION_JSON).content(requestJson)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		String expected = "{\"status\":\"FAILED\",\"message\":\"" + Constants.NO_RECIPIENTS + "\"}";
		JSONAssert.assertEquals(expected, response.getContentAsString(), false);
	}
	
	@Test 
	public void testMissingBody() throws Exception {
		String requestJson = "{" 
				+ "\"from\": \"test1@example.com\","
				+ "\"to\": [\"test1@example.com\", \"test2@example.com\", \"test@test.com\"],"
				+ "\"subject\": \"Testing\""
				+ "}";


		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/email/send")
				.accept(MediaType.APPLICATION_JSON).content(requestJson)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		String expected = "{\"status\":\"FAILED\",\"message\":\"" + Constants.NO_MAIL_BODY + "\"}";
		JSONAssert.assertEquals(expected, response.getContentAsString(), false);
	}
	
	@Test
	public void testTimeOut() throws Exception {
		String requestJson = "{" 
				+ "\"from\":\"test@example.com\","
				+ "\"to\": [\"test1@example.com\", \"test2@example.com\", \"test@test.com\"],"
				+ "\"subject\": \"Testing\","
				+ "\"content\": \"Hello from Email Service\""
				+ "}";

		Mockito.when(handler.sendMail(ArgumentMatchers.any(EmailRequest.class)))
		.thenThrow(new MailTimeOutException());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/email/send")
				.accept(MediaType.APPLICATION_JSON).content(requestJson)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertEquals(HttpStatus.GATEWAY_TIMEOUT.value(), response.getStatus());
		String expected = "{\"status\":\"FAILED\",\"message\":\"" + Constants.TIMED_OUT +"\"}";
		JSONAssert.assertEquals(expected, response.getContentAsString(), false);

	}
	
	@Test
	public void testBadGateway() throws Exception {
		String requestJson = "{" 
				+ "\"from\":\"test@example.com\","
				+ "\"to\": [\"test1@example.com\", \"test2@example.com\", \"test@test.com\"],"
				+ "\"subject\": \"Testing\","
				+ "\"content\": \"Hello from Email Service\""
				+ "}";

		Mockito.when(handler.sendMail(ArgumentMatchers.any(EmailRequest.class)))
		.thenThrow(new SendMailException(Constants.SEND_FAILED));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/email/send")
				.accept(MediaType.APPLICATION_JSON).content(requestJson)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertEquals(HttpStatus.BAD_GATEWAY.value(), response.getStatus());
		String expected = "{\"status\":\"FAILED\",\"message\":\"" + Constants.SEND_FAILED +"\"}";
		JSONAssert.assertEquals(expected, response.getContentAsString(), false);

	}

}
