package org.springside.modules.utils.collection.type;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springside.modules.utils.collection.ListUtil;

public class FastListTest {

	@Test
	public void test() {
		FastList<String> list = ListUtil.createFastList(String.class, 2);
		list.add("1");
		list.add("2");
		list.add("3");

		assertThat(list).hasSize(3);
		
		assertThat(list.isEmpty()).isFalse();

		assertThat(list).containsExactly("1","2","3");
		
		assertThat(list.get(1)).isEqualTo("2");
		
		assertThat(list.set(1, "3"));
		
		assertThat(list.get(1)).isEqualTo("3");
		
		list.remove(1);
		assertThat(list).containsExactly("1","3");
		
		list.remove("3");
		assertThat(list).containsExactly("1");
		
		list.add("2");
		list.add("3");
		list.removeLast();
		assertThat(list).containsExactly("1","2");
		
		
		list.clear();
		assertThat(list).hasSize(0);
		
		try{
		list.contains("2");
		}catch(Throwable t){
			assertThat(t).isInstanceOf(UnsupportedOperationException.class);
		}
	}

}
