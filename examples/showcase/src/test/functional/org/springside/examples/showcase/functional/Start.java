package org.springside.examples.showcase.functional;

import org.eclipse.jetty.server.Server;
import org.springside.modules.test.functional.JettyFactory;

/**
 * 使用Jetty运行调试Web应用, 在Console输入回车停止服务器.
 * 
 * @author calvin
 */
public class Start {

	public static final int PORT = 8080;
	public static final String CONTEXT = "/showcase";
	public static final String BASE_URL = "http://localhost:8080/showcase";

	public static void main(String[] args) throws Exception {
		Server server = JettyFactory.createServer(PORT, CONTEXT);
		server.start();

		System.out.println("Server running at " + BASE_URL);
		System.out.println("Hit Enter in console to stop server");

		//wait for close
		System.in.read();
		server.stop();
		server.join();
		System.out.println("Server stopped");
	}
}
