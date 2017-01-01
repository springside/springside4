package org.springside.jmh;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

/**
 * 对比测试两种HashMap与EnumMap法的性能
 * 
 * 结果相差一倍
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@State(Scope.Benchmark)
public class EnumMapTest {

	public enum TestEnum {
		ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN;
	}

	private EnumMap<TestEnum, Integer> enumMap;
	private HashMap<TestEnum, Integer> hashMap;

	@Setup(Level.Trial)
	public void setup() {

		hashMap = new HashMap<TestEnum, Integer>();
		initMap(hashMap);

		enumMap = new EnumMap<TestEnum, Integer>(TestEnum.class);
		initMap(enumMap);
	}

	private void initMap(Map<TestEnum, Integer> map) {
		map.put(TestEnum.ONE, new Integer(1));
		map.put(TestEnum.TWO, new Integer(2));
		map.put(TestEnum.FOUR, new Integer(4));
		map.put(TestEnum.SIX, new Integer(6));
		map.put(TestEnum.SEVEN, new Integer(7));
	}

	private int caculateMap(Map<TestEnum, Integer> map) {
		int result = 0;
		result += map.get(TestEnum.ONE);
		result += map.get(TestEnum.TWO);
		result += map.get(TestEnum.FOUR);
		result += map.get(TestEnum.SIX);
		return result;
	}

	@Benchmark
	@Fork(value = 1)
	public Integer HashMap() {
		return caculateMap(hashMap);
	}

	@Benchmark
	@Fork(value = 1)
	public Integer enumMap() {
		return caculateMap(enumMap);
	}
}
