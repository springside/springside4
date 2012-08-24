package org.springside.modules.test.data;

import javax.sql.DataSource;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

public class DataFixtures {

	private static ResourceLoader resourceLoader = new DefaultResourceLoader();

	public static void executeScript(DataSource dataSource, String... sqlResourcePaths) throws DataAccessException {
		SimpleJdbcTemplate simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);

		for (String sqlResourcePath : sqlResourcePaths) {
			Resource resource = resourceLoader.getResource(sqlResourcePath);
			SimpleJdbcTestUtils.executeSqlScript(simpleJdbcTemplate, new EncodedResource(resource, null), true);
		}
	}
}
