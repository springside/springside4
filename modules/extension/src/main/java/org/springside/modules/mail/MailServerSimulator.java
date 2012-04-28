package org.springside.modules.mail;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

/**
 * 基于GreenMail的MailServer模拟器，用于开发/测试环境。
 * 
 * @author calvin
 */
public class MailServerSimulator implements InitializingBean, DisposableBean, FactoryBean<GreenMail> {

	public static final String DEFAULT_MAIL = "greenmail@localhost";
	public static final String DEFAULT_PASSWORD = "greenmail";

	private GreenMail greenMail;

	private String mail = DEFAULT_MAIL;

	private String password = DEFAULT_PASSWORD;

	@Override
	public void afterPropertiesSet() throws Exception {
		greenMail = new GreenMail(ServerSetup.SMTP);
		greenMail.setUser(mail, password);
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

	public void setMail(String mail) {
		this.mail = mail;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
