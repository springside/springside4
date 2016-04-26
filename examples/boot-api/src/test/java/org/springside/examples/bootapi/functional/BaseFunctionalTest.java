package org.springside.examples.bootapi.functional;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springside.examples.bootapi.BootApiApplication;
import org.springside.modules.test.rule.TestProgress;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BootApiApplication.class)
// 定义为Web集成测试，并使用随机端口号
@WebIntegrationTest("server.port=0")
// 定义每执行完一个Test文件刷新一次Spring Application Context，避免Case间的数据影响.
// 但Test文件内多个测试方法间的影响仍需注意
@DirtiesContext
public abstract class BaseFunctionalTest {

	// 注入启动server后的实际端口号
	@Value("${local.server.port}")
	protected int port;

	// 在Console里打印Case的开始与结束，更容易分清Console里的日志归属于哪个Case.
	@Rule
	public TestRule testProgress = new TestProgress();

}
