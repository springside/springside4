/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.test.data;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2Connection;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springside.modules.utils.Exceptions;

/**
 * 基于DBUnit初始化测试数据到H2数据库的工具类.
 */
public class H2Fixtures {

	private H2Fixtures() {
	}

	private static Logger logger = LoggerFactory.getLogger(H2Fixtures.class);
	private static ResourceLoader resourceLoader = new DefaultResourceLoader();

	/**
	 * 插入XML文件中的数据到数据库.
	 *  
	 * @param xmlFilePaths 符合Spring Resource路径格式的文件路径列表.
	 */
	public static void loadData(DataSource dataSource, String... xmlFilePaths) throws Exception {
		execute(DatabaseOperation.INSERT, dataSource, xmlFilePaths);
	}

	/**
	 * 先删除XML数据文件中涉及的表的数据, 再插入XML文件中的数据到数据库.
	 * 
	 * 在更新全部表时速度没有reloadAllTable快且容易产生锁死, 适合只更新小部分表的数据的情况.
	 * 
	 * @param xmlFilePaths 符合Spring Resource路径格式的文件路径列表.
	 */
	public static void reloadData(DataSource dataSource, String... xmlFilePaths) throws Exception {
		execute(DatabaseOperation.CLEAN_INSERT, dataSource, xmlFilePaths);
	}

	/**
	 * 在数据库中删除XML文件中涉及的表的数据. 
	 * 
	 * @param xmlFilePaths 符合Spring Resource路径格式的文件路径列表.
	 */
	public static void deleteData(DataSource dataSource, String... xmlFilePaths) throws Exception {
		execute(DatabaseOperation.DELETE_ALL, dataSource, xmlFilePaths);
	}

	/**
	 * 对XML文件中的数据在数据库中执行Operation.
	 * 
	 * @param xmlFilePaths 符合Spring Resource路径格式的文件列表.
	 */
	private static void execute(DatabaseOperation operation, DataSource dataSource, String... xmlFilePaths)
			throws DatabaseUnitException, SQLException {
		IDatabaseConnection connection = getConnection(dataSource);

		for (String xmlPath : xmlFilePaths) {
			try {
				InputStream input = resourceLoader.getResource(xmlPath).getInputStream();
				IDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(input);
				operation.execute(connection, dataSet);
			} catch (IOException e) {
				logger.warn(xmlPath + " file not found", e);
			}
		}
	}

	protected static H2Connection getConnection(DataSource dataSource) throws DatabaseUnitException, SQLException {
		return new H2Connection(dataSource.getConnection(), null);
	}

	/**
	 * 先删除数据库中所有表的数据, 再插入XML文件中的数据到数据库.
	 * 
	 * @param xmlFilePaths 符合Spring Resource路径格式的文件路径列表.
	 */
	public static void reloadAllTable(DataSource dataSource, String... xmlFilePaths) throws Exception {
		deleteAllTable(dataSource);
		loadData(dataSource, xmlFilePaths);
	}

	/**
	 * 删除所有的表,excludeTables除外.在删除期间disable外键检查.
	 */
	public static void deleteAllTable(DataSource dataSource, String... excludeTables) {

		List<String> tableNames = new ArrayList<String>();
		try {
			ResultSet rs = dataSource.getConnection().getMetaData()
					.getTables(null, null, null, new String[] { "TABLE" });
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				if (Arrays.binarySearch(excludeTables, tableName) < 0) {
					tableNames.add(tableName);
				}
			}

			rs.close();

			deleteTable(dataSource, tableNames.toArray(new String[tableNames.size()]));
		} catch (SQLException e) {
			Exceptions.unchecked(e);
		}

	}

	/**
	 * 删除指定的表, 在删除期间disable外键的检查.
	 */
	public static void deleteTable(DataSource dataSource, String... tableNames) {
		JdbcTemplate template = new JdbcTemplate(dataSource);

		template.update("SET REFERENTIAL_INTEGRITY FALSE");

		for (String tableName : tableNames) {
			template.update("DELETE FROM " + tableName);
		}

		template.update("SET REFERENTIAL_INTEGRITY TRUE");
	}
}
