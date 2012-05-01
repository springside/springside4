package org.springside.modules.test.spring;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
				"classpath:/applicationContext-test.xml");
		assertNotNull(SpringContextHolder.getApplicationContext());

		SpringContextHolder holder = SpringContextHolder.getBean("springContextHolder");
		assertEquals(SpringContextHolder.class, holder.getClass());

		SpringContextHolder holderByClass = SpringContextHolder.getBean(SpringContextHolder.class);
		assertEquals(SpringContextHolder.class, holderByClass.getClass());

		context.close();

		try {
			holder.getApplicationContext();
			fail("No exception throw for applicationContxt had been close.");
		} catch (IllegalStateException e) {

		}
	}
}
