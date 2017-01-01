package org.springside.modules.utils.concurrent.jsr166e;

import java.util.Random;

/**
 * ThreadLocalRandom 与 LongAdder进行跨JDK版本的支持
 * 
 * @author calvin
 */
public class Portables {
	
	public static Random threadLocalRandom(){
		return ThreadLocalRandom.current();
	}
	
	public static LongAdder longAdder(){
		return new LongAdder();
	}
}
