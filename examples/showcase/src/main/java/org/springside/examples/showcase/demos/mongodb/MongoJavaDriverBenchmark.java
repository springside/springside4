package org.springside.examples.showcase.demos.mongodb;

import java.net.UnknownHostException;
import java.util.Date;

import org.springside.modules.test.benchmark.BenchmarkBase;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;

public class MongoJavaDriverBenchmark extends BenchmarkBase {
	private static int THREAD_COUNT = 20;
	private static int LOOP_COUNT = 10000;

	private final String host = "localhost";
	private final String dbName = "calvin";
	private final String collectionName = "counter";
	private final String counterName = "springside.counter";

	public static void main(String[] args) throws Exception {
		MongoJavaDriverBenchmark benchmark = new MongoJavaDriverBenchmark(THREAD_COUNT, LOOP_COUNT);
		benchmark.run();
	}

	public MongoJavaDriverBenchmark(int threadCount, int loopCount) {
		super(threadCount, loopCount);
	}

	@Override
	protected void onPrepare() {
		Mongo mongoClient = null;
		try {
			mongoClient = new Mongo(host);
			mongoClient.setWriteConcern(WriteConcern.SAFE);
			DB db = mongoClient.getDB(dbName);
			DBCollection coll = db.getCollection(collectionName);

			coll.drop();

			BasicDBObject doc = new BasicDBObject("name", counterName).append("count", 0);
			coll.insert(doc);

			mongoClient.close();
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
				Mongo mongoClient = new Mongo(host);

				DB db = mongoClient.getDB(dbName);
				db.setWriteConcern(WriteConcern.SAFE);
				DBCollection coll = db.getCollection(collectionName);

				Date startTime = onThreadStart();

				for (int i = 0; i < loopCount; i++) {
					BasicDBObject set = new BasicDBObject("$inc", new BasicDBObject("count", 1));
					BasicDBObject query = new BasicDBObject("name", counterName);
					coll.update(query, set);
				}

				onThreadFinish(startTime);
				mongoClient.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}
}
