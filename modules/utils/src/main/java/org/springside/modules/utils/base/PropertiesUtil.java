package org.springside.modules.utils.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.utils.io.GeneralResourceUtil;
import org.springside.modules.utils.io.IOUtil;
import org.springside.modules.utils.number.NumberUtil;

/**
 * 关于Properties的工具类
 * 
 * 1. 统一的读取系统变量，其中Boolean.readBoolean的风格不统一，Double则不支持，都进行了扩展.
 * 
 * 2. 简单的合并系统变量(-D)，环境变量 和默认值，以系统变量优先，在未引入Commons Config时使用.
 * 
 * 3. Properties 本质上是一个HashTable，每次读写都会加锁，所以不支持频繁的System.getProperty(name)来检查系统内容变化 因此扩展了一个ListenableProperties,
 * 在其所关心的属性变化时进行通知.
 * 
 * 4. 从文件或字符串装载Properties
 * 
 * @author calvin
 */
public abstract class PropertiesUtil {

	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	/////////// Boolean.readBoolean()扩展 ///////////////

	/**
	 * 读取Boolean类型的系统变量，为空时返回null，代表未设置，而不是Boolean.getBoolean()的false.
	 */
	public static Boolean booleanSystemProperty(String name) {
		String stringResult = System.getProperty(name);
		if (stringResult != null) {
			return Boolean.valueOf(stringResult);
		}
		return null;
	}

	/**
	 * 读取Boolean类型的系统变量，为空时返回默认值, 而不是Boolean.getBoolean()的false.
	 */
	public static Boolean booleanSystemProperty(String name, Boolean defaultValue) {
		String stringResult = System.getProperty(name);
		if (stringResult != null) {
			return Boolean.valueOf(stringResult);
		} else {
			return defaultValue;
		}
	}

	/**
	 * 读取String类型的系统变量，为空时返回null.
	 */
	public static String stringSystemProperty(String name) {
		return System.getProperty(name);
	}

	/**
	 * 读取String类型的系统变量，为空时返回默认值
	 */
	public static String stringSystemProperty(String name, String defaultValue) {
		return System.getProperty(name, defaultValue);
	}

	/**
	 * 读取Integer类型的系统变量，为空时返回null.
	 */
	public static Integer intSystemProperty(String name) {
		return Integer.getInteger(name);
	}

	/**
	 * 读取Integer类型的系统变量，为空时返回默认值
	 */
	public static Integer intSystemProperty(String name, Integer defaultValue) {
		return Integer.getInteger(name, defaultValue);
	}

	/**
	 * 读取Long类型的系统变量，为空时返回null.
	 */
	public static Long longSystemProperty(String name) {
		return Long.getLong(name);
	}

	/**
	 * 读取Integer类型的系统变量，为空时返回默认值
	 */
	public static Long longSystemProperty(String name, Long defaultValue) {
		return Long.getLong(name, defaultValue);
	}

	/**
	 * 读取Double类型的系统变量，为空时返回null.
	 */
	public static Double doubleSystemProperty(String propertyName) {
		return NumberUtil.toDoubleObject(System.getProperty(propertyName));
	}

	/**
	 * 读取Double类型的系统变量，为空时返回默认值.
	 */
	public static Double doubleSystemProperty(String propertyName, Double defaultValue) {
		Double propertyValue = NumberUtil.toDoubleObject(System.getProperty(propertyName));
		return propertyValue != null ? propertyValue : defaultValue;
	}

	/////////// 简单的合并系统变量(-D)，环境变量 和默认值，以系统变量优先.///////////////

	/**
	 * 合并系统变量(-D)，环境变量 和默认值，以系统变量优先
	 */
	public static String stringSystemProperty(String propertyName, String envName, String defaultValue) {
		checkEnvName(envName);
		String propertyValue = System.getProperty(propertyName);
		if (propertyValue != null) {
			return propertyValue;
		} else {
			propertyValue = System.getenv(envName);
			return propertyValue != null ? propertyValue : defaultValue;
		}
	}

	/**
	 * 合并系统变量(-D)，环境变量 和默认值，以系统变量优先
	 */
	public static Integer intSystemProperty(String propertyName, String envName, Integer defaultValue) {
		checkEnvName(envName);
		Integer propertyValue = NumberUtil.toIntObject(System.getProperty(propertyName));
		if (propertyValue != null) {
			return propertyValue;
		} else {
			propertyValue = NumberUtil.toIntObject(System.getenv(envName));
			return propertyValue != null ? propertyValue : defaultValue;
		}
	}

