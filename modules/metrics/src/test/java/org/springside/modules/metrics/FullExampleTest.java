package org.springside.modules.metrics;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springside.modules.metrics.metric.Counter;
import org.springside.modules.metrics.metric.Histogram;
import org.springside.modules.metrics.metric.Timer;
import org.springside.modules.metrics.metric.Timer.TimerContext;
import org.springside.modules.metrics.reporter.ConsoleReporter;
import org.springside.modules.metrics.reporter.Slf4jReporter;

/**
 * 示例分别使用ConsoleReporter与Slf4jReporter, 演示Counter, Histogram 和 Timer的用法.
 */
public class FullExampleTest {

	@Test
	public void counterExample() throws InterruptedException {
		MetricRegistry metricRegistry = new MetricRegistry();

		ConsoleReporter consoleReporter = new ConsoleReporter();
		Counter counter = metricRegistry.counter(MetricRegistry.name("UserService", "getUser.counter"));
		
		ReportScheduler scheduler = new ReportScheduler(metricRegistry, consoleReporter);
		scheduler.start(1, TimeUnit.SECONDS);

		counter.inc();
		Thread.sleep(1050);

		counter.inc(2);
		Thread.sleep(1050);
		scheduler.stop();
	}

	@Test
	public void histogramExample() throws InterruptedException {
		MetricRegistry metricRegistry = new MetricRegistry();
		ConsoleReporter consoleReporter = new ConsoleReporter();

		ReportScheduler scheduler = new ReportScheduler(metricRegistry, consoleReporter);
		scheduler.start(1, TimeUnit.SECONDS);

		Histogram histogram = metricRegistry.histogram(MetricRegistry.name("UserService", "getUser.latency"));
		histogram.update(1);
		histogram.update(100);

		Thread.sleep(1050);
		// 增加百分位
		histogram.setPcts(new Double[] { 0.99d, 0.999d });
		histogram.update(2);
		histogram.update(200);
		Thread.sleep(1050);
		scheduler.stop();
	}

	@Test
	public void timerExample() throws InterruptedException {
		MetricRegistry metricRegistry = new MetricRegistry();
		Slf4jReporter slf4jReporter = new Slf4jReporter();

		ReportScheduler scheduler = new ReportScheduler(metricRegistry);
		scheduler.addReporter(slf4jReporter);
		
		scheduler.start(1, TimeUnit.SECONDS);

		Timer timer = metricRegistry.timer(MetricRegistry.name("UserService", "getUser.timer"),
				new Double[] { 0.99d, 0.999d });

		// 写法1
		TimerContext timerContext = timer.start();
		Thread.sleep(100);
		timerContext.stop();

		timerContext = timer.start();
		Thread.sleep(200);
		timerContext.stop();

		Thread.sleep(750);

		// 用法2
		long start = System.currentTimeMillis();
		Thread.sleep(150);
		timer.update(start);

		start = System.currentTimeMillis();
		Thread.sleep(250);
		timer.update(start);
		
		Thread.sleep(650);
		scheduler.stop();
	}

}
