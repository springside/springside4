package org.springside.modules.utils.collection;

import java.util.List;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.collections4.map.AbstractHashedMap;
import org.apache.commons.collections4.map.Flat3Map;
import org.apache.commons.collections4.map.MultiKeyMap;

/**
 * 使用CommonCollections中的扩展类型
 * 
 * 参考pom.xml, 引入apache Commons Collections4依赖
 * 
 * @author calvin
 */
public class CommonCollections {

	/**
	 * 联合多个Key来定位Value的HashMap.
	 * 
	 * 普通HashMap，需要将多个Key拼装成一个字符串作为主键，因此MultiKeyMap尤其适合于这几个Key的类型不是String时.
	 * 
	 * @param map 被包裹的底层HashMap，可先定义HashMap的初始大小和加载因子，可使用类型为LinkedMap
	 */
	public static <K, V> MultiKeyMap<K, V> multiKeyMap(final AbstractHashedMap<MultiKey<? extends K>, V> map) {
		return MultiKeyMap.multiKeyMap(map);
	}

	/**
	 * 创建性能与访问速度更优的微型Map
	 * 
	 * 当Map的元素个数<=3时，直接使用key1,key2,key3属性,节约空间与加快速度. 当Map的元素个数>3时，自动创建真正的HashMap
	 */
	public static <K, V> Flat3Map<K, V> flat3Map() {
		return new Flat3Map<K, V>();
	}

	/**
	 * 队列内元素唯一的List, 内部集成了一个HashSet来实现.
	 * 
	 * @param list 被包裹的底层List，可先定义ArrayList的初始长度等.
	 */
	public static <E> SetUniqueList<E> setUniqueList(final List<E> list) {
		return SetUniqueList.setUniqueList(list);
	}

	/**
	 * ArrayList 与 LinkedList的折衷版
	 * 
	 * ArrayList 随机访问快，当非末尾的插入删除慢. LinkedList的插入删除快，但非两端的操作都需要遍历链表到达指定下标.
	 * 
	 * TreeList是内部结构为TreeNode的Linked List, 与Linked相仿，但能更快的到达指定下标.
	 */
	public static <E> TreeList<E> treeList() {
		return new TreeList<E>();
	}
}
