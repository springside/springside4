/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.utils.ee;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

/**
 * 使用Velocity渲染模板内容的工具类.
 * 
 * @author calvin
 */
public class Velocitys {

	static {
		Velocity.init();
	}

	private Velocitys() {
	}

	/**
	 * 渲染模板内容.
	 * 
	 * @param templateContent 模板内容.
	 * @param context 变量Map.
	 */
	public static String renderTemplateContent(String templateContent, Map<String, ?> context) {
		VelocityContext velocityContext = new VelocityContext(context);

		StringWriter result = new StringWriter();
		Velocity.evaluate(velocityContext, result, "", templateContent);
		return result.toString();
	}

	/**
	 * 渲染模板文件.
	 * 
	 * @param velocityEngine velocityEngine, 需经过VelocityEngineFactory处理, 绑定Spring的ResourceLoader.
	 * @param templateContent 模板文件名, loader会自动在前面加上velocityEngine的resourceLoaderPath.
	 * @param context 变量Map.
	 */
	public static String renderFile(String templateFilePName, VelocityEngine velocityEngine, String encoding,
			Map<String, ?> context) {
		VelocityContext velocityContext = new VelocityContext(context);

		StringWriter result = new StringWriter();
		velocityEngine.mergeTemplate(templateFilePName, encoding, velocityContext, result);
		return result.toString();
	}
}
