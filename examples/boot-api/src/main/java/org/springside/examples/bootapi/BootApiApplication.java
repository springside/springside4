package org.springside.examples.bootapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// SpringBoot 应用标识
@SpringBootApplication
public class BootApiApplication {

	/**
	 * 启动嵌入式的Tomcat并初始化Spring环境.
	 * 
	 * 无 applicationContext.xml 和 web.xml, 靠下述方式进行配置：
	 * 
	 * 1. 扫描当前package下的class设置自动注入的Bean<br/>
	 * 2. 也支持用@Bean标注的类配置Bean <br/>
	 * 3. 根据classpath中的三方包Class及集中的application.properties条件配置三方包，如线程池 <br/>
	 * 4. 也支持用@Configuration标注的类配置三方包.
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(BootApiApplication.class, args);
	}
}
