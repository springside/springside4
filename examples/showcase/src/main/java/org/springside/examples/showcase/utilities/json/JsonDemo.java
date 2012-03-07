package org.springside.examples.showcase.utilities.json;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonView;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.type.JavaType;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springside.modules.mapper.JsonMapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 演示Jackson的基本使用方式及大量的特殊Feature.
 * 
 * @author calvin
 */
public class JsonDemo {

	private static JsonMapper mapper = JsonMapper.buildNonDefaultMapper();

	/**
	 * 序列化对象/集合到Json字符串.
	 */
	@Test
	public void toJson() throws Exception {
		//Bean
		TestBean bean = new TestBean("A");
		String beanString = mapper.toJson(bean);
		System.out.println("Bean:" + beanString);
		assertEquals("{\"name\":\"A\"}", beanString);

		//Map
		Map<String, Object> map = Maps.newLinkedHashMap();
		map.put("name", "A");
		map.put("age", 2);
		String mapString = mapper.toJson(map);
		System.out.println("Map:" + mapString);
		assertEquals("{\"name\":\"A\",\"age\":2}", mapString);

		//List<String>
		List<String> stringList = Lists.newArrayList("A", "B", "C");
		String listString = mapper.toJson(stringList);
		System.out.println("String List:" + listString);
		assertEquals("[\"A\",\"B\",\"C\"]", listString);

		//List<Bean>
		List<TestBean> beanList = Lists.newArrayList(new TestBean("A"), new TestBean("B"));
		String beanListString = mapper.toJson(beanList);
		System.out.println("Bean List:" + beanListString);
		assertEquals("[{\"name\":\"A\"},{\"name\":\"B\"}]", beanListString);

		//Bean[]
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
		//Bean
		String beanString = "{\"name\":\"A\"}";
		TestBean bean = mapper.fromJson(beanString, TestBean.class);
		System.out.println("Bean:" + bean);

		//Map
		String mapString = "{\"name\":\"A\",\"age\":2}";
		Map<String, Object> map = mapper.fromJson(mapString, HashMap.class);
		System.out.println("Map:");
		for (Entry<String, Object> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}

		//List<String>
		String listString = "[\"A\",\"B\",\"C\"]";
		List<String> stringList = mapper.fromJson(listString, List.class);
		System.out.println("String List:");
		for (String element : stringList) {
			System.out.println(element);
		}

		//List<Bean>
		String beanListString = "[{\"name\":\"A\"},{\"name\":\"B\"}]";
		JavaType beanListType = mapper.constructParametricType(List.class, TestBean.class);
		List<TestBean> beanList = mapper.fromJson(beanListString, beanListType);
		System.out.println("Bean List:");
		for (TestBean element : beanList) {
			System.out.println(element);
		}
	}

	/**
	 * 测试三种不同的Binder.
	 */
	@Test
	public void threeTypeBinders() {
		TestBean bean = new TestBean("A");

		//打印全部属性
		JsonMapper normalMapper = JsonMapper.buildNormalMapper();
		assertEquals("{\"name\":\"A\",\"defaultValue\":\"hello\",\"nullValue\":null}", normalMapper.toJson(bean));

		//不打印nullValue属性
		JsonMapper nonNullMapper = JsonMapper.buildNonNullMapper();
		assertEquals("{\"name\":\"A\",\"defaultValue\":\"hello\"}", nonNullMapper.toJson(bean));

		//不打印默认值未改变的nullValue与defaultValue属性
		JsonMapper nonDefaultMaper = JsonMapper.buildNonDefaultMapper();
		assertEquals("{\"name\":\"A\"}", nonDefaultMaper.toJson(bean));
	}

	/**
	 * 测试传入空对象,空字符串,Empty的集合,"null"字符串的结果.
	 */
	@Test
	public void nullAndEmpty() {
		// toJson测试 //

		//Null Bean
		TestBean nullBean = null;
		String nullBeanString = mapper.toJson(nullBean);
		assertEquals("null", nullBeanString);

		//Empty List
		List<String> emptyList = Lists.newArrayList();
		String emptyListString = mapper.toJson(emptyList);
		assertEquals("[]", emptyListString);

		// fromJson测试 //

		//Null String for Bean
		TestBean nullBeanResult = mapper.fromJson(null, TestBean.class);
		assertNull(nullBeanResult);

		nullBeanResult = mapper.fromJson("null", TestBean.class);
		assertNull(nullBeanResult);

		//Null/Empty String for List
		List nullListResult = mapper.fromJson(null, List.class);
		assertNull(nullListResult);

		nullListResult = mapper.fromJson("null", List.class);
		assertNull(nullListResult);

		nullListResult = mapper.fromJson("[]", List.class);
		assertEquals(0, nullListResult.size());
	}

