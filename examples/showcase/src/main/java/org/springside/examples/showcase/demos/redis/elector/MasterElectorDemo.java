/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.redis.elector;

import org.springside.modules.nosql.redis.pool.JedisPool;
import org.springside.modules.nosql.redis.pool.JedisPoolBuilder;
import org.springside.modules.nosql.redis.service.elector.MasterElector;

public class MasterElectorDemo {

	public static void main(String[] args) throws Exception {

		JedisPool pool = new JedisPoolBuilder().setUrl("direct://localhost:6379").setPoolSize(1).buildPool();
		try {
			MasterElector masterElector = new MasterElector(pool, 5);

			masterElector.start();

			System.out.println("Hit enter to stop.");
			while (true) {
				char c = (char) System.in.read();
				if (c == '\n') {
					System.out.println("Shuting down");
					masterElector.stop();
					return;
				}
			}
		} finally {
			pool.destroy();
		}
	}

}
