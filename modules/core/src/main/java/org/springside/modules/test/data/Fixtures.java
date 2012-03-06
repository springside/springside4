package org.springside.modules.test.data;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public abstract class Fixtures {

	private static Logger logger = LoggerFactory.getLogger(Fixtures.class);
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

	protected static IDatabaseConnection getConnection(DataSource dataSource) throws DatabaseUnitException,
			SQLException {
		throw new UnsupportedOperationException("Need to override in subclass ");
	}

}