	/**
	 * 合并系统变量(-D)，环境变量 和默认值，以系统变量优先
	 */
	public static Long longSystemProperty(String propertyName, String envName, Long defaultValue) {
		checkEnvName(envName);
		Long propertyValue = NumberUtil.toLongObject(System.getProperty(propertyName));
		if (propertyValue != null) {
			return propertyValue;
		} else {
			propertyValue = NumberUtil.toLongObject(System.getenv(envName));
			return propertyValue != null ? propertyValue : defaultValue;
		}
	}

	/**
	 * 合并系统变量(-D)，环境变量 和默认值，以系统变量优先
	 */
	public static Double doubleSystemProperty(String propertyName, String envName, Double defaultValue) {
		checkEnvName(envName);
		Double propertyValue = NumberUtil.toDoubleObject(System.getProperty(propertyName));
		if (propertyValue != null) {
			return propertyValue;
		} else {
			propertyValue = NumberUtil.toDoubleObject(System.getenv(envName));
			return propertyValue != null ? propertyValue : defaultValue;
		}
	}

	/**
	 * 合并系统变量(-D)，环境变量 和默认值，以系统变量优先
	 */
	public static Boolean booleanSystemProperty(String propertyName, String envName, Boolean defaultValue) {
		checkEnvName(envName);
		Boolean propertyValue = toBooleanObject(System.getProperty(propertyName));
		if (propertyValue != null) {
			return propertyValue;
		} else {
			propertyValue = toBooleanObject(System.getenv(envName));
			return propertyValue != null ? propertyValue : defaultValue;
		}
	}

	private static Boolean toBooleanObject(String str) {
		if (str == null) {
			return null;
		} else {
			return Boolean.valueOf(str);
		}
	}

	/**
	 * 检查环境变量名不能有'.'，在linux下不支持
	 */
	private static void checkEnvName(String envName) {
		if (envName == null || envName.indexOf('.') != -1) {
			throw new IllegalArgumentException("envName " + envName + " has dot which is not valid");
		}
	}

	/////////// Load Properties /////////
	/**
	 * 从文件路径加载properties.
	 * 
	 * 路径支持从外部文件或resources文件加载, "file://"代表外部文件, "classpath://"代表resources,
	 */
	public static Properties loadFromFile(String generalPath) {
		Properties p = new Properties();
		InputStream is = null;
		try {
			is = GeneralResourceUtil.asStream(generalPath);
			p.load(is);
		} catch (IOException e) {
			logger.warn("Load property from " + generalPath + " fail ", e);
		} finally {
			IOUtil.closeQuietly(is);
		}
		return p;
	}

	/**
	 * 从字符串内容加载Properties
	 */
	public static Properties loadFromString(String content) {
		Properties p = new Properties();
		Reader reader = new StringReader(content);
		try {
			p.load(reader);
		} catch (IOException ignored) {
		} finally {
			IOUtil.closeQuietly(reader);
		}

		return p;
	}

	/////////// ListenableProperties /////////////
	/**
	 * Properties 本质上是一个HashTable，每次读写都会加锁，所以不支持频繁的System.getProperty(name)来检查系统内容变化 因此扩展了一个ListenableProperties,
	 * 在其所关心的属性变化时进行通知.
	 * 
	 * @see ListenableProperties
	 * @see PropertiesListener
	 */
	public static synchronized void registerSystemPropertiesListener(PropertiesListener listener) {
		Properties currentProperties = System.getProperties();

		if (!(currentProperties instanceof ListenableProperties)) {
			ListenableProperties newProperties = new ListenableProperties(currentProperties);
			System.setProperties(newProperties);
			currentProperties = newProperties;
		}

		((ListenableProperties) currentProperties).register(listener);
	}

	/**
	 * Properties 本质上是一个HashTable，每次读写都会加锁，所以不支持频繁的System.getProperty(name)来检查系统内容变化 因此扩展了一个ListenableProperties,
	 * 在其所关心的属性变化时进行通知.
	 * 
	 * @see PropertiesUtil#registerSystemPropertiesListener(PropertiesListener)
	 * @see PropertiesListener
	 */
	public static class ListenableProperties extends Properties {

		private static final long serialVersionUID = -8282465702074684324L;

		protected List<PropertiesListener> listeners = new CopyOnWriteArrayList<PropertiesListener>();

		public ListenableProperties(Properties properties) {
			super(properties);
		}

		public void register(PropertiesListener listener) {
			listeners.add(listener);
		}

		@Override
		public synchronized Object setProperty(String key, String value) {
			Object result = put(key, value);
			for (PropertiesListener listener : listeners) {
				if (listener.propertyName.equals(key)) {
					listener.onChange(key, value);
				}
			}
			return result;
		}
	}

	/**
	 * 获取所关心的Properties变更的Listener基类.
	 */
	public abstract static class PropertiesListener {

		protected String propertyName;

		public PropertiesListener(String propertyName) {
			this.propertyName = propertyName;
		}

		public abstract void onChange(String propertyName, String value);
	}

}
