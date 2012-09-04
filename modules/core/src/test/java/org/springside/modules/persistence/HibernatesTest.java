package org.springside.modules.persistence;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.junit.Test;
import org.mockito.Mockito;

public class HibernatesTest {

	@Test
	public void testGetDialect() throws SQLException {
		DataSource mockDataSource = Mockito.mock(DataSource.class);
		Connection mockConnection = Mockito.mock(Connection.class);
		DatabaseMetaData mockMetaData = Mockito.mock(DatabaseMetaData.class);

		Mockito.when(mockDataSource.getConnection()).thenReturn(mockConnection);
		Mockito.when(mockConnection.getMetaData()).thenReturn(mockMetaData);

		Mockito.when(mockMetaData.getURL()).thenReturn("jdbc:h2:file:~/test;AUTO_SERVER=TRUE");
		String dialect = Hibernates.getDialect(mockDataSource);
		assertEquals(H2Dialect.class.getName(), dialect);

		Mockito.when(mockMetaData.getURL()).thenReturn("jdbc:mysql://localhost:3306/test");
		dialect = Hibernates.getDialect(mockDataSource);
		assertEquals(MySQL5InnoDBDialect.class.getName(), dialect);

		Mockito.when(mockMetaData.getURL()).thenReturn("jdbc:oracle:thin:@127.0.0.1:1521:XE");
		dialect = Hibernates.getDialect(mockDataSource);
		assertEquals(Oracle10gDialect.class.getName(), dialect);
	}
}
