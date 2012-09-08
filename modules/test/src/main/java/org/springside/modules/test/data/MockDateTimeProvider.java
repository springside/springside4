package org.springside.modules.test.data;

import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.support.DateTimeProvider;

public class MockDateTimeProvider implements DateTimeProvider {

	private final DateTime dateTime;

	public MockDateTimeProvider(DateTime dateTime) {
		this.dateTime = dateTime;
	}

	@Override
	public DateTime getDateTime() {
		return dateTime;
	}

}
