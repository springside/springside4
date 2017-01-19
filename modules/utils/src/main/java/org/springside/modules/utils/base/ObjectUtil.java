package org.springside.modules.utils.base;

import java.util.Arrays;

public class ObjectUtil {

	private static String NULL = "null";

	/**
	 * 多个对象的HashCode串联
	 */
	public static int hashCode(Object... objects) {
		return Arrays.hashCode(objects);
	}

	/**
	 * 对象的toString()，处理了对象为数组和集合的情况.
	 */
	public static String toPrettyString(Object value) {
		if (value == null) {
			return NULL;
		}

		Class<?> type = value.getClass();

		if (type.isArray()) {
			Class componentType = type.getComponentType();

			if (componentType.isPrimitive()) {
				StringBuilder sb = new StringBuilder();

				if (componentType == int.class) {
					sb.append(Arrays.toString((int[]) value));
				} else if (componentType == long.class) {
					sb.append(Arrays.toString((long[]) value));
				} else if (componentType == double.class) {
					sb.append(Arrays.toString((double[]) value));
				} else if (componentType == float.class) {
					sb.append(Arrays.toString((float[]) value));
				} else if (componentType == boolean.class) {
					sb.append(Arrays.toString((boolean[]) value));
				} else if (componentType == short.class) {
					sb.append(Arrays.toString((short[]) value));
				} else if (componentType == byte.class) {
					sb.append(Arrays.toString((byte[]) value));
				} else {
					throw new IllegalArgumentException("unsupport array type");
				}

				return sb.toString();
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append('[');

				Object[] array = (Object[]) value;
				for (int i = 0; i < array.length; i++) {
					if (i > 0) {
						sb.append(", ");
					}
					sb.append(toPrettyString(array[i]));
				}
				sb.append(']');
				return sb.toString();
			}
		} else if (value instanceof Iterable) {
			Iterable iterable = (Iterable) value;
			StringBuilder sb = new StringBuilder();
			sb.append('{');
			int i = 0;
			for (Object o : iterable) {
				if (i > 0) {
					sb.append(',');
				}
				sb.append(toPrettyString(o));
				i++;
			}
			sb.append('}');
			return sb.toString();
		}

		return value.toString();
	}

}
