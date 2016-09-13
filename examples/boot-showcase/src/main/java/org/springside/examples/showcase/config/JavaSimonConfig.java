package org.springside.examples.showcase.config;

import org.javasimon.console.SimonConsoleServlet;
import org.javasimon.spring.MonitoredMeasuringPointcut;
import org.javasimon.spring.MonitoringInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 演示配置能力，包括AOP配置与Servlet配置
 */
@Configuration
public class JavaSimonConfig {

	// 定义AOP, 对标注了@Monitored的方法进行监控
	@Bean(name = "monitoringAdvisor")
	public DefaultPointcutAdvisor monitoringAdvisor() {
		DefaultPointcutAdvisor monitoringAdvisor = new DefaultPointcutAdvisor();
		monitoringAdvisor.setAdvice(new MonitoringInterceptor());
		monitoringAdvisor.setPointcut(new MonitoredMeasuringPointcut());
		return monitoringAdvisor;
	}

	// 定义Servlet URL Mapping
	@Bean
	public ServletRegistrationBean dispatcherRegistration() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new SimonConsoleServlet());
		registration.addInitParameter("url-prefix", "/javasimon");
		registration.addUrlMappings("/javasimon/*");
		return registration;
	}

}
