package org.springside.modules.utils.reflect;

import org.junit.Test;

public class ClassloaderUtilTest {

	@Test
	public void test() {
		ClassLoader loader = ClassLoaderUtil.getDefaultClassLoader();
		ClassLoaderUtil.isPresent("org.springside.modules.utils.reflect.ClassUtil", loader);
	}
}
