package org.springside.examples.showcase.demos.utilities.json;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.springside.modules.mapper.JsonMapper;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 演示Jackson的基本使用方式及大量的特殊Feature.
 * 
 * @author calvin
 */
public class JsonDemo {

	private static JsonMapper mapper = JsonMapper.nonDefaultMapper();

	// // 基本操作 演示 ////

	/**
	 * 序列化对象/集合到Json字符串.
	 */
	@Test
	public void toJson() throws Exception {
		// Bean
		TestBean bean = new TestBean("A");
		String beanString = mapper.toJson(bean);
		System.out.println("Bean:" + beanString);
		assertEquals("{\"name\":\"A\"}", beanString);

		// Map
		Map<String, Object> map = Maps.newLinkedHashMap();
		map.put("name", "A");
		map.put("age", 2);
		String mapString = mapper.toJson(map);
		System.out.println("Map:" + mapString);
		assertEquals("{\"name\":\"A\",\"age\":2}", mapString);

		// List<String>
		List<String> stringList = Lists.newArrayList("A", "B", "C");
		String listString = mapper.toJson(stringList);
		System.out.println("String List:" + listString);
		assertEquals("[\"A\",\"B\",\"C\"]", listString);

		// List<Bean>
		List<TestBean> beanList = Lists.newArrayList(new TestBean("A"), new TestBean("B"));
		String beanListString = mapper.toJson(beanList);
		System.out.println("Bean List:" + beanListString);
		assertEquals("[{\"name\":\"A\"},{\"name\":\"B\"}]", beanListString);

		// Bean[]
		TestBean[] beanArray = new TestBean[] { new TestBean("A"), new TestBean("B") };
		String beanArrayString = mapper.toJson(beanArray);
		System.out.println("Array List:" + beanArrayString);
		assertEquals("[{\"name\":\"A\"},{\"name\":\"B\"}]", beanArrayString);
	}

	/**
	 * 从Json字符串反序列化对象/集合.
	 */
	@Test
	public void fromJson() throws Exception {
		// Bean
		String beanString = "{\"name\":\"A\"}";
		TestBean bean = mapper.fromJson(beanString, TestBean.class);
		System.out.println("Bean:" + bean);

		// Map
		String mapString = "{\"name\":\"A\",\"age\":2}";
		Map<String, Object> map = mapper.fromJson(mapString, HashMap.class);
		System.out.println("Map:");
		for (Entry<String, Object> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}

		// List<String>
		String listString = "[\"A\",\"B\",\"C\"]";
		List<String> stringList = mapper.fromJson(listString, List.class);
		System.out.println("String List:");
		for (String element : stringList) {
			System.out.println(element);
		}

		// List<Bean>
		String beanListString = "[{\"name\":\"A\"},{\"name\":\"B\"}]";
		JavaType beanListType = mapper.contructCollectionType(List.class, TestBean.class);
		List<TestBean> beanList = mapper.fromJson(beanListString, beanListType);
		System.out.println("Bean List:");
		for (TestBean element : beanList) {
			System.out.println(element);
		}
	}

	/**
	 * 测试三种不同的Inclusion风格.
	 */
	@Test
	public void threeTypeInclusion() {
		TestBean bean = new TestBean("A");

		// 打印全部属性
		JsonMapper normalMapper = new JsonMapper();
		assertEquals("{\"name\":\"A\",\"defaultValue\":\"hello\",\"nullValue\":null}", normalMapper.toJson(bean));

		// 不打印nullValue属性
		JsonMapper nonEmptyMapper = JsonMapper.nonEmptyMapper();
		assertEquals("{\"name\":\"A\",\"defaultValue\":\"hello\"}", nonEmptyMapper.toJson(bean));

		// 不打印默认值未改变的nullValue与defaultValue属性
		JsonMapper nonDefaultMaper = JsonMapper.nonDefaultMapper();
		assertEquals("{\"name\":\"A\"}", nonDefaultMaper.toJson(bean));
	}

	/*
	 * 测试类似Jaxb的常用annotaion，如properName，ignore，propertyOrder
	 */
	@Test
	public void jacksonAnnoation() {
		TestBean2 testBean = new TestBean2(1, "foo", 18);
		// 结果name属性输出在前，且被改名为productName，且age属性被ignore
		assertEquals("{\"productName\":\"foo\",\"id\":1}", mapper.toJson(testBean));
	}

	/*
	 * 测试直接使用Jaxb的annotaion
	 */
	@Test
	public void jaxbAnnoation() {
		JsonMapper newMapper = new JsonMapper();
		newMapper.enableJaxbAnnotation();
		TestBean3 testBean = new TestBean3(1, "foo", 18);
		// 结果name属性输出在前，且被改名为productName，且age属性被ignore
		assertEquals("{\"productName\":\"foo\",\"id\":1}", newMapper.toJson(testBean));
	}

