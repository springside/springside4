package org.springside.modules.utils.collection.type;

import org.springside.modules.utils.base.annotation.Nullable;

/**
 * 引入一个简简单单的Pair, 用于返回值返回两个元素.
 * 
 * from Twitter Common
 */
public class Pair<A, B> {

	@Nullable
	private final A first;
	@Nullable
	private final B second;

	/**
	 * Creates a new pair.
	 *
	 * @param first The first value.
	 * @param second The second value.
	 */
	public Pair(@Nullable A first, @Nullable B second) {
		this.first = first;
		this.second = second;
	}

	@Nullable
	public A getFirst() {
		return first;
	}

	@Nullable
	public B getSecond() {
		return second;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Pair other = (Pair) obj;
		if (first == null) {
			if (other.first != null) {
				return false;
			}
		} else if (!first.equals(other.first)) {
			return false;
		}
		if (second == null) {
			if (other.second != null) {
				return false;
			}
		} else if (!second.equals(other.second)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}

	/**
	 * 根据等号左边的泛型，自动构造合适的Pair
	 */
	public static <A, B> Pair<A, B> of(@Nullable A a, @Nullable B b) {
		return new Pair<A, B>(a, b);
	}
}
