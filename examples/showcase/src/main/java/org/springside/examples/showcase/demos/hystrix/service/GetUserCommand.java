package org.springside.examples.showcase.demos.hystrix.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
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

	protected GetUserCommand(Long id, boolean runInNewThread) {
		// 配置Command
		super(configCommand(runInNewThread));
		// 传入请求参数
		this.id = id;
	}

	/**
	 * 实际访问依赖资源的函数的实现。
	 */
	@Override
	protected UserDTO run() throws Exception {
		logger.info("Access restful resource");
		return restTemplate.getForObject("http://localhost:8080/showcase/hystrix/resource/{id}", UserDTO.class, id);
	}

	/**
	 * 演示Command的各种配置项.
	 */
	protected static Setter configCommand(boolean runInNewThread) {
		// 设置名称 //
		Setter config = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")).andCommandKey(
				HystrixCommandKey.Factory.asKey("GetUserCommand"));

		// 设置短路计算规则 //
		config.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
		// 至少多少请求在rolling window内发生，开始触发短路的计算，默认为20, 设为3方便演示。
				.withCircuitBreakerRequestVolumeThreshold(3)
				// 多少百分比的fail在rolling windows内发生，计算为短路。默认为50%，无改变。
				.withCircuitBreakerErrorThresholdPercentage(50)
				// rolling windows 长度，默认为20秒，改为60秒方便演示。
				.withMetricsRollingStatisticalWindowInMilliseconds(60000)
				// rolling windows里桶的数量，默认为为20，因为rolling window增大，需相应增加让短路计算保持秒的精度。
				.withMetricsRollingStatisticalWindowBuckets(60)
				// 计算短路的时间间隔，默认是500毫秒，无改变。
				.withMetricsHealthSnapshotIntervalInMilliseconds(500));

		// 设置短路后的保护时间 //
		config.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
				.withCircuitBreakerSleepWindowInMilliseconds(20000));

		// 使用Hystrix线程池的异步执行方式
		if (runInNewThread) {
			config.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
			// 线程超时，默认为1秒，设为2秒方便演示
					.withExecutionIsolationThreadTimeoutInMilliseconds(2000))
			// 线程池属性， 线程池大小，默认为10，无改变。待执行队列的大小，默认为5，无改变。
					.andThreadPoolPropertiesDefaults(
							HystrixThreadPoolProperties.Setter().withCoreSize(10).withQueueSizeRejectionThreshold(5));
		} else {
			// 使用Invoker原有线程的方式

		}

		return config;
	}

}
