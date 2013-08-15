package org.springside.examples.showcase.demos.mail;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springside.examples.showcase.demos.utilities.email.MimeMailService;
import org.springside.examples.showcase.demos.utilities.email.SimpleMailService;
import org.springside.modules.test.spring.SpringContextTestCase;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;

@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml", "/email/applicationContext-email.xml" })
public class MailServiceTest extends SpringContextTestCase {

	@Autowired
	private MimeMailService mimeMailService;

	@Autowired
	private SimpleMailService simpleMailService;

	@Autowired
	private GreenMail greenMail;

	@Test
	public void sendSimpleMail() throws MessagingException, InterruptedException, IOException {
		simpleMailService.sendNotificationMail("calvin");

		greenMail.waitForIncomingEmail(2000, 1);

		MimeMessage[] messages = greenMail.getReceivedMessages();
		MimeMessage message = messages[messages.length - 1];

		assertEquals("springside3.demo@gmail.com", message.getFrom()[0].toString());
		assertEquals("用户修改通知", message.getSubject());
		// text格式内容
		System.out.println(message.getContent());
		assertTrue(((String) message.getContent()).contains("被修改"));

	}

	@Test
	public void sendMimeMail() throws InterruptedException, MessagingException, IOException {
		mimeMailService.sendNotificationMail("calvin");

		greenMail.waitForIncomingEmail(2000, 1);
		MimeMessage[] messages = greenMail.getReceivedMessages();
		MimeMessage message = messages[messages.length - 1];

		assertEquals("springside3.demo@gmail.com", message.getFrom()[0].toString());
		assertEquals("用户修改通知", message.getSubject());

		MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();

		assertEquals(2, mimeMultipart.getCount());

		// Html格式的主邮件
		String mainPartText = getMainPartText(mimeMultipart.getBodyPart(0));
		System.out.println(mainPartText);
		assertTrue(mainPartText.contains("<h1>用户calvin被修改.</h1>"));

		// 附件
		assertEquals("Hello,i am a attachment.", GreenMailUtil.getBody(mimeMultipart.getBodyPart(1)).trim());

	}

	private String getMainPartText(Part mainPart) throws MessagingException, IOException {
		return (String) ((Multipart) mainPart.getContent()).getBodyPart(0).getContent();
	}
}
