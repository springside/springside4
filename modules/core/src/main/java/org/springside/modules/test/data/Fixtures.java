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
import org.springside.modules.utils.PropertiesLoader;

/**
 * 基于DBUnit初始化测试数据到H2数据库的工具类.
 */
public class Fixtures {

	private static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);
	private static ResourceLoader resourceLoader = new DefaultResourceLoader();

	private Fixtures() {
	}

	/**
	 * 插入XML文件中的数据到H2数据库.
	 *  
	 * @param xmlFilePaths 符合Spring Resource路径格式的文件路径列表.
	 */
	public static void loadData(DataSource h2DataSource, String... xmlFilePaths) throws Exception {
		execute(DatabaseOperation.INSERT, h2DataSource, xmlFilePaths);
	}

	/**
	 * 先删除XML数据文件中涉及的表的数据, 再插入XML文件中的数据到H2数据库.
	 * 
	 * 在更新全部表时速度没有reloadAllTable快且容易产生锁死, 适合只更新小部分表的数据的情况.
	 * 
	 * @param xmlFilePaths 符合Spring Resource路径格式的文件路径列表.
	 */
	public static void reloadData(DataSource h2DataSource, String... xmlFilePaths) throws Exception {
		execute(DatabaseOperation.CLEAN_INSERT, h2DataSource, xmlFilePaths);
	}

	/**
	 * 先删除数据库中所有表的数据, 再插入XML文件中的数据到H2数据库.
	 * 
	 * @param xmlFilePaths 符合Spring Resource路径格式的文件路径列表.
	 */
	public static void reloadAllTable(DataSource h2DataSource, String... xmlFilePaths) throws Exception {
		deleteAllTable(h2DataSource);
		loadData(h2DataSource, xmlFilePaths);
	}

	/**
	 * 在H2数据库中删除XML文件中涉及的表的数据. 
	 * 
	 * @param xmlFilePaths 符合Spring Resource路径格式的文件路径列表.
	 */
	public static void deleteData(DataSource h2DataSource, String... xmlFilePaths) throws Exception {
		execute(DatabaseOperation.DELETE_ALL, h2DataSource, xmlFilePaths);
	}

	/**
	 * 对XML文件中的数据在H2数据库中执行Operation.
	 * 
	 * @param xmlFilePaths 符合Spring Resource路径格式的文件列表.
	 */
	private static void execute(DatabaseOperation operation, DataSource h2DataSource, String... xmlFilePaths)
			throws DatabaseUnitException, SQLException {
		//注意这里HardCode了使用H2的Connetion
		IDatabaseConnection h2Connection = new H2Connection(h2DataSource.getConnection(), null);

		for (String xmlPath : xmlFilePaths) {
			try {
				InputStream input = resourceLoader.getResource(xmlPath).getInputStream();
				IDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(input);
				operation.execute(h2Connection, dataSet);
			} catch (IOException e) {
				logger.warn(xmlPath + " file not found", e);
			}
		}
	}

	/**
	 * 删除所有的表,excludeTables除外.在删除期间disable外键检查.
	 */
	public static void deleteAllTable(DataSource h2DataSource, String... excludeTables) {

		List<String> tableNames = new ArrayList<String>();
		try {
			ResultSet rs = h2DataSource.getConnection().getMetaData()
					.getTables(null, null, null, new String[] { "TABLE" });
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				if (Arrays.binarySearch(excludeTables, tableName) < 0) {
					tableNames.add(tableName);
				}
			}

			deleteTable(h2DataSource, tableNames.toArray(new String[tableNames.size()]));
		} catch (SQLException e) {
			Exceptions.unchecked(e);
		}

	}

	/**
	 * 删除指定的表, 在删除期间disable外键的检查.
	 */
	public static void deleteTable(DataSource h2DataSource, String... tableNames) {
		JdbcTemplate template = new JdbcTemplate(h2DataSource);

		template.update("SET REFERENTIAL_INTEGRITY FALSE");

		for (String tableName : tableNames) {
			template.update("DELETE FROM " + tableName);
		}

		template.update("SET REFERENTIAL_INTEGRITY TRUE");
	}
}
