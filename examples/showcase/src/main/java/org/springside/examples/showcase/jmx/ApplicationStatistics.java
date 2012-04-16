package org.springside.examples.showcase.jmx;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(objectName = ApplicationStatistics.MBEAN_NAME, description = "Application Statistics Management Bean")
public class ApplicationStatistics {

	public static final String MBEAN_NAME = "showcase:name=ApplicationStatistics";

	private AtomicInteger listUserTimes = new AtomicInteger();
	private AtomicInteger updateUserTimes = new AtomicInteger();

	public void incrListUserTimes() {
		listUserTimes.incrementAndGet();
	}

	public void incrUpdateUserTimes() {
		updateUserTimes.incrementAndGet();
	}

	@ManagedAttribute(description = "Times of all users be listed")
	public int getListUserTimes() {
		return listUserTimes.get();
	}

	@ManagedAttribute(description = "Times of users be updated")
	public int getUpdateUserTimes() {
		return updateUserTimes.get();
	}

	@ManagedOperation(description = "Reset all statistics")
	public void resetStatistics() {
		listUserTimes.set(0);
		updateUserTimes.set(0);
	}

}
