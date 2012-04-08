package org.springside.modules.test.data;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;

public class DefaultDataInitializer implements InitializingBean {

	private DataSource dataSource;

	private String defaultDataFile;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setDefaultDataFile(String defaultDataFile) {
		this.defaultDataFile = defaultDataFile;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Fixtures.reloadData(dataSource, defaultDataFile);
	}

}
