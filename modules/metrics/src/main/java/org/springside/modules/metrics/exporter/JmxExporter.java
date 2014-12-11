package org.springside.modules.metrics.exporter;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.metrics.Counter;
import org.springside.modules.metrics.Gauge;
import org.springside.modules.metrics.Histogram;
import org.springside.modules.metrics.MetricRegistry;
import org.springside.modules.metrics.Timer;

public class JmxExporter implements MetricRegistryListener {

	private static Logger logger = LoggerFactory.getLogger(JmxExporter.class);
	private MBeanServer mBeanServer;
	private MetricRegistry registry;
	private String domain;
	private final Map<ObjectName, ObjectName> registered;

	public JmxExporter(String domain, MetricRegistry registry) {
		this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
		this.registry = registry;
		this.domain = domain;
		this.registered = new ConcurrentHashMap<ObjectName, ObjectName>();

		registry.addListener(this);
	}

	public void initMBeans() {

		Map<String, Gauge> gauges = registry.getGauges();
		for (Entry<String, Gauge> entry : gauges.entrySet()) {
			onGaugeAdded(entry.getKey(), entry.getValue());
		}

		Map<String, Counter> counters = registry.getCounters();
		for (Entry<String, Counter> entry : counters.entrySet()) {
			onCounterAdded(entry.getKey(), entry.getValue());
		}

		Map<String, Histogram> histograms = registry.getHistograms();
		for (Entry<String, Histogram> entry : histograms.entrySet()) {
			onHistogramAdded(entry.getKey(), entry.getValue());
		}

		Map<String, Timer> timers = registry.getTimers();
		for (Entry<String, Timer> entry : timers.entrySet()) {
			onTimerAdded(entry.getKey(), entry.getValue());
		}
	}

	public void destroyMBeans() {
		Map<String, Gauge> gauges = registry.getGauges();
		for (String key : gauges.keySet()) {
			onGaugeRemoved(key);
		}

		Map<String, Counter> counters = registry.getCounters();
		for (String key : counters.keySet()) {
			onCounterRemoved(key);
		}

		Map<String, Histogram> histograms = registry.getHistograms();
		for (String key : histograms.keySet()) {
			onHistogramRemoved(key);
		}

		Map<String, Timer> timers = registry.getTimers();
		for (String key : timers.keySet()) {
			onTimerRemoved(key);
		}
	}

	private void registerMBean(Object mBean, ObjectName objectName) {
		try {
			ObjectInstance objectInstance = mBeanServer.registerMBean(mBean, objectName);
			if (objectInstance != null) {
				// the websphere mbeanserver rewrites the objectname to include cell, node & server info
				// make sure we capture the new objectName for unregistration
				registered.put(objectName, objectInstance.getObjectName());
			} else {
				registered.put(objectName, objectName);
			}
		} catch (InstanceAlreadyExistsException e) {
			logger.debug("Unable to register:" + objectName, e);
		} catch (JMException e) {
			logger.warn("Unable to register:" + objectName, e);
		}
	}

	private void unregisterMBean(ObjectName originalObjectName) {
		ObjectName storedObjectName = registered.remove(originalObjectName);
		try {
			if (storedObjectName != null) {
				mBeanServer.unregisterMBean(storedObjectName);

			} else {
				mBeanServer.unregisterMBean(originalObjectName);
			}
		} catch (InstanceNotFoundException e) {
			logger.debug("Unable to unregister:" + originalObjectName, e);
		} catch (MBeanRegistrationException e) {
			logger.warn("Unable to unregister:" + originalObjectName, e);
		}
	}

	private ObjectName createName(String type, String name) {
		try {
			return new ObjectName(this.domain, "name", name);
		} catch (MalformedObjectNameException e) {
			try {
				return new ObjectName(this.domain, "name", ObjectName.quote(name));
			} catch (MalformedObjectNameException e1) {
				logger.warn("Unable to register {} {}", type, name, e1);
				throw new RuntimeException(e1);
			}
		}
	}

	@Override
	public void onGaugeAdded(String name, Gauge gauge) {
		final ObjectName objectName = createName("gauges", name);
		registerMBean(new JmxGauge(gauge, objectName), objectName);
	}

	@Override
	public void onCounterAdded(String name, Counter counter) {
		final ObjectName objectName = createName("counters", name);
		registerMBean(new JmxCounter(counter, objectName), objectName);
	}

