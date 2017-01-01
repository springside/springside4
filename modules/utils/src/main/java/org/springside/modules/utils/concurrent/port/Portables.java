package org.springside.modules.utils.concurrent.port;

import java.util.Random;

import org.springside.modules.utils.base.Platforms;

/**
 * ThreadLocalRandom 与 LongAdder进行跨JDK版本的支持
 * 
 * @author calvin
 */
public class Portables {
	
	public static Random threadLocalRandom(){
		if(Platforms.IS_ATLEASET_JAVA7){
			return java.util.concurrent.ThreadLocalRandom.current();
		}else{
			return org.springside.modules.utils.concurrent.port.ThreadLocalRandom.current();
		}
	}
}
