package org.springside.modules.orm;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.Oracle10gDialect;

public class Hibernates {

	/**
	 * Initialize the lazy property value, eg.
	 * 
	 * Hibernates.initLazyProperty(user.getGroups()); 
	 */
	public static void initLazyProperty(Object proxyedPropertyValue) {
		Hibernate.initialize(proxyedPropertyValue);
	}
	
	/**
	 * 根据DataSoure里的url判断Dialect类型.
	 */
	public static String getDialect(DataSource dataSource) {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			if (connection == null) {
				throw new IllegalStateException("Connection returned by DataSource [" + dataSource + "] was null");
			}

			String url = connection.getMetaData().getURL();

			if (StringUtils.contains(url, ":h2:")) {
				return H2Dialect.class.getName();
			} else if (StringUtils.contains(url, ":mysql:")) {
				return MySQL5InnoDBDialect.class.getName();
			} else if (StringUtils.contains(url, ":oracle:")) {
				return Oracle10gDialect.class.getName();
			} else {
				throw new IllegalArgumentException("Unknown Database");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Could not get database url", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
	}
}
