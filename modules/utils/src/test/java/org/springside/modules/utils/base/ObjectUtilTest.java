package org.springside.modules.utils.base;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springside.modules.utils.collection.ListUtil;
public class ObjectUtilTest {
	
	@Test
	public void hashCodeTest() {
		assertThat(ObjectUtil.hashCode("a", "b") - ObjectUtil.hashCode("a", "a")).isEqualTo(1);
	}
	
	@Test
	public void toPrettyString(){
		assertThat(ObjectUtil.toPrettyString(1)).isEqualTo("1");
		
		assertThat(ObjectUtil.toPrettyString(new int[]{1,2})).isEqualTo("[1, 2]");
		assertThat(ObjectUtil.toPrettyString(new Integer[]{1,2})).isEqualTo("[1, 2]");
		assertThat(ObjectUtil.toPrettyString(ListUtil.newArrayList("1","2"))).isEqualTo("{1,2}");
	}

}
