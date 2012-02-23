/**
 * Copyright (c) 2005-2011 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 
 * $Id: JerseyClientFactory.java 1656 2011-11-17 00:20:42Z calvinxiu $
 */
package org.springside.modules.utils.jersey;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class JerseyClientFactory {

	private JerseyClientFactory() {
	}

	/**
	 * 创建JerseyClient, 设定JSON字符串使用Jackson解析.
	 */
	public static WebResource createClient(String baseUrl) {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		Client client = Client.create(clientConfig);
		return client.resource(baseUrl);
	}
}
