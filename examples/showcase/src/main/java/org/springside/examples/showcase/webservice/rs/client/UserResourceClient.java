package org.springside.examples.showcase.webservice.rs.client;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Required;
import org.springside.examples.showcase.common.entity.User;
import org.springside.examples.showcase.webservice.rs.dto.UserDTO;
import org.springside.modules.jersey.JerseyClientFactory;
import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.utils.ee.Servlets;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.MultiPart;

/**
 * 使用Jersey Client的User REST客户端.
 * 在Mini-Service演示的基础上添加更多演示.
 * 
 * @author calvin
 */
public class UserResourceClient {

	private WebResource client;

	@Required
	public void setBaseUrl(String baseUrl) {
		client = JerseyClientFactory.createClient(baseUrl);
	}

	/**
	 * 访问有SpringSecurity安全控制的页面, 进行HttpBasic的登录.
	 */
	public List<UserDTO> getAllUser(String userName, String password) {
		String authentication = Servlets.encodeHttpBasic(userName, password);
		return client.path("/users").header(Servlets.AUTHENTICATION_HEADER, authentication)
				.accept(MediaType.APPLICATION_JSON).get(new GenericType<List<UserDTO>>() {
				});
	}

	public UserDTO getUser(Long id) {
		return client.path("/users/" + id).accept(MediaType.APPLICATION_JSON).get(UserDTO.class);
	}

	/**
	 * 返回html格式的特定内容.
	 */
	public String searchUserReturnHtml(String name) {
		return client.path("/users/search").queryParam("name", name).queryParam("format", "html").get(String.class);
	}

	/**
	 * 无公共DTO类定义, 取得返回JSON字符串后自行转换DTO.
	 */
	public UserDTO searchUserReturnJson(String name) {
		String jsonString = client.path("/users/search").queryParam("name", name).get(String.class);
		return JsonMapper.buildNormalMapper().fromJson(jsonString, UserDTO.class);
	}

	/**
	 * Multi-Part 演示。
	 */
	public String multipart(String userName, String descriptionText) {
		User user = new User();
		user.setName(userName);

		MultiPart inputMultiPart = new MultiPart().bodyPart(new BodyPart(user, MediaType.APPLICATION_JSON_TYPE))
				.bodyPart(new BodyPart(descriptionText, MediaType.TEXT_PLAIN_TYPE));

		MultiPart outputMultiPart = client.path("/users/multipart").type("multipart/mixed")
				.post(MultiPart.class, inputMultiPart);

		String resultString = outputMultiPart.getBodyParts().get(0).getEntityAs(String.class) + ":"
				+ outputMultiPart.getBodyParts().get(1).getEntityAs(String.class);
		;
		return resultString;
	}
}
