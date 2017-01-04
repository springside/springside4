package org.springside.modules.utils.base;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 关于Properties的工具类
 * 
 * 1. Boolean.readBoolean(name) 不够用, 进行扩展. 其他Integer.read()等都没问题.
 * 
 * 2. 简单的合并系统变量(-D)，环境变量 和默认值，以系统变量优先，在未引入Commons Config时使用.
 * 
 * 3. Properties 本质上是一个HashTable，每次读写都会加锁，所以不支持频繁的System.getProperty(name)来检查系统内容变化 因此扩展了一个ListenableProperties,
 * 在其所关心的属性变化时进行通知.
 * 
 * @author calvin
 */
public class PropertiesUtil {

	/////////// Boolean.readBoolean()扩展 ///////////////

	/**
	 * 读取Boolean类型的系统变量，为空时返回null，而不是false.
	 */
	public static Boolean readBoolean(String name) {
		String stringResult = System.getProperty(name);
		if (stringResult != null) {
			return Boolean.valueOf(stringResult);
		}
		return null; // NOSONAR
	}

	/**
	 * 读取Boolean类型的系统变量，为空时返回默认值，因为默认值为Boolean，返回的也是Boolean
	 */
	public static Boolean readBoolean(String name, Boolean defaultVal) {
		String stringResult = System.getProperty(name);
		if (stringResult != null) {
			return Boolean.parseBoolean(stringResult);
		} else {
			return defaultVal;
		}
	}

	/**
	 * 读取Boolean类型的系统变量，为空时返回默认值。因为默认值为Boolean，返回的也是boolean
	 */
	public static boolean readBoolean(String name, boolean defaultVal) {
		String stringResult = System.getProperty(name);
		if (stringResult != null) {
			return Boolean.parseBoolean(stringResult);
		} else {
			return defaultVal;
		}
	}

	/////////// 简单的合并系统变量(-D)，环境变量 和默认值，以系统变量优先.///////////////

	/**
	 * 合并系统变量(-D)，环境变量 和默认值，以系统变量优先
	 */
	public static String readString(String propertyName, String envName, String defaultValue) {
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
	public static Integer readInt(String propertyName, String envName, Integer defaultValue) {
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
	public static Long readLong(String propertyName, String envName, Long defaultValue) {
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
	public static Double readDouble(String propertyName, String envName, Double defaultValue) {
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
	public static Boolean readBoolean(String propertyName, String envName, Boolean defaultValue) {
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

	/**
	 * Properties 本质上是一个HashTable，每次读写都会加锁，所以不支持频繁的System.getProperty(name)来检查系统内容变化
	 * 因此扩展了一个ListenableProperties, 在其所关心的属性变化时进行通知.
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
	 * Properties 本质上是一个HashTable，每次读写都会加锁，所以不支持频繁的System.getProperty(name)来检查系统内容变化
	 * 因此扩展了一个ListenableProperties, 在其所关心的属性变化时进行通知.
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
	public abstract class PropertiesListener {

		protected String propertyName;

		public PropertiesListener(String propertyName) {
			this.propertyName = propertyName;
		}

		public abstract void onChange(String propertyName, String value);
	}

}
