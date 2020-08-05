package com.siteminder.emailservice.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.siteminder.emailservice.exception.MailTimeOutException;
import com.siteminder.emailservice.exception.SendMailException;
import com.siteminder.emailservice.model.EmailRequest;
import com.siteminder.emailservice.model.EmailResponse;
import com.siteminder.emailservice.model.EmailStatus;
import com.siteminder.emailservice.utils.Constants;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EmailServiceHandlerTest {

	@MockBean
	@Qualifier(EmailService.MAILGUN)
	private EmailService mailgun;
	
	@MockBean
	@Qualifier(EmailService.SENDGRID)
	private EmailService sendGrid;
	
	@Autowired
	@Qualifier(EmailService.HANDLER)
	private EmailService handler;

	private EmailRequest request;
	
	@Before
	public void setup() {
		request = new EmailRequest();
		request.setFrom("test@example.com");
		request.setSubject("Subject");
		request.setContent("Body of mail");
		request.addTo("test1@example.com");
	}
	
	@Test
	public void testSendGridSuccess() {
		when(sendGrid.sendMail(request)).thenReturn(new EmailResponse(EmailStatus.SUCCESS, Constants.SEND_SUCCESS));
		
		EmailResponse response = handler.sendMail(request);
		verify(sendGrid, times(1)).sendMail(request);
		verifyNoInteractions(mailgun);
		assertTrue(response.getStatus() == EmailStatus.SUCCESS);
		assertTrue(Constants.SEND_SUCCESS.equals(response.getMessage()));
	}
	
	@Test
	public void testSendGridFailure() {
		when(sendGrid.sendMail(request)).thenThrow(new RuntimeException());
		when(mailgun.sendMail(request)).thenReturn(new EmailResponse(EmailStatus.SUCCESS, Constants.SEND_SUCCESS));

		EmailResponse response = handler.sendMail(request);
		verify(sendGrid, times(1)).sendMail(request);
		verify(mailgun, times(1)).sendMail(request);
		assertTrue(response.getStatus() == EmailStatus.SUCCESS);
		assertTrue(Constants.SEND_SUCCESS.equals(response.getMessage()));

	}

	@Test(expected=MailTimeOutException.class)
	public void testSendgridTimeout() {
		((EmailServiceHandler)handler).setTimeOut(3L);
		when(sendGrid.sendMail(request)).thenAnswer(new Answer<EmailResponse>() {

			@Override
			public EmailResponse answer(InvocationOnMock invocation) throws Throwable {
				Thread.sleep(10000);
				throw new RuntimeException();
			}
		});
		when(mailgun.sendMail(request)).thenReturn(new EmailResponse(EmailStatus.SUCCESS, Constants.SEND_SUCCESS));
		
		handler.sendMail(request);
	}

	@Test(expected=MailTimeOutException.class)
	public void testMailgunTimeout() {
		((EmailServiceHandler)handler).setTimeOut(3L);
		when(sendGrid.sendMail(request)).thenAnswer(new Answer<EmailResponse>() {

			@Override
			public EmailResponse answer(InvocationOnMock invocation) throws Throwable {
				throw new RuntimeException();
			}
		});
		when(mailgun.sendMail(request)).thenAnswer(new Answer<EmailResponse>() {

			@Override
			public EmailResponse answer(InvocationOnMock invocation) throws Throwable {
				Thread.sleep(10000);
				return null;
			}
		});
		
		handler.sendMail(request);
	}
	
	@Test
	public void testMailSendFailure() {
		when(sendGrid.sendMail(request)).thenThrow(new SendMailException("sendgrid failure"));
		when(mailgun.sendMail(request)).thenThrow(new SendMailException("mailgun failure"));
		
		try {
			handler.sendMail(request);
		}catch(SendMailException e) {
			assertTrue(e.getMessage().equals(Constants.SEND_FAILED));
		}
	}
}
