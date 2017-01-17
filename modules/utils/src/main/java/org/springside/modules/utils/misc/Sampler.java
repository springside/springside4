package org.springside.modules.utils.misc;

import org.apache.commons.lang3.Validate;
import org.springside.modules.utils.number.RandomUtil;

/**
 * 采样器
 * 
 * from Twitter Common, 优化使用ThreadLocalRandom
 * 
 * @author calvin
 */
public class Sampler {

	private double threshold;

	/**
	 * 优化的创建函数，如果为0或100时，返回更直接的采样器
	 */
	public static Sampler create(double selectPercent) {
		if (selectPercent == 100) {
			return new AlwaysSampler();
		} else if (selectPercent == 0) {
			return new NeverSampler();
		} else {
			return new Sampler(selectPercent);
		}
	}

	protected Sampler() {
	}

	/**
	 * @param selectPercent 采样率，在0-100 之间，可以有小数位
	 */
	public Sampler(double selectPercent) {
		Validate.isTrue((selectPercent >= 0) && (selectPercent <= 100),
				"Invalid selectPercent value: " + selectPercent);

		this.threshold = selectPercent / 100;
	}

	/**
	 * 判断当前请求是否命中采样
	 */
	public boolean select() {
		return RandomUtil.threadLocalRandom().nextDouble() < threshold;
	}

	/**
	 * 采样率为100时，总是返回true
	 */
	public static class AlwaysSampler extends Sampler {

		public AlwaysSampler() {
		}

		public boolean select() {
			return true;
		}
	}

	/**
	 * 采样率为0时，总是返回false
	 */
	public static class NeverSampler extends Sampler {
		public boolean select() {
			return false;
		}
	}
}
