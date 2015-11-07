package org.springside.examples.bootapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// SpringBoot 应用标识, 继承于@Configuration, @ComponentScan, @@EnableAutoConfiguration三大标识
@SpringBootApplication
public class BootApiApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(BootApiApplication.class, args);
	}
}
