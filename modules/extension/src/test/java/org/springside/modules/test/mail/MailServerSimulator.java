/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.test.mail;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

/**
 * 基于GreenMail的MailServer模拟器，用于开发/测试环境。
 * 默认在localhost的3025端口启动SMTP服务, 用户名密码是greenmail@localhost.com/greemail, 均可设置.
 * FactoryBean已将greenMail对象注入到Context中，可在测试中取用.
 * 
 * @author calvin
 */
public class MailServerSimulator implements InitializingBean, DisposableBean, FactoryBean<GreenMail> {

	public static final String DEFAULT_ACCOUNT = "greenmail@localhost.com";
	public static final String DEFAULT_PASSWORD = "greenmail";
	public static final int DEFAULT_PORT = 3025;

	private GreenMail greenMail;

	private String account = DEFAULT_ACCOUNT;

	private String password = DEFAULT_PASSWORD;

	private int port = DEFAULT_PORT;

	@Override
	public void afterPropertiesSet() throws Exception {
		greenMail = new GreenMail(new ServerSetup(port, null, ServerSetup.PROTOCOL_SMTP));
		greenMail.setUser(account, password);
		greenMail.start();
	}

	@Override
	public void destroy() throws Exception {
		if (greenMail != null) {
			greenMail.stop();
		}
	}

	@Override
	public GreenMail getObject() throws Exception {
		return greenMail;
	}

	@Override
	public Class<?> getObjectType() {
		return GreenMail.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
