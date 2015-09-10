/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package org.springside.modules.metrics;

public abstract class Gauge {

	public Number latestMetric;

	public void calculateMetric() {
		latestMetric = getValue();
	}

	protected abstract Number getValue();

	@Override
	public String toString() {
		return "Gauge [latestMetric=" + latestMetric + "]";
	}
}
