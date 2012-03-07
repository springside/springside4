package org.springside.modules.test.data;

import static org.junit.Assert.*;

import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.data.H2Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

@ContextConfiguration(locations = { "/applicationContext-core-test.xml" })
public class FixturesTest extends SpringTxTestCase {

	@Test
	public void normal() throws BeansException, Exception {
		simpleJdbcTemplate.update("drop all objects");

		executeSqlScript("classpath:/schema.sql", false);

		DataSource ds = (DataSource) applicationContext.getBean("dataSource");
		H2Fixtures.loadData(ds, "classpath:/test-data.xml");
		assertEquals(6, countRowsInTable("SS_USER"));

		H2Fixtures.reloadData(ds, "classpath:/test-data.xml");
		assertEquals(6, countRowsInTable("SS_USER"));

		H2Fixtures.deleteData(ds, "classpath:/test-data.xml");
		assertEquals(0, countRowsInTable("SS_USER"));

		H2Fixtures.loadData(ds, "classpath:/test-data.xml");
		H2Fixtures.deleteAllTable(ds);
		assertEquals(0, countRowsInTable("SS_USER"));

		H2Fixtures.reloadAllTable(ds, "classpath:/test-data.xml");
		assertEquals(6, countRowsInTable("SS_USER"));

	}
}
