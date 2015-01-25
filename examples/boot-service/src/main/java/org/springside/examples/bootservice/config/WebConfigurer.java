package org.springside.examples.bootservice.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class WebConfigurer implements ServletContextInitializer {

	@Autowired
	private Environment env;

	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		if (!env.acceptsProfiles(Profiles.PRODUCTION)) {
			initH2Console(servletContext);
		}
	}

	private void initH2Console(ServletContext servletContext) {
		ServletRegistration.Dynamic h2ConsoleServlet = servletContext
				.addServlet("H2Console", new org.h2.server.web.WebServlet());
		h2ConsoleServlet.addMapping("/h2/*");
		h2ConsoleServlet.setInitParameter("-properties", "src/main/resources");
		h2ConsoleServlet.setLoadOnStartup(1);
	}
}
