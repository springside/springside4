package org.springside.examples.miniweb.data;

import javax.sql.DataSource;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springside.modules.test.data.Fixtures;

@Component
public class SampleDataRule implements TestRule {

	public static boolean dataLoaded = false;

	@Autowired
	public DataSource dataSource;

	@Override
	public Statement apply(Statement base, Description description) {
		if (!dataLoaded) {
			try {
				Fixtures.reloadData(dataSource, "/data/sample-data.xml");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataLoaded = true;
		}
		return base;
	}
}