	// 调转顺序
	@JsonPropertyOrder({ "name", "id" })
	public static class TestBean2 {

		public long id;

		@JsonProperty("productName")
		public String name;

		@JsonIgnore
		public int age;

		public TestBean2() {

		}

		public TestBean2(long id, String name, int age) {
			this.id = id;
			this.name = name;
			this.age = age;
		}

	}

	// 调转顺序
	@XmlType(propOrder = { "name", "id" })
	public static class TestBean3 {

		public long id;

		@XmlElement(name = "productName")
		public String name;

		@XmlTransient
		public int age;

		public TestBean3() {

		}

		public TestBean3(long id, String name, int age) {
			this.id = id;
			this.name = name;
			this.age = age;
		}

	}

	/**
	 * 更新一個已存在Bean，JSON字符串裡只含有Bean的部分屬性，只覆蓋这部分的屬性.
	 */
	@Test
	public void updateBean() {
		String jsonString = "{\"name\":\"A\"}";

		TestBean bean = new TestBean();
		bean.setDefaultValue("Foobar");

		bean = mapper.update(jsonString, bean);

		// name被赋值
		assertEquals("A", bean.getName());
		// DefaultValue不在Json串中，依然保留。
		assertEquals("Foobar", bean.getDefaultValue());
	}

	/**
	 * 測試輸出jsonp格式內容.
	 */
	@Test
	public void jsonp() {
		TestBean bean = new TestBean("foo");
		String jsonpString = mapper.toJsonP("callback", bean);
		assertEquals("callback({\"name\":\"foo\"})", jsonpString);
	}

	/**
	 * 演示用的Bean, 主要演示不同風格的Mapper對Null值，初始化後沒改變過的屬性值的處理.
	 */
	public static class TestBean {

		private String name;
		private String defaultValue = "hello"; // 默认值没被修改过的属性，可能会不序列化
		private String nullValue = null; // 空值的据行，可能会不序列化

		public TestBean() {
		}

		public TestBean(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		public String getNullValue() {
			return nullValue;
		}

		public void setNullValue(String nullValue) {
			this.nullValue = nullValue;
		}

		@Override
		public String toString() {
			return "TestBean [defaultValue=" + defaultValue + ", name=" + name + ", nullValue=" + nullValue + "]";
		}
	}

	// //特殊数据类型演示////

	/**
	 * 测试对枚举的序列化.
	 */
	@Test
	public void enumType() {
		// toJSon默認使用enum.name()
		assertEquals("\"One\"", mapper.toJson(TestEnum.One));
		// fromJson使用enum.name()或enum.order()
		assertEquals(TestEnum.One, mapper.fromJson("\"One\"", TestEnum.class));
		assertEquals(TestEnum.One, mapper.fromJson("0", TestEnum.class));

		// 使用enum.toString(), 注意配置必須在所有讀寫動作之前調用.
		// 建议toString()使用index数字属性，比enum.name()节约了空间，比enum.order()则不会有顺序随时改变不确定的问题。
		JsonMapper newMapper = new JsonMapper();
		newMapper.enableEnumUseToString();
		assertEquals("\"1\"", newMapper.toJson(TestEnum.One));
		assertEquals(TestEnum.One, newMapper.fromJson("\"1\"", TestEnum.class));
	}

	/**
	 * 枚舉類型的演示Bean.
	 */
	public static enum TestEnum {
		One(1), Two(2), Three(3);

		private final int index;

		TestEnum(int index) {
			this.index = index;
		}

		@Override
		public String toString() {
			return String.valueOf(index);
		}
	}

	/**
	 * 测试对日期的序列化,日期默认以Timestamp方式存储，也可以用2.0后也可以用@JsonFormat在属性上格式化.
	 * 但JodaTime仍然只支持Timestamp形式, 或调用JodaTime.toString().
	 */
	@Test
	public void dateType() {

		mapper.getMapper().registerModule(new JodaModule());

		Date date = new Date();
		DateTime dateTime = new DateTime(date);
		String timestampString = String.valueOf(date.getTime());
		String format = "yyyy-MM-dd HH:mm:ss";
		String formatedString = new DateTime(date).toString(format);

		DateBean dateBean = new DateBean();
		dateBean.startDate = date;
		dateBean.endDate = date;
		dateBean.dateTime = dateTime;

		// to json
		String expectedJson = "{\"startDate\":" + timestampString + ",\"endDate\":\"" + formatedString
				+ "\",\"dateTime\":" + timestampString + "}";
		assertEquals(expectedJson, mapper.toJson(dateBean));

		// from json
		Date expectedEndDate = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(formatedString).toDate();

		DateBean resultBean = mapper.fromJson(expectedJson, DateBean.class);
		assertEquals(date, resultBean.startDate);
		assertEquals(expectedEndDate, resultBean.endDate);
	}

