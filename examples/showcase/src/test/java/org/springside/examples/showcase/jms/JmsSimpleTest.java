package org.springside.examples.showcase.jms;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springside.examples.showcase.common.entity.User;
import org.springside.examples.showcase.jms.simple.NotifyMessageListener;
import org.springside.examples.showcase.jms.simple.NotifyMessageProducer;
import org.springside.modules.log.MockLog4jAppender;
import org.springside.modules.test.spring.SpringContextTestCase;
import org.springside.modules.utils.Threads;

@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml", "/jms/applicationContext-jms-simple.xml" })
public class JmsSimpleTest extends SpringContextTestCase {

	@Autowired
	private NotifyMessageProducer notifyMessageProducer;

	@Test
	public void queueMessage() {
		Threads.sleep(1000);
		MockLog4jAppender appender = new MockLog4jAppender();
		appender.addToLogger(NotifyMessageListener.class);

		User user = new User();
		user.setName("calvin");
		user.setEmail("calvin@sringside.org.cn");

		notifyMessageProducer.sendQueue(user);
		logger.info("sended message");

		Threads.sleep(1000);
		assertNotNull(appender.getFirstLog());
		assertEquals("UserName:calvin, Email:calvin@sringside.org.cn", appender.getFirstLog().getMessage());
	}

	@Test
	public void topicMessage() {
		Threads.sleep(1000);
		MockLog4jAppender appender = new MockLog4jAppender();
		appender.addToLogger(NotifyMessageListener.class);

		User user = new User();
		user.setName("calvin");
		user.setEmail("calvin@sringside.org.cn");

		notifyMessageProducer.sendTopic(user);
		logger.info("sended message");

		Threads.sleep(1000);
		assertNotNull(appender.getFirstLog());
		assertEquals("UserName:calvin, Email:calvin@sringside.org.cn", appender.getFirstLog().getMessage());
	}
}
