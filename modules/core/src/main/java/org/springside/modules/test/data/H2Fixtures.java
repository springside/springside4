/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.test.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.ext.h2.H2Connection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springside.modules.utils.Exceptions;

/**
 * 基于DBUnit初始化测试数据到H2数据库的工具类.
 */
public class H2Fixtures extends Fixtures {

	private H2Fixtures() {
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