	public static class DateBean {
		// 默认timestamp存储
		public Date startDate;
		// 按annotation中的日期格式存储。
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
		public Date endDate;

		public DateTime dateTime;
	}

	/**
	 * 测试传入空对象,空字符串,Empty的集合,"null"字符串的结果.
	 */
	@Test
	public void nullAndEmpty() {
		// toJson测试 //

		// Null Bean
		TestBean nullBean = null;
		String nullBeanString = mapper.toJson(nullBean);
		assertEquals("null", nullBeanString);

		// Empty List
		List<String> emptyList = Lists.newArrayList();
		String emptyListString = mapper.toJson(emptyList);
		assertEquals("[]", emptyListString);

		// fromJson测试 //

		// Null String for Bean
		TestBean nullBeanResult = mapper.fromJson(null, TestBean.class);
		assertNull(nullBeanResult);

		nullBeanResult = mapper.fromJson("null", TestBean.class);
		assertNull(nullBeanResult);

		// Null/Empty String for List
		List nullListResult = mapper.fromJson(null, List.class);
		assertNull(nullListResult);

		nullListResult = mapper.fromJson("null", List.class);
		assertNull(nullListResult);

		nullListResult = mapper.fromJson("[]", List.class);
		assertEquals(0, nullListResult.size());
	}

	// // 高级应用 ////
	/**
	 * 測試父子POJO間的循環引用.
	 */
	@Test
	public void cycleReferenceBean() {
		// 初始化对象关系，parent的children里含有 child1,child2, child1/child2的parent均指向parent.
		CycleReferenceBean parent = new CycleReferenceBean("parent");

		CycleReferenceBean child1 = new CycleReferenceBean("child1");
		child1.setParent(parent);
		parent.getChildren().add(child1);

		CycleReferenceBean child2 = new CycleReferenceBean("child2");
		child2.setParent(parent);
		parent.getChildren().add(child2);

		// 序列化是, json字符串裡children中的child1/child2都不包含到parent的屬性
		String jsonString = "{\"name\":\"parent\",\"children\":[{\"name\":\"child1\"},{\"name\":\"child2\"}]}";
		assertEquals(jsonString, mapper.toJson(parent));

		// 注意此時如果單獨序列化child1，也不會打印parent，信息將丟失。
		assertEquals("{\"name\":\"child1\"}", mapper.toJson(child1));

		// 反向序列化时，Json已很聪明的把parent填入child1/child2中.
		CycleReferenceBean parentResult = mapper.fromJson(jsonString, CycleReferenceBean.class);
		assertEquals("parent", parentResult.getChildren().get(0).getParent().getName());

		// 单独反序列化child1，当然parent也是空
		CycleReferenceBean child1Result = mapper.fromJson("{\"name\":\"child1\"}", CycleReferenceBean.class);
		assertNull(child1Result.parent);
		assertEquals("child1", child1Result.getName());
	}

	/**
	 * 父子POJO間的循環引用的演示Bean,@JsonBackReference 与 @JsonManagedReference 是关键.
	 */
	public static class CycleReferenceBean {

		private String name;
		private CycleReferenceBean parent;
		private List<CycleReferenceBean> children = Lists.newArrayList();

		public CycleReferenceBean() {
		}

		public CycleReferenceBean(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		// 注意getter與setter都要添加annotation
		@JsonBackReference
		public CycleReferenceBean getParent() {
			return parent;
		}

		@JsonBackReference
		public void setParent(CycleReferenceBean parent) {
			this.parent = parent;
		}

		@JsonManagedReference
		public List<CycleReferenceBean> getChildren() {
			return children;
		}

		@JsonManagedReference
		public void setChildren(List<CycleReferenceBean> children) {
			this.children = children;
		}
	}

	/**
	 * 測試可擴展Bean.
	 * 可扩展Bean的设计会混合一些的固定属性和用一个Map<String,object>存放的扩展属性。
	 * 通常，哪那些是固定属性，哪些是扩展属性，在应用不断演进中是不断变化的。
	 * Jackson支持将所有属性都序列化成平行的属性列表，没有固定属性与Map中属性的区别，然后智能的将不在固定列的属性都丢到被@JsonAnyGetter/Setter注释的Map里面去。
	 */
	@Test
	public void extensibleBean() {
		// 一个没有区分是变量还是Map的普通JSON字符串.
		String jsonString = "{\"name\" : \"Foobar\",\"age\" : 37,\"occupation\" : \"coder man\"}";
		ExtensibleBean extensibleBean = mapper.fromJson(jsonString, ExtensibleBean.class);
		// 固定属性
		assertEquals("Foobar", extensibleBean.getName());
		assertEquals(null, extensibleBean.getProperties().get("name"));

		// 可扩展属性
		assertEquals("coder man", extensibleBean.getProperties().get("occupation"));
	}

