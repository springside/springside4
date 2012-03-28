package org.springside.examples.showcase.cache.guava;

import static org.junit.Assert.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springside.examples.showcase.common.entity.User;
import org.springside.examples.showcase.common.service.AccountManager;
import org.springside.modules.test.data.H2Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * 本地缓存演示，使用GuavaCache.
 * 可以通过log4jdbc查看sql情况.
 */
@ContextConfiguration(locations = { "/applicationContext.xml" })
@TransactionConfiguration(transactionManager = "defaultTransactionManager")
public class GuavaCacheDemo extends SpringTxTestCase {

	private LoadingCache<Long, User> userCacheFormCacheLoader;

	private Cache<Long, User> userCacheFormCallable = CacheBuilder.newBuilder().maximumSize(100).build();

	private AccountManager accountManager;

	@Resource
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
		initUserCacheFormCacheLoader();
	}

	/**
	 * 初始化缓存.
	 */
	private void initUserCacheFormCacheLoader() {
		userCacheFormCacheLoader = CacheBuilder.newBuilder().maximumSize(100)//设置缓存个数
				.expireAfterAccess(3, TimeUnit.SECONDS)//缓存过期时间为8秒
				.removalListener(new RemovalListener<Long, User>() {//增加移除监听器

							@Override
							public void onRemoval(RemovalNotification<Long, User> notification) {
								System.out.println("用户id为:" + notification.getKey() + "被移除");

							}

						}).build(new CacheLoader<Long, User>() {

					@Override
					public User load(Long key) throws Exception {
						return accountManager.getUser(key);
					}

				});
	}

	@Before
	public void before() throws Exception {
		H2Fixtures.reloadAllTable(dataSource, "/data/sample-data.xml");

	}

	/**
	 * 演示从CacheLoader加载数据.
	 */
	@Test
	public void testFormCacheLoader() throws Exception {
		User user = userCacheFormCacheLoader.get(1L);//第一次加载会查数据库
		assertEquals("admin", user.getLoginName());

		User user2 = userCacheFormCacheLoader.get(1L);//第二次加载时直接从缓存里取
		assertEquals("admin", user2.getLoginName());
		assertSame(user, user2);
		Thread.sleep(8000);
		user = userCacheFormCacheLoader.get(1L);
		//第三次加载时，因为缓存已经过期所以会查数据库
		System.out.println(user);
		assertEquals("admin", user.getLoginName());
	}

	/**
	 * 演示从Callable加载数据.
	 */
	@Test
	public void testFormCallable() {
		final long userId = 1L;
		User user = getUserFromCallableCache(userId);//第一次加载会查数据库
		assertEquals("admin", user.getLoginName());
		User user2 = getUserFromCallableCache(userId);//第二次加载时直接从缓存里取
		assertEquals("admin", user2.getLoginName());
	}

	private User getUserFromCallableCache(final Long userId) {
		try {
			//Callable只有在缓存值不存在时，才会调用
			return userCacheFormCallable.get(userId, new Callable<User>() {
				@Override
				public User call() throws Exception {
					return accountManager.getUser(userId);
				}
			});
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}

	}
}
