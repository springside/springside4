package org.springside.examples.showcase.cache.guava;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springside.examples.showcase.common.entity.User;
import org.springside.examples.showcase.common.service.AccountManager;
import org.springside.modules.test.data.H2Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 本地缓存演示，使用GuavaCache.
 * 可以通过log4jdbc查看sql情况.
 */
@ContextConfiguration(locations = { "/applicationContext.xml" })
@TransactionConfiguration(transactionManager = "defaultTransactionManager")
public class GuavaCacheDemo extends SpringTxTestCase {

	@Resource
	private AccountManager accountManager;

	@Test
	public void test1() throws Exception {
		H2Fixtures.reloadAllTable(dataSource, "/data/sample-data.xml");
		LoadingCache<Long, User> cache = CacheBuilder.newBuilder().maximumSize(100)//设置缓存个数
				.expireAfterAccess(3, TimeUnit.SECONDS)//缓存过期时间为8秒
				.build(new CacheLoader<Long, User>() {

					@Override
					public User load(Long key) throws Exception {
						return accountManager.getUser(key);
					}

				});

		User user = cache.get(1L);
		assertEquals("admin", user.getLoginName());//第一次加载会查数据库

		User user2 = cache.get(1L);//第二次加载时直接从缓存里取
		assertEquals("admin", user2.getLoginName());

		Thread.sleep(10000);
		user = cache.get(1L);
		;//第三次加载时，因为缓存已经过期所以会查数据库
		System.out.println(user);
		assertEquals("admin", user.getLoginName());
	}
}
