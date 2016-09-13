package org.springside.modules.utils;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JDK并没有提供ConcurrenHashSet，考虑到JDK的HashSet也是基于HashMap实现的，因此ConcurrenHashSet也由ConcurrenHashMap完成。
 * 
 * 实现参考了Jetty的实现.
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E> {
	private final Map<E, Boolean> map;
	private transient Set<E> keys;

	public ConcurrentHashSet() {
		map = new ConcurrentHashMap<E, Boolean>();
		keys = map.keySet();
	}

	public ConcurrentHashSet(int initialCapacity) {
		map = new ConcurrentHashMap<E, Boolean>(initialCapacity);
		keys = map.keySet();
	}

	@Override
	public boolean add(E e) {
		return map.put(e, Boolean.TRUE) == null;
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o) != null;
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Iterator<E> iterator() {
		return keys.iterator();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return keys.containsAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return keys.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return keys.retainAll(c);
	}

	@Override
	public Object[] toArray() {
		return keys.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return keys.toArray(a);
	}

	@Override
	public boolean equals(Object o) {
		return o == this || keys.equals(o);
	}

	@Override
	public int hashCode() {
		return keys.hashCode();
	}

	@Override
	public String toString() {
		return keys.toString();
	}
}
