package org.springside.examples.showcase.demos.hystrix.service;

import org.springframework.web.client.RestTemplate;
import org.springside.examples.showcase.webservice.rest.UserDTO;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class GetUserCommand extends HystrixCommand<UserDTO> {

	private Long id;

	private RestTemplate restTemplate = new RestTemplate();

	protected GetUserCommand(Long id) {

		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("GetUserCommand"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
				// 线程超时，默认为1秒,设为3秒，
						.withExecutionIsolationThreadTimeoutInMilliseconds(3000)
						// 至少多少请求在rolling window内发生，开始触发短路的计算，默认为20, 设为3
						.withCircuitBreakerRequestVolumeThreshold(3)
						// 多少百分比的fail在rolling windows内发生，计算为短路。默认为50%
						.withCircuitBreakerErrorThresholdPercentage(50)
						// 被置成短路后，多久才会重新尝试访问资源。默认为5秒，设为10秒
						.withCircuitBreakerSleepWindowInMilliseconds(10000)
						// rolling windows 长度，默认为10秒，设为20秒。
						.withMetricsRollingStatisticalWindowInMilliseconds(20000)));
		this.id = id;
	}

	@Override
	protected UserDTO run() throws Exception {
		return restTemplate.getForObject("http://localhost:8080/showcase/hystrix/resource/{id}", UserDTO.class, id);
	}
}