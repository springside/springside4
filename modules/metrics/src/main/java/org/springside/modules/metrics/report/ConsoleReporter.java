package org.springside.modules.metrics.report;

import java.io.PrintStream;
import java.util.Map;
import java.util.SortedMap;

import org.springside.modules.metrics.report.metrics.CounterMetric;
import org.springside.modules.metrics.report.metrics.HistogramMetric;

public class ConsoleReporter implements Reporter {
	private static final int CONSOLE_WIDTH = 80;

	private PrintStream output = System.out;

	@Override
	public void report(SortedMap<String, CounterMetric> counters, SortedMap<String, HistogramMetric> histograms) {
		if (!counters.isEmpty()) {
			printWithBanner("-- Counters", '-');
			for (Map.Entry<String, CounterMetric> entry : counters.entrySet()) {
				output.println(entry.getKey());
				printCounter(entry.getValue());
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
		output.printf("             count = %d%n", counter.coute);
		output.printf("         last rate = %2.2f events/%s%n", counter.lastRate);
		output.printf("         mean rate = %2.2f events/%s%n", counter.meanRate);
	}
}
