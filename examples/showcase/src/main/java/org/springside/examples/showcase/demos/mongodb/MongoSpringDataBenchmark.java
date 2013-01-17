package org.springside.examples.showcase.demos.mongodb;

import java.net.UnknownHostException;
import java.util.Date;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springside.modules.test.benchmark.BenchmarkBase;

import com.mongodb.Mongo;
import com.mongodb.WriteConcern;

public class MongoSpringDataBenchmark extends BenchmarkBase {
	private static int THREAD_COUNT = 20;
	private static int LOOP_COUNT = 10000;

	private final String host = "localhost";
	private final String dbName = "calvin";
	private final String collectionName = "counter";
	private final String counterName = "springside.counter";

	public static void main(String[] args) throws Exception {
		MongoSpringDataBenchmark benchmark = new MongoSpringDataBenchmark(THREAD_COUNT, LOOP_COUNT);
		benchmark.run();
	}

	public MongoSpringDataBenchmark(int threadCount, int loopCount) {
		super(threadCount, loopCount);
	}

	@Override
	protected void onStart() {
		try {
			Mongo client = new Mongo(host);
			client.setWriteConcern(WriteConcern.SAFE);
			MongoOperations mongoOps = new MongoTemplate(client, dbName);

			mongoOps.dropCollection(collectionName);

			Counter counter = new Counter(counterName, 0);
			mongoOps.insert(counter, collectionName);

			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Runnable getTask() {
		return new IncTask();
	}

	public class IncTask implements Runnable {

		@Override
		public void run() {
			try {
				Mongo client = new Mongo(host);
				client.setWriteConcern(WriteConcern.SAFE);
				MongoOperations mongoOps = new MongoTemplate(client, dbName);

				Date startTime = onThreadStart();

				for (int i = 0; i < loopCount; i++) {
					mongoOps.updateFirst(new Query(Criteria.where("name").is(counterName)),
							new Update().inc("count", 1), collectionName);
				}

				onThreadFinish(startTime);
				client.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}

	public static class Counter {
		private final String name;
		private final int count;

		public Counter(String name, int count) {
			this.name = name;
			this.count = count;
		}

		public String getName() {
			return name;
		}

		public int getCount() {
			return count;
		}

		@Override
		public String toString() {
			return "Counter [name=" + name + ", count=" + count + "]";
		}
	}
}
