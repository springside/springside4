package org.springside.examples.showcase.functional.soap;

import static org.junit.Assert.*;

import org.dozer.DozerBeanMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springside.examples.showcase.data.UserData;
import org.springside.examples.showcase.entity.User;
import org.springside.examples.showcase.functional.BaseFunctionalTestCase;
import org.springside.examples.showcase.webservice.soap.AccountWebService;
import org.springside.examples.showcase.webservice.soap.response.base.IdResponse;
import org.springside.examples.showcase.webservice.soap.response.base.WSResponse;
import org.springside.examples.showcase.webservice.soap.response.dto.UserDTO;

/**
 * UserService Web服务的功能测试, 测试主要的接口调用.
 * 
 * 使用在Spring applicaitonContext.xml中用<jaxws:client/>，根据AccountWebService接口创建的Client.
 * 
 * @author calvin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/applicationContext-soap-client.xml" })
public class AccountWebServiceWithPredefineClientFT extends BaseFunctionalTestCase {

	@Autowired
	private AccountWebService accountWebServiceClient;

	/**
	 * 测试创建用户,在Spring applicaitonContext.xml中用<jaxws:client/>创建Client.
	 */
	@Test
	public void createUser() {
		User user = UserData.randomUser();

		UserDTO userDTO = new UserDTO();
		userDTO.setLoginName(user.getLoginName());
		userDTO.setName(user.getName());
		userDTO.setEmail(user.getEmail());

		IdResponse result = accountWebServiceClient.createUser(userDTO);
		assertNotNull(result.getId());
	}

	/**
	 * 测试创建用户,使用错误的登录名, 在Spring applicaitonContext.xml中用<jaxws:client/>创建Client.
	 */
	@Test
	public void createUserWithInvalidLoginName() {
		User user = UserData.randomUser();
		UserDTO userDTO = new DozerBeanMapper().map(user, UserDTO.class);

		//登录名为空
		userDTO.setLoginName(null);
		IdResponse result = accountWebServiceClient.createUser(userDTO);
		assertEquals(result.getCode(), WSResponse.PARAMETER_ERROR);

		//登录名重复
		userDTO.setLoginName("user1");
		result = accountWebServiceClient.createUser(userDTO);
		assertEquals(result.getCode(), WSResponse.PARAMETER_ERROR);
	}
}
