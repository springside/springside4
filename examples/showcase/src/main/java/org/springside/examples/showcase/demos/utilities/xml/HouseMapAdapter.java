package org.springside.examples.showcase.demos.utilities.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.springside.examples.showcase.demos.utilities.xml.HouseMapAdapter.HouseMap.HouseEntry;

import com.google.common.collect.Maps;

/**
 * 为使Map<String,String> houses转化为有业务意义的xml的巨大努力,
 * 分别定义了一个Adapter--HouseMapAdapter, 一个List<HouseEntry> Wrapper类--HouseMap, 一个MapEntry表达类--HouseEntry.
 * 最后的劳动成果是：
 * 
 * <pre>
 * <houses>
 * 		<house key="bj">house1</item>
 * 		<hosue key="gz">house2</item>
 * </houses>
 * </pre>
 * 
 * @author calvin
 */
public class HouseMapAdapter extends XmlAdapter<HouseMapAdapter.HouseMap, Map<String, String>> {

	@Override
	public HouseMap marshal(Map<String, String> map) throws Exception {
		HouseMap houseMap = new HouseMap();
		for (Map.Entry<String, String> e : map.entrySet()) {
			houseMap.entries.add(new HouseEntry(e));
		}
		return houseMap;
	}

	@Override
	public Map<String, String> unmarshal(HouseMap houseMap) throws Exception {
		Map<String, String> map = Maps.newLinkedHashMap();
		for (HouseEntry e : houseMap.entries) {
			map.put(e.key, e.value);
		}
		return map;
	}

	/**
	 * List<HouseEntry>的Adapter类.
	 * 
	 * @author calvin
	 */
	@XmlType(name = "houses")
	public static class HouseMap {
		@XmlElement(name = "house")
		List<HouseEntry> entries = new ArrayList<HouseEntry>();

		/**
		 * HouseMap中的Entry类.
		 */
		static class HouseEntry {
			@XmlAttribute
			String key;

			@XmlValue
			String value;

			public HouseEntry() {
			}

			public HouseEntry(Map.Entry<String, String> e) {
				key = e.getKey();
				value = e.getValue();
			}
		}
	}
}