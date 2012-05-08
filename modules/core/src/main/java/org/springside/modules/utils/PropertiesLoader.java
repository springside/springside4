/**
 * Copyright (c) 2005-2011 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 
 * $Id: PropertiesLoader.java 1690 2012-02-22 13:42:00Z calvinxiu $
 */
package org.springside.modules.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Properties文件载入工具类. 可载入多个properties文件, 相同的属性在最后载入的文件中的值将会覆盖之前的值.
 * 
 * 本类有两种使用方法:
 * 1. 
 * 
 * @author calvin
 */
public class PropertiesLoader {

	private static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

	private static ResourceLoader resourceLoader = new DefaultResourceLoader();

	private Properties properties;

	public PropertiesLoader(String... resourcesPaths) {
		properties = loadProperties(resourcesPaths);
	}

	public Properties getProperties() {
		return properties;
	}

	/**
	 * 取出Property，但以System的Property优先.
	 */
	public String getProperty(String key) {
		String result = System.getProperty(key);
		if (result != null) {
			return result;
		}
		return properties.getProperty(key);
	}

	/**
	 * 取出Property，但以System的Property优先.如果都為Null則返回Default值.
	 */
	public String getProperty(String key, String defaultValue) {
		String result = getProperty(key);
		return StringUtils.defaultString(result, defaultValue);
	}

	/**
	 * 取出Integer类型的Property，但以System的Property优先.如果都為Null或内容错误則返回0.
	 */
	public Integer getInteger(String key) {
		String strResult = getProperty(key);
		return NumberUtils.toInt(strResult);
	}

	/**
	 * 取出Integer类型的Property，但以System的Property优先.如果都為Null或内容错误則返回Default值.
	 */
	public Integer getInteger(String key, Integer defaultValue) {
		String strResult = getProperty(key);
		return NumberUtils.toInt(strResult, defaultValue);
	}

	/**
	 * 取出Boolean类型的Property，但以System的Property优先.如果都為Null或内容不为true/false則返回false.
	 */
	public Boolean getBoolean(String key) {
		return Boolean.valueOf(getProperty(key));
	}

	/**
	 * 取出Boolean类型的Property，但以System的Property优先.如果都為Null則返回Default值,如果内容不为true/false则返回false.
	 */
	public Boolean getBoolean(String key, boolean defaultValue) {
		String strResult = getProperty(key);
		if (strResult != null) {
			return Boolean.valueOf(strResult);
		} else {
			return defaultValue;
		}
	}

	/**
	 * 载入多个文件, 文件路径使用Spring Resource格式.
	 */
	private Properties loadProperties(String... resourcesPaths) {
		Properties props = new Properties();

		for (String location : resourcesPaths) {

			logger.debug("Loading properties file from:" + location);

			InputStream is = null;
			try {
				Resource resource = resourceLoader.getResource(location);
				is = resource.getInputStream();
				props.load(is);
			} catch (IOException ex) {
				logger.info("Could not load properties from path:" + location + ", " + ex.getMessage());
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
		return props;
	}
}
