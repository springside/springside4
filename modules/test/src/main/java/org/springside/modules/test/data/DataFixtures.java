package org.springside.modules.test.data;

import javax.sql.DataSource;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

public class DataFixtures {

	private static ResourceLoader resourceLoader = new DefaultResourceLoader();

	public static void executeScript(DataSource dataSource, String... sqlResourcePaths) throws DataAccessException {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		for (String sqlResourcePath : sqlResourcePaths) {
			JdbcTestUtils.executeSqlScript(jdbcTemplate, resourceLoader, sqlResourcePath, true);
		}
	}
}
