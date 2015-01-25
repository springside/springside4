/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package org.springside.examples.bootservice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexRestController {

	@Autowired
	private CounterService counterService;

	@RequestMapping(value = "/", produces = "text/html")
	public String index() {
		counterService.increment("web.index");
		return "<html><body>"
				+ "<p>Access below management endpoint:"
				+ "<ul>"
				+ "<li><a href=\"http://localhost:7002/health\">http://localhost:7002/health</a></li>"
				+ "<li><a href=\"http://localhost:7002/info\">http://localhost:7002/info</a></li>"
				+ "<li><a href=\"http://localhost:7002/dump\">http://localhost:7002/dump</a></li>"
				+ "<li><a href=\"http://localhost:7002/metrics\">http://localhost:7002/metrics</a></li>"
				+ "<li><a href=\"http://localhost:7002/env\">http://localhost:7002/env</a></li>"
				+ "<li>shutdown(disable by default, POST method)</li>"
				+ "</ul>"
				+ "<p>JMX expose as Restful by jolokia. e.g. Tomcat's MBean:"
				+ "<ul>"
				+ "<li><a href=\"http://localhost:7002/jolokia/read/Tomcat:type=Connector,port=8080\">http://localhost:7002/jolokia/read/Tomcat:type=Connector,port=8080</a></li>"
				+ "</ul></p>" + "</body></html>";
	}
}
