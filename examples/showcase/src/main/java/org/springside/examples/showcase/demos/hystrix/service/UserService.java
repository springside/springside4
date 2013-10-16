package org.springside.examples.showcase.demos.hystrix.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.showcase.webservice.rest.UserDTO;

import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * 使用Hystrix 封装的Service。原来直接调用restTemplate改为调用Hystrix Command的封装.
 * 
 * @author calvin
 */
@Service
public class UserService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	private boolean runInNewThread = true;

	private Setter commandConfig;
	private RestTemplate restTemplate;

	public UserDTO getUser(Long id) throws Exception {

		GetUserCommand command = new GetUserCommand(commandConfig, restTemplate, id);

		try {
			return command.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 演示Command的各种配置项, 并构造Thread-Safed的RestTemplate.
	 */
	@PostConstruct
	private void init() {
		// 初始化RestTemplate
		restTemplate = new RestTemplate();

		// 设置Command名称 //
		commandConfig = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")).andCommandKey(
				HystrixCommandKey.Factory.asKey("GetUserCommand"));

		HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
		commandConfig.andCommandPropertiesDefaults(commandProperties);

		// 设置短路计算规则 //
		commandProperties
				// 多少百分比的失败在rolling windows内发生，计算为短路。默认为50%，无改变。
				.withCircuitBreakerErrorThresholdPercentage(50)
				// 至少多少请求在rolling window内发生，才开始触发短路的计算，默认为20, 设为3方便演示。
				.withCircuitBreakerRequestVolumeThreshold(3)
				// rolling windows 长度，默认为20秒，改为180秒方便演示。同时相应改变桶的数量。
				.withMetricsRollingStatisticalWindowInMilliseconds(180000)
				.withMetricsRollingStatisticalWindowBuckets(180);

		// 设置短路后的保护时间 ，默认为5秒，改为20秒方便演示//
		commandProperties.withCircuitBreakerSleepWindowInMilliseconds(20000);

		// 设置超时与并发控制 //
		if (runInNewThread) {
			// 线程超时，默认为1秒，设为3秒方便演示
			commandProperties.withExecutionIsolationThreadTimeoutInMilliseconds(3000);
			// 线程池属性， 线程池大小，默认为10，无改变。待执行队列的大小，默认为5，无改变。
			commandConfig.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(10)
					.withQueueSizeRejectionThreshold(5));
		} else {
			// RestTemplate设置为10秒超时
			((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(10000);

			// 设置使用信号量模式而不是默认的线程池模式，设置并发，默认为10，无改变
			commandProperties.withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE)
					.withExecutionIsolationSemaphoreMaxConcurrentRequests(10);
		}
	}

	public void setRunInNewThread(Boolean runInNewThread) {
		this.runInNewThread = runInNewThread;
	}
}
