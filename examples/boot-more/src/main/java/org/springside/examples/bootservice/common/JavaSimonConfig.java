package org.springside.examples.bootservice.common;

import org.javasimon.console.SimonConsoleServlet;
import org.javasimon.spring.MonitoredMeasuringPointcut;
import org.javasimon.spring.MonitoringInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JavaSimonConfig {

	// 定义AOP
	@Bean(name = "monitoringAdvisor")
	public DefaultPointcutAdvisor monitoringAdvisor() {
		DefaultPointcutAdvisor monitoringAdvisor = new DefaultPointcutAdvisor();
		monitoringAdvisor.setAdvice(new MonitoringInterceptor());
		monitoringAdvisor.setPointcut(new MonitoredMeasuringPointcut());
		return monitoringAdvisor;
	}

	// 定义URL Mapping
	@Bean
	public ServletRegistrationBean dispatcherRegistration() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new SimonConsoleServlet());
		registration.addInitParameter("url-prefix", "/javasimon");
		registration.addUrlMappings("/javasimon/*");
		return registration;
	}

}
