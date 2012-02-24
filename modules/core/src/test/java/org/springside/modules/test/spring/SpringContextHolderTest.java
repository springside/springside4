package org.springside.modules.test.spring;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springside.modules.test.spring.SpringContextHolder;
import org.springside.modules.utils.Reflections;

public class SpringContextHolderTest {

	@Test
	public void testGetBean() {

		SpringContextHolder.clearHolder();
		try {
			SpringContextHolder.getBean("foo");
			fail("No exception throw for applicationContxt hadn't been init.");
		} catch (IllegalStateException e) {

		}

		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:/applicationContext-core-test.xml");
		assertNotNull(SpringContextHolder.getApplicationContext());

		SpringContextHolder holderByName = SpringContextHolder.getBean("springContextHolder");
		assertEquals(SpringContextHolder.class, holderByName.getClass());

		SpringContextHolder holderByClass = SpringContextHolder.getBean(SpringContextHolder.class);
		assertEquals(SpringContextHolder.class, holderByClass.getClass());

		context.close();
		assertNull(Reflections.getFieldValue(holderByName, "applicationContext"));

	}
}
