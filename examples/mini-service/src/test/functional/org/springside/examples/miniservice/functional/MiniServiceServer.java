package org.springside.examples.miniservice.functional;

import org.eclipse.jetty.server.Server;
import org.springside.modules.test.functional.JettyFactory;

/**
 * 使用Jetty运行调试Web应用,在Console输入回车停止服务器.
 * 
 * @author calvin
 */
public class MiniServiceServer {

	public static final int PORT = 8080;
	public static final String CONTEXT = "/mini-service";
	public static final String BASE_URL = "http://localhost:8080/mini-service";

	public static void main(String[] args) throws Exception {

		//设定Spring的profile
		System.setProperty("spring.profiles.active", "development");

		//启动Jetty
		Server server = JettyFactory.createServerInSource(PORT, CONTEXT);
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
