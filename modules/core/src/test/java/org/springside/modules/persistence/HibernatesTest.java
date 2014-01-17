/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.persistence;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.junit.Test;

public class HibernatesTest {

	@Test
	public void testGetDialect() throws SQLException {
		DataSource mockDataSource = mock(DataSource.class);
		Connection mockConnection = mock(Connection.class);
		DatabaseMetaData mockMetaData = mock(DatabaseMetaData.class);

		when(mockDataSource.getConnection()).thenReturn(mockConnection);
		when(mockConnection.getMetaData()).thenReturn(mockMetaData);

		when(mockMetaData.getURL()).thenReturn("jdbc:h2:file:~/test;AUTO_SERVER=TRUE");
		String dialect = Hibernates.getDialect(mockDataSource);
		assertThat(dialect).isEqualTo(H2Dialect.class.getName());

		when(mockMetaData.getURL()).thenReturn("jdbc:mysql://localhost:3306/test");
		dialect = Hibernates.getDialect(mockDataSource);
		assertThat(dialect).isEqualTo(MySQL5InnoDBDialect.class.getName());

		when(mockMetaData.getURL()).thenReturn("jdbc:oracle:thin:@127.0.0.1:1521:XE");
		dialect = Hibernates.getDialect(mockDataSource);
		assertThat(dialect).isEqualTo(Oracle10gDialect.class.getName());
	}
}
