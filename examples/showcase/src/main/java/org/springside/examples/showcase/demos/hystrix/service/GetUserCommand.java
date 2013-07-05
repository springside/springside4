package org.springside.examples.showcase.demos.hystrix.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.showcase.demos.hystrix.web.HystrixController;
import org.springside.examples.showcase.webservice.rest.UserDTO;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class GetUserCommand extends HystrixCommand<UserDTO> {

	private static Logger logger = LoggerFactory.getLogger(GetUserCommand.class);

	private Long id;

	private RestTemplate restTemplate = new RestTemplate();

	protected GetUserCommand(Long id) {

		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("GetUserCommand"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
				// 线程超时，默认为1秒，设为2秒方便演示
						.withExecutionIsolationThreadTimeoutInMilliseconds(2000)
						// 至少多少请求在rolling window内发生，开始触发短路的计算，默认为20, 设为3方便演示。
						.withCircuitBreakerRequestVolumeThreshold(3)
						// 多少百分比的fail在rolling windows内发生，计算为短路。默认为50%，无改变。
						.withCircuitBreakerErrorThresholdPercentage(50)
						// 被置成短路后，多久才会重新尝试访问资源。默认为5秒，改为20秒方便演示。
						.withCircuitBreakerSleepWindowInMilliseconds(20000)
						// rolling windows 长度，默认为20秒，改为60秒方便演示。
						.withMetricsRollingStatisticalWindowInMilliseconds(60000)
						// rolling Windows的桶的数量，默认为10，无为20，因为rolling window增大，需相应增加以保持秒的精度。
						.withMetricsRollingStatisticalWindowBuckets(60)
						// 计算短路的时间间隔，默认是500毫秒，无改变。
						.withMetricsHealthSnapshotIntervalInMilliseconds(500))
				// 线程池属性
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
				// 线程池大小，默认为10，无改变。
						.withCoreSize(10)
						// 待执行队列的大小，默认为5，无改变。
						.withQueueSizeRejectionThreshold(5)));
		this.id = id;
	}

	@Override
	protected UserDTO run() throws Exception {
		logger.info("Access restful resource");
		return restTemplate.getForObject("http://localhost:8080/showcase/hystrix/resource/{id}", UserDTO.class, id);
	}

	@Override
	protected UserDTO getFallback() {
		if ("exception".equals(HystrixController.fallback)) {
			throw new RuntimeException("HystrixException");
		}

		if ("dummy".equals(HystrixController.fallback)) {
			UserDTO dummyUser = new UserDTO();
			dummyUser.setId(0L);
			dummyUser.setLoginName("dummy");
			dummyUser.setName("Dummy");
			return dummyUser;
		}

		if ("standby".equals(HystrixController.fallback)) {
			return restTemplate.getForObject("http://localhost:8080/showcase/hystrix/resource/standby/{id}",
					UserDTO.class, id);
		}

		return null;
	}
}