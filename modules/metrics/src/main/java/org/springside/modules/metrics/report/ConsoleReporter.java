package org.springside.modules.metrics.report;

import java.io.PrintStream;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.springside.modules.metrics.CounterMetric;
import org.springside.modules.metrics.ExecutionMetric;
import org.springside.modules.metrics.HistogramMetric;

public class ConsoleReporter implements Reporter {

	private static final int CONSOLE_WIDTH = 80;
	private PrintStream output = System.out;

	@Override
	public void report(Map<String, CounterMetric> counters, Map<String, HistogramMetric> histograms,
			Map<String, ExecutionMetric> executions) {

		printWithBanner(new Date().toString(), '=');
		output.println();

		if (!counters.isEmpty()) {
			printWithBanner("-- Counters", '-');
			for (Map.Entry<String, CounterMetric> entry : counters.entrySet()) {
				output.println(entry.getKey());
				printCounter(entry.getValue());
			}
			output.println();
		}

		if (!histograms.isEmpty()) {
			printWithBanner("-- Histograms", '-');
			for (Map.Entry<String, HistogramMetric> entry : histograms.entrySet()) {
				output.println(entry.getKey());
				printHistogram(entry.getValue());
			}
			output.println();
		}

		if (!executions.isEmpty()) {
			printWithBanner("-- Executions", '-');
			for (Map.Entry<String, ExecutionMetric> entry : executions.entrySet()) {
				output.println(entry.getKey());
				printExecution(entry.getValue());
			}
			output.println();
		}
	}

	private void printWithBanner(String s, char c) {
		output.print(s);
		output.print(' ');
		for (int i = 0; i < (CONSOLE_WIDTH - s.length() - 1); i++) {
			output.print(c);
		}
		output.println();
	}

	private void printCounter(CounterMetric counter) {
		output.printf("             count = %d%n", counter.count);
		output.printf("         last rate = %2.2f/s%n", counter.lastRate);
		output.printf("         mean rate = %2.2f/s%n", counter.meanRate);
	}

	private void printHistogram(HistogramMetric histogram) {
		output.printf("               min = %d%n", histogram.min);
		output.printf("               max = %d%n", histogram.max);
		output.printf("              mean = %2.2f%n", histogram.mean);
		for (Entry<Double, Long> pct : histogram.pcts.entrySet()) {
			output.printf("           %2.2f%% <= %d %n", pct.getKey(), pct.getValue());
		}
	}

	private void printExecution(ExecutionMetric execution) {
		output.printf("             count = %d%n", execution.counter.count);
		output.printf("         last rate = %2.2f/s%n", execution.counter.lastRate);
		output.printf("         mean rate = %2.2f/s%n", execution.counter.meanRate);
		output.printf("               min = %d ms%n", execution.histogram.min);
		output.printf("               max = %d ms%n", execution.histogram.max);
		output.printf("              mean = %2.2f ms%n", execution.histogram.mean);
		for (Entry<Double, Long> pct : execution.histogram.pcts.entrySet()) {
			output.printf("           %2.2f%% <= %d ms%n", pct.getKey(), pct.getValue());
		}
	}
}
