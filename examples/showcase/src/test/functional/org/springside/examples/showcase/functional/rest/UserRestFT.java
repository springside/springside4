package org.springside.examples.showcase.functional.rest;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.showcase.functional.BaseFunctionalTestCase;
import org.springside.examples.showcase.webservice.rest.UserDTO;
import org.springside.modules.test.category.Smoke;
import org.springside.modules.web.Servlets;

import com.google.common.collect.Lists;

/**
 * 演示Rest Service的客户端，包含：
 * 1. 使用exchange()函数与使用对Header操作的两种方式
 * 2. JDK HttpConnection与Apache HttpClient4两种底层
 * 3. 验证JSON与XML两种编码
 * 4. 验证与Shiro结合的HttpBasic验证
 * 
 * @author calvin
 */
public class UserRestFT extends BaseFunctionalTestCase {

	private RestTemplate jdkTemplate;
	private RestTemplate httpClientRestTemplate;
	private HttpComponentsClientHttpRequestFactory httpClientRequestFactory;

	private static String resoureUrl;

	@BeforeClass
	public static void initUrl() {
		resoureUrl = baseUrl + "/api/v1/user";
	}

	@Before
	public void initRestTemplate() {
		// 默认使用JDK Connection
		jdkTemplate = new RestTemplate();
		// (optional)设置20秒超时
		((SimpleClientHttpRequestFactory) jdkTemplate.getRequestFactory()).setConnectTimeout(20000);

		// 设置使用HttpClient4.0
		httpClientRestTemplate = new RestTemplate();
		httpClientRequestFactory = new HttpComponentsClientHttpRequestFactory();
		// (optional)设置20秒超时
		httpClientRequestFactory.setConnectTimeout(20000);

		httpClientRestTemplate.setRequestFactory(httpClientRequestFactory);

		// 设置处理HttpBasic Header的Interceptor
		ClientHttpRequestInterceptor interceptor = new HttpBasicInterceptor("admin", "admin");
		httpClientRestTemplate.setInterceptors(Lists.newArrayList(interceptor));
	}

	@After
	public void destoryClient() {
		// 退出时关闭HttpClient4连接池中的连接
		httpClientRequestFactory.destroy();
	}

	/**
	 * 演示使用原始的exchange()方法来设置Headers.
	 * 演示xml格式数据.
	 * 演示jdk connection.
	 */
	@Test
	public void getUserAsXML() {
		// 设置Http Basic参数
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(com.google.common.net.HttpHeaders.AUTHORIZATION, Servlets.encodeHttpBasic("admin", "admin"));
		HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);

		HttpEntity<UserDTO> response = jdkTemplate.exchange(resoureUrl + "/{id}.xml", HttpMethod.GET, requestEntity,
				UserDTO.class, 1L);
		assertEquals("admin", response.getBody().getLoginName());
		assertEquals("管理员", response.getBody().getName());
		assertEquals(new Long(1), response.getBody().getTeamId());

		// 直接取出XML串
		HttpEntity<String> xml = jdkTemplate.exchange(resoureUrl + "/{id}.xml", HttpMethod.GET, requestEntity,
				String.class, 1L);
		System.out.println("xml output is " + xml.getBody());
	}

	/**
	 * 演示使用ClientHttpRequestInterceptor设置header
	 * 演示json格式数据.
	 * 演示使用Apache Http client4.
	 */
	@Test
	@Category(Smoke.class)
	public void getUserAsJson() {
		UserDTO user = httpClientRestTemplate.getForObject(resoureUrl + "/{id}.json", UserDTO.class, 1L);
		assertEquals("admin", user.getLoginName());
		assertEquals("管理员", user.getName());
		assertEquals(new Long(1), user.getTeamId());

		// 直接取出JSON串
		String json = httpClientRestTemplate.getForObject(resoureUrl + "/{id}.json", String.class, 1L);
		System.out.println("json output is " + json);
	}

	/**
	 * 验证与Shiro的HttpBasic的结合
	 */
	@Test
	public void authWithHttpBasic() {
		// 设置Http Basic参数
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(com.google.common.net.HttpHeaders.AUTHORIZATION,
				Servlets.encodeHttpBasic("admin", "wrongpassword"));
		HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);

		try {
			jdkTemplate.exchange(resoureUrl + "/{id}.xml", HttpMethod.GET, requestEntity, UserDTO.class, 1L);
			fail("Get should fail with error username/password");
		} catch (HttpClientErrorException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
		}
	}

	/**
	 * 处理HttpBasicHeader的Interceptor
	 */
	public static class HttpBasicInterceptor implements ClientHttpRequestInterceptor {

		private final String user;
		private final String password;

		public HttpBasicInterceptor(String user, String password) {
			this.user = user;
			this.password = password;
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			request.getHeaders().set(com.google.common.net.HttpHeaders.AUTHORIZATION,
					Servlets.encodeHttpBasic(user, password));
			return execution.execute(request, body);
		}
	}
}
