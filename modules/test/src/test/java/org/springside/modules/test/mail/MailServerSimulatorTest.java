package org.springside.modules.test.mail;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.spring.SpringContextTestCase;

import com.icegreen.greenmail.util.GreenMail;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class MailServerSimulatorTest extends SpringContextTestCase {

	@Autowired
	private GreenMail greenMail;

	@Test
	public void greenMail() {
		assertEquals(3025, greenMail.getSmtp().getPort());
	}
}
