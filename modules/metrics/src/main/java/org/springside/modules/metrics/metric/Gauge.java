/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package org.springside.modules.metrics.metric;

public abstract class Gauge<T extends Number> {

	public T latestMetric;

	public void calculateMetric() {
		latestMetric = getValue();
	}

	protected abstract T getValue();

	@Override
	public String toString() {
		return "Gauge [latestMetric=" + latestMetric + "]";
	}
}
