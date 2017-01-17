package org.springside.modules.utils.collection.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Pair)) {
			return false;
		}

		Pair<?, ?> that = (Pair<?, ?>) o;
		return new EqualsBuilder().append(this.first, that.first).append(this.second, that.second).isEquals();
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)", getFirst(), getSecond());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(first).append(second).toHashCode();
	}

	/**
	 * 根据等号左边的泛型，自动构造合适的Pair
	 */
	public static <A, B> Pair<A, B> of(@Nullable A a, @Nullable B b) {
		return new Pair<A, B>(a, b);
	}
}