	/**
	 * 测试对枚举的序列化,可以選擇用一個int字段而不是以Name來序列化，以減少長度.
	 */
	@Test
	public void enumData() {
		//默認使用enum.name()
		assertEquals("\"One\"", mapper.toJson(TestEnum.One));
		assertEquals(TestEnum.One, mapper.fromJson("\"One\"", TestEnum.class));

		//使用enum.toString()
		//注意，index會通過toString序列成字符串而不是int,否則又和順序號混淆.
		//注意配置必須在所有讀寫動作之前調用.
		JsonMapper newMapper = JsonMapper.buildNormalMapper();
		newMapper.setEnumUseToString(true);
		assertEquals("\"1\"", newMapper.toJson(TestEnum.One));
		assertEquals(TestEnum.One, newMapper.fromJson("\"1\"", TestEnum.class));
	}

	/**
	 * 枚舉類型的演示Bean.
	 */
	public static enum TestEnum {
		One(1), Two(2), Three(3);

		private int index;

		TestEnum(int index) {
			this.index = index;
		}

		@Override
		public String toString() {
			return String.valueOf(index);
		}
	}

	/**
	 * 测试对日期的序列化.
	 */
	@Test
	public void dateData() {
		DateTime jodaDate = new DateTime();

		//日期默认以Timestamp方式存储
		Date date = new Date(jodaDate.getMillis());
		String tsString = String.valueOf(jodaDate.getMillis());

		assertEquals(tsString, mapper.toJson(date));

		assertEquals(date, mapper.fromJson(tsString, Date.class));
	}

	/**
	 * JSON字符串裡只含有Bean中部分的屬性時，更新一個已存在Bean，只覆蓋部分的屬性.
	 */
	@Test
	public void updateBean() {
		String jsonString = "{\"name\":\"A\"}";

		TestBean bean = new TestBean();
		bean.setDefaultValue("Foobar");
		bean = mapper.update(bean, jsonString);
		assertEquals("A", bean.getName());
		assertEquals("Foobar", bean.getDefaultValue());
	}

	/**
	 * 測試父子POJO間的循環引用.
	 */
	@Test
	public void parentChildBean() {
		//初始化对象关系，parent的Childs里含有 child1,child2, child1/child2的parent均指向parent.
		ParentChildBean parent = new ParentChildBean("parent");

		ParentChildBean child1 = new ParentChildBean("child1");
		child1.setParent(parent);
		parent.getChilds().add(child1);

		ParentChildBean child2 = new ParentChildBean("child2");
		child2.setParent(parent);
		parent.getChilds().add(child2);

		String jsonString = "{\"name\":\"parent\",\"childs\":[{\"name\":\"child1\"},{\"name\":\"child2\"}]}";
		//打印parent的json输出，json字符串裡childs中的child1/child2都不包含到parent的屬性
		assertEquals(jsonString, mapper.toJson(parent));

		//注意此時如果單獨打印child1，也不會打印parent，信息將丟失。
		assertEquals("{\"name\":\"child1\"}", mapper.toJson(child1));

		//反向序列化时，Json已很聪明的把parent填入child1/child2中.
		ParentChildBean parentResult = mapper.fromJson(jsonString, ParentChildBean.class);
		assertEquals("parent", parentResult.getChilds().get(0).getParent().getName());
	}

	/**
	 * 父子POJO間的循環引用的演示Bean,@JsonBackReference 与 @JsonManagedReference 是关键.
	 */
	public static class ParentChildBean {

		private String name;
		private ParentChildBean parent;
		private List<ParentChildBean> childs = Lists.newArrayList();

		public ParentChildBean() {
		}

		public ParentChildBean(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		//注意getter與setter都要添加annotation
		@JsonBackReference
		public ParentChildBean getParent() {
			return parent;
		}

		@JsonBackReference
		public void setParent(ParentChildBean parent) {
			this.parent = parent;
		}

		@JsonManagedReference
		public List<ParentChildBean> getChilds() {
			return childs;
		}

		@JsonManagedReference
		public void setChilds(List<ParentChildBean> childs) {
			this.childs = childs;
		}
	}