	@Override
	public void onHistogramAdded(String name, Histogram histogram) {
		final ObjectName objectName = createName("histograms", name);
		registerMBean(new JmxHistogram(histogram, objectName), objectName);
	}

	@Override
	public void onTimerAdded(String name, Timer timer) {
		final ObjectName objectName = createName("timers", name);
		registerMBean(new JmxTimer(timer, objectName), objectName);
	}

	@Override
	public void onGaugeRemoved(String name) {
		final ObjectName objectName = createName("guages", name);
		unregisterMBean(objectName);
	}

	@Override
	public void onCounterRemoved(String name) {
		final ObjectName objectName = createName("counters", name);
		unregisterMBean(objectName);
	}

	@Override
	public void onHistogramRemoved(String name) {
		final ObjectName objectName = createName("histograms", name);
		unregisterMBean(objectName);
	}

	@Override
	public void onTimerRemoved(String name) {
		final ObjectName objectName = createName("timers", name);
		unregisterMBean(objectName);
	}

	public interface MetricMBean {
		ObjectName objectName();
	}

	public interface JmxGaugeMBean extends MetricMBean {
		Number getValue();
	}

	public interface JmxCounterMBean extends MetricMBean {
		long getLatestCount();

		long getLatestRate();

		long getTotalCount();

		long getMeanRate();
	}

	public interface JmxHistogramMBean extends MetricMBean {

		long getMin();

		long getMax();

		double getMean();

		// TODO: add pcts
	}

	public interface JmxTimerMBean extends MetricMBean {
		long getLatestCount();

		long getLatestRate();

		long getTotalCount();

		long getMeanRate();

		long getMinLatency();

		long getMaxLatency();

		double getMeanLatency();

		// TODO: add pcts
	}

	private abstract static class AbstractBean implements MetricMBean {
		private final ObjectName objectName;

		AbstractBean(ObjectName objectName) {
			this.objectName = objectName;
		}

		@Override
		public ObjectName objectName() {
			return objectName;
		}
	}

	private static class JmxGauge extends AbstractBean implements JmxGaugeMBean {

		private final Gauge metric;

		public JmxGauge(Gauge gauge, ObjectName objectName) {
			super(objectName);
			this.metric = gauge;
		}

		@Override
		public Number getValue() {
			return metric.latestMetric;
		}
	}

	private static class JmxCounter extends AbstractBean implements JmxCounterMBean {
		private final Counter metric;

		private JmxCounter(Counter metric, ObjectName objectName) {
			super(objectName);
			this.metric = metric;
		}

		@Override
		public long getLatestCount() {
			return metric.latestMetric.latestCount;
		}

		@Override
		public long getLatestRate() {
			return metric.latestMetric.latestRate;
		}

		@Override
		public long getTotalCount() {
			return metric.latestMetric.totalCount;
		}

		@Override
		public long getMeanRate() {
			return metric.latestMetric.meanRate;
		}

	}

	private static class JmxHistogram extends AbstractBean implements JmxHistogramMBean {
		private final Histogram metric;

		private JmxHistogram(Histogram metric, ObjectName objectName) {
			super(objectName);
			this.metric = metric;
		}

		@Override
		public long getMin() {
			return metric.latestMetric.min;
		}

		@Override
		public long getMax() {
			return metric.latestMetric.max;
		}

		@Override
		public double getMean() {
			return metric.latestMetric.mean;
		}
	}

	private static class JmxTimer extends AbstractBean implements JmxTimerMBean {
		private final Timer metric;

		private JmxTimer(Timer metric, ObjectName objectName) {
			super(objectName);
			this.metric = metric;
		}

		@Override
		public long getLatestCount() {
			return metric.latestMetric.counterMetric.latestCount;
		}

		@Override
		public long getLatestRate() {
			return metric.latestMetric.counterMetric.latestRate;
		}

		@Override
		public long getTotalCount() {
			return metric.latestMetric.counterMetric.totalCount;
		}

		@Override
		public long getMeanRate() {
			return metric.latestMetric.counterMetric.meanRate;
		}

		@Override
		public long getMinLatency() {
			return metric.latestMetric.histogramMetric.min;
		}

		@Override
		public long getMaxLatency() {
			return metric.latestMetric.histogramMetric.max;
		}

		@Override
		public double getMeanLatency() {
			return metric.latestMetric.histogramMetric.mean;
		}
	}
}
