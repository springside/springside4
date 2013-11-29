package org.springside.modules.metrics.report;

import java.util.List;

import org.springside.modules.metrics.MetricRegistry;

public class ReportScheduler {

	private List<Reporter> reporters;
	private MetricRegistry metricRegistry;

	public ReportScheduler(MetricRegistry metricRegistry, List<Reporter> repoters) {
		this.reporters = repoters;
	}

	public void report() {

	}

}
