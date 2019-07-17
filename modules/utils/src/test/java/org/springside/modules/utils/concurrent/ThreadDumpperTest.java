package org.springside.modules.utils.concurrent;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springside.modules.test.log.LogbackListAppender;
import org.springside.modules.utils.concurrent.threadpool.ThreadPoolBuilder;

public class ThreadDumpperTest {
	
	public static class LongRunTask implements Runnable {
		
		private CountDownLatch countDownLatch;
		public LongRunTask(CountDownLatch countDownLatch) {
			this.countDownLatch = countDownLatch;
		}
		@Override
		public void run() {
			countDownLatch.countDown();
			ThreadUtil.sleep(5, TimeUnit.SECONDS);
		}
	}

	@Test
	public void test() throws InterruptedException {
		ExecutorService executor = ThreadPoolBuilder.fixedPool().setPoolSize(10).build();
		CountDownLatch countDownLatch= Concurrents.countDownLatch(10);
		for(int i=0;i<10;i++){
			executor.execute(new LongRunTask(countDownLatch));
		}
		countDownLatch.await();
		
		ThreadDumpper dumpper = new ThreadDumpper();
		dumpper.threadDumpIfNeed();

		LogbackListAppender appender = new LogbackListAppender();
		appender.addToLogger(ThreadDumpper.class);

		// disable,不输出
		dumpper.setEnable(false);
		dumpper.threadDumpIfNeed();
		assertThat(appender.getAllLogs()).hasSize(0);

		// 设置最少间隔,不输出
		dumpper.setEnable(true);
		dumpper.setLeastInterval(1800);
		dumpper.threadDumpIfNeed();
		assertThat(appender.getAllLogs()).hasSize(0);
		
		executor.shutdownNow();

	}

}
