package org.springside.modules.test.data;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.spring.SpringTransactionalTestCase;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class DataFixturesTest extends SpringTransactionalTestCase {

	@Test
	public void normal() throws BeansException, Exception {
		simpleJdbcTemplate.update("drop all objects");

		executeSqlScript("classpath:/schema.sql", false);

		DataFixtures.loadData(dataSource, "classpath:/test-data.xml");
		assertEquals(6, countRowsInTable("SS_USER"));

		DataFixtures.reloadData(dataSource, "classpath:/test-data.xml");
		assertEquals(6, countRowsInTable("SS_USER"));

		DataFixtures.deleteData(dataSource, "classpath:/test-data.xml");
		assertEquals(0, countRowsInTable("SS_USER"));

	}
}
