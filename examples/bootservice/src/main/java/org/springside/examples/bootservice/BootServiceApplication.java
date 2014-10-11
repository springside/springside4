package org.springside.examples.bootservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

// Spring Java Config的标识
@Configuration
@ComponentScan("org.springside.examples.bootservice")
// Spring Boot的AutoConfig和载入外部properties文件的 标识
@EnableAutoConfiguration
@EnableConfigurationProperties
public class BootServiceApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(BootServiceApplication.class, args);
	}
}
