/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.test.data;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * SQL数据文件导入工具类。
 * 
 * @author Calvin
 */
public class DataFixtures {

	public static final String DEFAULT_ENCODING = "UTF-8";

	private static ResourceLoader resourceLoader = new DefaultResourceLoader();

	public static void executeScript(DataSource dataSource, String... sqlResourcePaths) throws DataAccessException,
			SQLException {
		for (String sqlResourcePath : sqlResourcePaths) {
			Resource resource = resourceLoader.getResource(sqlResourcePath);
			ScriptUtils.executeSqlScript(dataSource.getConnection(), new EncodedResource(resource, DEFAULT_ENCODING));
		}
	}
}
