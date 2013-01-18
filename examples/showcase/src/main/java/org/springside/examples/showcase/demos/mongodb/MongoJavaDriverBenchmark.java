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
	private static final int THREAD_COUNT = 20;
	private static final int LOOP_COUNT = 10000;
	private static final String HOST = "localhost";

	private final String dbName = "springside";
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
	protected void onStart() {
		Mongo mongoClient = null;
		try {
			mongoClient = new Mongo(HOST);
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
				Mongo mongoClient = new Mongo(HOST);

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
