package org.springside.examples.showcase.utilities.email;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.spring.SpringContextTestCase;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
/**
 * 
 * @author 六 翼
 * 用GreenMail Mock Mail Server 来测试邮件发总
 */
@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml",
		"/email/applicationContext-email.xml" })
@ActiveProfiles("test")
public class MailServiceTest extends SpringContextTestCase {

	@Autowired
	private SimpleMailService simpleMailService;

	@Autowired
	private MimeMailService mimeMailService;

	private GreenMail greenMail;

	@Before
	public void setUp() {
		greenMail = new GreenMail(ServerSetup.SMTP);
		greenMail.setUser("localhost", "forTest");
		greenMail.start();
	}

	@After
	public void tearDown() {
		if (null != greenMail) {
			greenMail.stop();
		}
	}

	/**
	 * 简单文本邮件发送测试
	 * @throws MessagingException
	 * @throws InterruptedException
	 */
	@Test
	public void sendSimpleMail() throws MessagingException,
			InterruptedException {
		simpleMailService.sendNotificationMail("lucifer");
		greenMail.waitForIncomingEmail(2000, 1);
		
		Message message = greenMail.getReceivedMessages()[0];
		assertEquals("springside3.demo@gmail.com",
				message.getFrom()[0].toString());
		assertEquals("用户修改通知", message.getSubject());
	}

	/**
	 * MIME邮件发送测试
	 * @throws InterruptedException
	 * @throws MessagingException
	 * @throws IOException
	 */
	@Test
	public void sendMimeMail() throws InterruptedException, MessagingException,
			IOException {
		mimeMailService.sendNotificationMail("lucifer");
		greenMail.waitForIncomingEmail(2000, 1);
		
		MimeMessage message = greenMail.getReceivedMessages()[0];
		MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
		assertEquals("springside3.demo@gmail.com",
				message.getFrom()[0].toString());
		assertEquals("用户修改通知", message.getSubject());
		assertEquals(false, GreenMailUtil.hasNonTextAttachments(message));
		assertEquals("Hello,i am a attachment.",
				GreenMailUtil.getBody(mimeMultipart.getBodyPart(1)).trim());
	}

}
