package org.springside.examples.showcase.demos.hystrix.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.showcase.webservice.rest.UserDTO;

import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class UserService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	private Boolean runInNewThread = true;
	private Setter config;
	private RestTemplate restTemplate = new RestTemplate();

	public UserDTO getUser(Long id) throws Exception {

		GetUserCommand command = new GetUserCommand(config, restTemplate, id);

		try {
			return command.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 演示Command的各种配置项,构造RestTemplate.
	 */
	@PostConstruct
	private void init() {
		// 初始化RestTemplate
		restTemplate = new RestTemplate();

		// 设置Command名称 //
		config = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")).andCommandKey(
				HystrixCommandKey.Factory.asKey("GetUserCommand"));

		// 设置短路计算规则 //
		config.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
				// 多少百分比的失败在rolling windows内发生，计算为短路。默认为50%，无改变。
				.withCircuitBreakerErrorThresholdPercentage(50)
				// 至少多少请求在rolling window内发生，才开始触发短路的计算，默认为20, 设为3方便演示。
				.withCircuitBreakerRequestVolumeThreshold(3)
				// rolling windows 长度，默认为20秒，改为60秒方便演示。同时相应改变桶的数量。
				.withMetricsRollingStatisticalWindowInMilliseconds(60000)
				.withMetricsRollingStatisticalWindowBuckets(60));

		// 设置短路后的保护时间 //
		config.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
				.withCircuitBreakerSleepWindowInMilliseconds(20000));

		// 设置超时与并发控制 //
		if (runInNewThread) {
			// 使用Hystrix线程池的异步执行方式
			config.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
			// 线程超时，默认为1秒，设为2秒方便演示
					.withExecutionIsolationThreadTimeoutInMilliseconds(2000))
			// 线程池属性， 线程池大小，默认为10，无改变。待执行队列的大小，默认为5，无改变。
					.andThreadPoolPropertiesDefaults(
							HystrixThreadPoolProperties.Setter().withCoreSize(10).withQueueSizeRejectionThreshold(5));
		} else {
			// 使用Invoker原有线程的方式， 设置使用信号量模式而不是默认的线程池模式，设置并发，默认为10，无改变
			config.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
					.withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE)
					.withExecutionIsolationSemaphoreMaxConcurrentRequests(10));

			// RestTemplate设置为10秒超时
			((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(10000);

		}
	}

	public void setRunInNewThread(Boolean runInNewThread) {
		this.runInNewThread = runInNewThread;
	}
}
