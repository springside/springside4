package org.springside.examples.showcase.demos.hystrix.service;

import org.springframework.web.client.RestTemplate;
import org.springside.examples.showcase.webservice.rest.UserDTO;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class GetUserCommand extends HystrixCommand<UserDTO> {

	private Long id;

	private RestTemplate restTemplate = new RestTemplate();

	protected GetUserCommand(Long id) {
		super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
		this.id = id;
	}

	@Override
	protected UserDTO run() throws Exception {
		return restTemplate.getForObject("http://localhost:8080/showcase/hystrix/resource/{id}", UserDTO.class, id);
	}
}