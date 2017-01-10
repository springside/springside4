package org.springside.modules.utils.collection.type;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springside.modules.utils.collection.SetUtil;

public class ConcurrentHashSetTest {

	@Test
	public void concurrentHashSet() {
		ConcurrentHashSet<String> conrrentHashSet = SetUtil.newConcurrentHashSet();
		conrrentHashSet.add("a");
		conrrentHashSet.add("b");
		conrrentHashSet.add("c");

		assertThat(conrrentHashSet.isEmpty()).isFalse();
		assertThat(conrrentHashSet.size()).isEqualTo(3);
		assertThat(conrrentHashSet.contains("a")).isTrue();
		assertThat(conrrentHashSet.contains("d")).isFalse();
		
		assertThat(conrrentHashSet).contains("a","b","c");
		
		for(String key: conrrentHashSet){
			System.out.print(key+",");
		}
	}

}
