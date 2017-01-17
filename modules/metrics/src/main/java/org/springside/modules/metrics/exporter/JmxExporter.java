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
import org.springside.modules.metrics.Exporter;
import org.springside.modules.metrics.MetricRegistry;
import org.springside.modules.metrics.MetricRegistryListener;
import org.springside.modules.metrics.metric.Counter;
import org.springside.modules.metrics.metric.Gauge;
import org.springside.modules.metrics.metric.Histogram;
import org.springside.modules.metrics.metric.Timer;

/**
 * 以JMX形式，暴露所有Metrics.
 * 
 * 为每一个Metrics注册一个MBean, 实现MetricRegistryListener接口感知Metrics的变化.
 * 
 * MBean名字为 ${dommianName}:name=${metriceName}
 * 属性名见JmxGauge， JmxCounter， JmxHistogram，JmxTimer四个类的Getter函数名.
 * 
 * TODO: 另一个将所有Metrics暴露成一个JSON字符串的实现.
 */
public class JmxExporter implements Exporter, MetricRegistryListener {

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

	/**
	 * 将Registry中所有的Metrics注册为独立的MBean
	 */
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

	/**
	 * 将Registry中所有的Metrics取消MBean注册
	 */
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

	private void registerMBean(ObjectName objectName, Object mBean) {
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
			logger.debug("Unable to register already exist mbean:" + objectName, e);
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

	private ObjectName createObjectName(String name) {
		try {
			return new ObjectName(this.domain, "name", name);
		} catch (MalformedObjectNameException e) {
			try {
				return new ObjectName(this.domain, "name", ObjectName.quote(name));
			} catch (MalformedObjectNameException e1) {
				logger.warn("Unable to register {}", name, e1);
				throw new RuntimeException(e1);
			}
		}
	}

	@Override
	public void onGaugeAdded(String name, Gauge gauge) {
		ObjectName objectName = createObjectName(name);
		registerMBean(objectName, new JmxGauge(gauge, objectName));
	}

	@Override
	public void onCounterAdded(String name, Counter counter) {
		ObjectName objectName = createObjectName(name);
		registerMBean(objectName, new JmxCounter(counter, objectName));
	}

	@Override
	public void onHistogramAdded(String name, Histogram histogram) {
		ObjectName objectName = createObjectName(name);
		registerMBean(objectName, new JmxHistogram(histogram, objectName));
	}

	@Override
	public void onTimerAdded(String name, Timer timer) {
		ObjectName objectName = createObjectName(name);
		registerMBean(objectName, new JmxTimer(timer, objectName));
	}

	@Override
	public void onGaugeRemoved(String name) {
		ObjectName objectName = createObjectName(name);
		unregisterMBean(objectName);
	}

	@Override
	public void onCounterRemoved(String name) {
		ObjectName objectName = createObjectName(name);
		unregisterMBean(objectName);
	}

	@Override
	public void onHistogramRemoved(String name) {
		ObjectName objectName = createObjectName(name);
		unregisterMBean(objectName);
	}

	@Override
	public void onTimerRemoved(String name) {
		ObjectName objectName = createObjectName(name);
		unregisterMBean(objectName);
	}
	
	///////// MBean定义///////
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

		long getAvgRate();
	}

	public interface JmxHistogramMBean extends MetricMBean {

		long getMin();

		long getMax();

		double getAvg();

		Map<Double,Long> getPcts();
	}

	public interface JmxTimerMBean extends MetricMBean {
		long getLatestCount();

		long getLatestRate();

		long getTotalCount();

		long getAvgRate();

		long getMinLatency();

		long getMaxLatency();

		double getAvgLatency();
		
		Map<Double,Long> getPcts();
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

	/**
	 * Gauge类型的JmxMbean, JMX属性名见getter函数名.
	 */
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

	/**
	 * Counter类型的JmxMbean, JMX属性名见getter函数名.
	 */
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
		public long getAvgRate() {
			return metric.latestMetric.avgRate;
		}
	}

	/**
	 * Histogram类型的JmxMbean, JMX属性名见getter函数名.
	 */
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
		public double getAvg() {
			return metric.latestMetric.avg;
		}

		@Override
		public Map<Double, Long> getPcts() {
			return metric.latestMetric.pcts;
		}
	}

	/**
	 * Timer类型的JmxMbean, JMX属性名见getter函数名.
	 */
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
		public long getAvgRate() {
			return metric.latestMetric.counterMetric.avgRate;
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
		public double getAvgLatency() {
			return metric.latestMetric.histogramMetric.avg;
		}

		@Override
		public Map<Double, Long> getPcts() {
			return metric.latestMetric.histogramMetric.pcts;
		}
	}
}
