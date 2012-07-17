package org.springside.modules.test.data;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;

/**
 * åœ¨Spring Contextå�¯åŠ¨æ—¶ï¼Œä½¿ç”¨DBUnitä¸€æ¬¡æ€§åˆ�å§‹åŒ–æ•°æ�®ã€‚
 * 
 * @author calvin
 */
public class DataInitializer implements InitializingBean {

	private DataSource dataSource;

	private String dataFile;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		DataFixtures.reloadData(dataSource, dataFile);
	}
}
