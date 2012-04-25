package org.springside.examples.showcase.utilities.xml;

import static org.junit.Assert.*;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springside.modules.mapper.JaxbMapper;
import org.springside.modules.mapper.JaxbMapper.CollectionWrapper;

import com.google.common.collect.Lists;

/**
 * 演示基于JAXB2.0的Java对象-XML转换及Dom4j的使用.
 * 
 * @author calvin
 * 
 * 演示用xml如下:
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <user id="1">
 * 	<name>calvin</name>
 * 	<roles>
 * 		<role id="1" name="admin"/>
 * 		<role id="2" name="user"/>
 * 	</roles>
 * 	<interests>
 * 		<interest>movie</interest>
 * 		<interest>sports</interest>
 * 	</interests>
 * 	<houses>
 * 		<house key="bj">house1</item>
 * 		<hosue key="gz">house2</item>
 * 	</houses>
 * </user>
 */
public class JaxbDemo {

	private static JaxbMapper mapper;

	@BeforeClass
	public static void setUp() {
		mapper = new JaxbMapper(User.class, CollectionWrapper.class);
	}

	@Test
	public void objectToXml() {
		User user = new User();
		user.setId(1L);
		user.setName("calvin");

		user.getRoles().add(new Role(1L, "admin"));
		user.getRoles().add(new Role(2L, "user"));
		user.getInterests().add("movie");
		user.getInterests().add("sports");

		user.getHouses().put("bj", "house1");
		user.getHouses().put("gz", "house2");

		String xml = mapper.toXml(user, "UTF-8");
		System.out.println("Jaxb Object to Xml result:\n" + xml);
		assertXmlByDom4j(xml);
	}

	@Test
	public void xmlToObject() {
		String xml = generateXmlByDom4j();
		User user = mapper.fromXml(xml);

		System.out.println("Jaxb Xml to Object result:\n" + user);

		assertEquals(Long.valueOf(1L), user.getId());
		assertEquals(2, user.getRoles().size());
		assertEquals("admin", user.getRoles().get(0).getName());

		assertEquals(2, user.getInterests().size());
		assertEquals("movie", user.getInterests().get(0));

		assertEquals(2, user.getHouses().size());
		assertEquals("house1", user.getHouses().get("bj"));
	}

	/**
	 * 测试以List对象作为根节点时的XML输出
	 */
	@Test
	public void toXmlWithListAsRoot() {
		User user1 = new User();
		user1.setId(1L);
		user1.setName("calvin");

		User user2 = new User();
		user2.setId(2L);
		user2.setName("kate");

		List<User> userList = Lists.newArrayList(user1, user2);

		String xml = mapper.toXml(userList, "userList", "UTF-8");
		System.out.println("Jaxb Object List to Xml result:\n" + xml);
	}

	/**
	 * 使用Dom4j生成测试用的XML文档字符串.
	 */
	private static String generateXmlByDom4j() {
		Document document = DocumentHelper.createDocument();

		Element root = document.addElement("user").addAttribute("id", "1");

		root.addElement("name").setText("calvin");

		//List<Role>
		Element roles = root.addElement("roles");
		roles.addElement("role").addAttribute("id", "1").addAttribute("name", "admin");
		roles.addElement("role").addAttribute("id", "2").addAttribute("name", "user");

		//List<String>
		Element interests = root.addElement("interests");
		interests.addElement("interest").addText("movie");
		interests.addElement("interest").addText("sports");

		//Map<String,String>
		Element houses = root.addElement("houses");
		houses.addElement("house").addAttribute("key", "bj").addText("house1");
		houses.addElement("house").addAttribute("key", "gz").addText("house2");

		return document.asXML();
	}

	/**
	 * 使用Dom4j验证Jaxb所生成XML的正确性.
	 */
	private static void assertXmlByDom4j(String xml) {
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			fail(e.getMessage());
		}
		Element user = doc.getRootElement();
		assertEquals("1", user.attribute("id").getValue());

		Element adminRole = (Element) doc.selectSingleNode("//roles/role[@id=1]");
		assertEquals(2, adminRole.getParent().elements().size());
		assertEquals("admin", adminRole.attribute("name").getValue());

		Element interests = (Element) doc.selectSingleNode("//interests");
		assertEquals(2, interests.elements().size());
		assertEquals("movie", ((Element) interests.elements().get(0)).getText());

		Element house1 = (Element) doc.selectSingleNode("//houses/house[@key='bj']");
		assertEquals("house1", house1.getText());
	}
}
