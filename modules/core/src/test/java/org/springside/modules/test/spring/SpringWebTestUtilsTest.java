package org.springside.modules.test.spring;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringWebTestUtilsTest {

	@Test
	public void initByPaths() {
		MockServletContext servletContext = new MockServletContext();
		SpringWebTestHelper.initWebApplicationContext(servletContext, "classpath:/applicationContext-core-test.xml");
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		assertNotNull(context);
		assertNotNull(context.getBean("springContextHolder"));
	}

	@Test
	public void initByApplicationConetxt() {
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext(
				"classpath:/applicationContext-core-test.xml");
		MockServletContext servletContext = new MockServletContext();
		SpringWebTestHelper.initWebApplicationContext(servletContext, ac);
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		assertNotNull(context);
		assertNotNull(context.getBean("springContextHolder"));
	}

	@Test
	public void closeApplicationContext() {
		MockServletContext servletContext = new MockServletContext();
		SpringWebTestHelper.initWebApplicationContext(servletContext, "classpath:/applicationContext-core-test.xml");

		SpringWebTestHelper.closeWebApplicationContext(servletContext);
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		assertNull(context);
	}
}