	/**
	 * 演示用的可擴展Bean.@JsonAnySetter与@JsonAnyGetter是关键.
	 */
	public static class ExtensibleBean {
		// 固定属性
		private String name;
		// 扩展属性
		private final Map<String, String> properties = Maps.newHashMap();

		public ExtensibleBean() {
		}

		@JsonAnySetter
		public void add(String key, String value) {
			properties.put(key, value);
		}

		@JsonAnyGetter
		public Map<String, String> getProperties() {
			return properties;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	/**
	 * 同一种POJO，在不同场景下可能需要序列化不同的属性组，Jackson支持使用View来定义.
	 */
	@Test
	public void multiViewBean() throws IOException {
		MultiViewBean multiViewBean = new MultiViewBean();
		multiViewBean.setName("Foo");
		multiViewBean.setAge(16);
		multiViewBean.setOtherValue("others");

		// public view
		ObjectWriter publicWriter = mapper.getMapper().writerWithView(Views.Public.class);
		assertEquals("{\"name\":\"Foo\",\"otherValue\":\"others\"}", publicWriter.writeValueAsString(multiViewBean));

		// internal view
		ObjectWriter internalWriter = mapper.getMapper().writerWithView(Views.Internal.class);
		assertEquals("{\"age\":16,\"otherValue\":\"others\"}", internalWriter.writeValueAsString(multiViewBean));

	}

	public static class Views {
		static class Public {
		}

		static class Internal {
		}
	}

	/**
	 * 演示序列化不同View不同属性的Bean.
	 */
	public static class MultiViewBean {
		private String name;
		private int age;
		private String otherValue;

		@JsonView(Views.Public.class)
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@JsonView(Views.Internal.class)
		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public String getOtherValue() {
			return otherValue;
		}

		public void setOtherValue(String otherValue) {
			this.otherValue = otherValue;
		}
	}

	// //自定制行为////

	/**
	 * 测试自定义转换器，整体感觉稍显复杂。这里是将Money和Long互转.
	 */
	@Test
	public void customConverter() {

		JsonMapper newMapper = JsonMapper.nonEmptyMapper();

		SimpleModule moneyModule = new SimpleModule("MoneyModule");
		moneyModule.addSerializer(new MoneySerializer());
		moneyModule.addDeserializer(Money.class, new MoneyDeserializer());
		newMapper.getMapper().registerModule(moneyModule);

		// tojson
		User user = new User();
		user.setName("foo");
		user.setSalary(new Money(1.2));

		String jsonString = newMapper.toJson(user);

		assertEquals("{\"name\":\"foo\",\"salary\":\"1.2\"}", jsonString);

		// from
		User resultUser = newMapper.fromJson(jsonString, User.class);
		assertEquals(Double.valueOf(1.2), resultUser.getSalary().value);

	}

	public class MoneySerializer extends StdSerializer<Money> {
		public MoneySerializer() {
			super(Money.class);
		}

		@Override
		public void serialize(Money value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

			jgen.writeString(value.toString());
		}
	}

	public class MoneyDeserializer extends StdDeserializer<Money> {
		public MoneyDeserializer() {
			super(Money.class);
		}

		@Override
		public Money deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
			return Money.valueOf(jp.getText());
		}

	}

	public static class Money {
		private final Double value;

		public Money(Double value) {
			this.value = value;
		}

		public static Money valueOf(String value) {
			Double srcValue = Double.valueOf(value);
			return new Money(srcValue);
		}

		@Override
		public String toString() {
			return value.toString();
		}
	}

	/**
	 * 包含Money属性的对象.
	 */
	public static class User {
		private String name;
		private Money salary;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Money getSalary() {
			return salary;
		}

		public void setSalary(Money salary) {
			this.salary = salary;
		}

	}

	/**
	 * 测试修改 属性名策略。
	 */
	@Test
	public void customPropertyNaming() throws JsonMappingException {

		TestBean bean = new TestBean("foo");
		bean.setDefaultValue("bar");
		JsonMapper newMapper = JsonMapper.nonEmptyMapper();
		newMapper.getMapper().setPropertyNamingStrategy(new LowerCaseNaming());
		String jsonpString = newMapper.toJson(bean);
		assertEquals("{\"name\":\"foo\",\"defaultvalue\":\"bar\"}", jsonpString);
	}

	public static class LowerCaseNaming extends PropertyNamingStrategy {
		@Override
		public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
			return defaultName.toLowerCase();
		}
	}
}