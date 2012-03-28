package org.springside.examples.showcase.cache.guava;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springside.examples.showcase.common.entity.User;
import org.springside.examples.showcase.common.service.AccountManager;
import org.springside.modules.log.MockLog4jAppender;
import org.springside.modules.test.data.H2Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;
import org.springside.modules.utils.Threads;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 本地缓存演示，使用GuavaCache.
 * 
 * @author hzl7652
 */
@ContextConfiguration(locations = { "/applicationContext.xml" })
@TransactionConfiguration(transactionManager = "defaultTransactionManager")
public class GuavaCacheDemo extends SpringTxTestCase {

	private static Logger logger = LoggerFactory.getLogger(GuavaCacheDemo.class);

	@Autowired
	private AccountManager accountManager;

	@Test
	public void demo() throws Exception {

		H2Fixtures.reloadAllTable(dataSource, "/data/sample-data.xml");
		////设置缓存最大个数为100，缓存过期时间为5秒
		LoadingCache<Long, User> cache = CacheBuilder.newBuilder().maximumSize(100)
				.expireAfterAccess(5, TimeUnit.SECONDS).build(new CacheLoader<Long, User>() {
					@Override
					public User load(Long key) throws Exception {
						logger.info("fetch from database");
						return accountManager.getUser(key);
					}

				});

		MockLog4jAppender appender = new MockLog4jAppender();
		appender.addToLogger(GuavaCacheDemo.class);

		//第一次加载会查数据库
		User user = cache.get(1L);
		assertEquals("admin", user.getLoginName());
		assertFalse(appender.isEmpty());
		appender.clearLogs();

		//第二次加载时直接从缓存里取
		User user2 = cache.get(1L);
		assertEquals("admin", user2.getLoginName());
		assertTrue(appender.isEmpty());

		//第三次加载时，因为缓存已经过期所以会查数据库
		Threads.sleep(10, TimeUnit.SECONDS);
		User user3 = cache.get(1L);
		assertEquals("admin", user3.getLoginName());
		assertFalse(appender.isEmpty());
	}
}