	/**
	 * 測試可擴展Bean,會自動的把確定的屬性放入固定的成員變量, 其他屬性放到一个类型为Map的成员变量裡,能很好的支持Bean版本升级时固定属性的变动.
	 */
	@Test
	public void extensibleBean() {
		//一个没有区分是变量还是Map的普通JSON字符串.
		String jsonString = "{\"name\" : \"Foobar\",\"age\" : 37,\"occupation\" : \"coder man\"}";
		ExtensibleBean extensibleBean = mapper.fromJson(jsonString, ExtensibleBean.class);
		assertEquals("Foobar", extensibleBean.getName());
		assertEquals(null, extensibleBean.getProperties().get("name"));
		assertEquals("coder man", extensibleBean.getProperties().get("occupation"));
	}

	/**
	 * 演示用的可擴展Bean.@JsonAnySetter与@JsonAnyGetter是关键.
	 */
	public static class ExtensibleBean {
		private String name; // we always have name

		private Map<String, String> properties = Maps.newHashMap();

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
	 * 測試序列化Bean时使用不同的View序列化不同的属性组, 及@JsonIgnore標註的屬性.
	 */
	@Test
	public void viewBean() throws IOException {
		ViewBean viewBean = new ViewBean();
		viewBean.setName("Foo");
		viewBean.setAge(16);
		viewBean.setOtherValue("others");
		viewBean.setIgnoreValue("ignored");

		ObjectWriter publicWriter = mapper.getMapper().writerWithView(Views.Public.class);
		assertEquals("{\"name\":\"Foo\",\"otherValue\":\"others\"}", publicWriter.writeValueAsString(viewBean));
		ObjectWriter internalWriter = mapper.getMapper().writerWithView(Views.Internal.class);
		assertEquals("{\"age\":16,\"otherValue\":\"others\"}", internalWriter.writeValueAsString(viewBean));

		//設置默認是否顯示沒有用@Json定義的屬性
		JsonMapper newMapper = JsonMapper.buildNormalMapper();
		newMapper.getMapper().configure(SerializationConfig.Feature.DEFAULT_VIEW_INCLUSION, false);
		publicWriter = newMapper.getMapper().writerWithView(Views.Public.class);
		assertEquals("{\"name\":\"Foo\"}", publicWriter.writeValueAsString(viewBean));
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
	public static class ViewBean {
		private String name;
		private int age;
		private String otherValue;
		private String ignoreValue;

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

		@JsonIgnore
		public String getIgnoreValue() {
			return ignoreValue;
		}

		public void setIgnoreValue(String ignoreValue) {
			this.ignoreValue = ignoreValue;
		}

	}

	/**
	 * 测试自定义转换器
	 */
	@Test
	public void customConverter() {

		JsonMapper newMapper = JsonMapper.buildNonNullMapper();
		SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null));
		testModule.addSerializer(new MoneySerializer()); // assuming serializer declares correct class to bind to
		testModule.addDeserializer(Money.class, new MoneyDeserializer());
		newMapper.getMapper().registerModule(testModule);

		Money money = new Money(1.2);

		String jsonString = newMapper.toJson(money);

		assertEquals("\"1.2\"", jsonString);

		Money resultMoney = newMapper.fromJson(jsonString, Money.class);

		assertEquals(new Double(1.2), resultMoney.value);

	}

	public class MoneySerializer extends SerializerBase<Money> {
		public MoneySerializer() {
			super(Money.class);
		}

		public void serialize(Money value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
				JsonProcessingException {

			jgen.writeString(value.toString());
		}
	}

	public class MoneyDeserializer extends StdDeserializer<Money> {
		public MoneyDeserializer() {
			super(Money.class);
		}

		@Override
		public Money deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
				JsonProcessingException {
			return Money.valueOf(jp.getText());
		}

	}

	public static class Money {
		private Double value;

		public Money(Double value) {
			this.value = value;
		}

		public static Money valueOf(String value) {
			Double srcValue = Double.valueOf(value);
			return new Money(srcValue);
		}

		public String toString() {
			return value.toString();
		}
	}

	/**
	 * 测试修改属性名策略
	 * @throws JsonMappingException 
	 */
	@Test
	public void customPropertyNameing() throws JsonMappingException {

		TestBean bean = new TestBean("foo");
		bean.setDefaultValue("bar");
		JsonMapper newMapper = JsonMapper.buildNonNullMapper();
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
	 * 演示Bean, 主要演示不同風格的Mapper對Null值，初始化後沒改變過的屬性值的處理.
	 */
	public static class TestBean {

		private String name;
		private String defaultValue = "hello"; //默认值没被修改过的属性，可能会不序列化
		private String nullValue = null; //空值的据行，可能会不序列化

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
}
