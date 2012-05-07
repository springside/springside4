package org.springside.examples.miniservice.functional.ws;

import static org.junit.Assert.*;

import org.dozer.DozerBeanMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springside.examples.miniservice.data.AccountData;
import org.springside.examples.miniservice.entity.User;
import org.springside.examples.miniservice.functional.BaseFunctionalTestCase;
import org.springside.examples.miniservice.webservice.dto.UserDTO;
import org.springside.examples.miniservice.webservice.ws.AccountWebService;
import org.springside.examples.miniservice.webservice.ws.response.base.IdResponse;
import org.springside.examples.miniservice.webservice.ws.response.base.WSResponse;

/**
 * UserService Web服务的功能测试, 测试主要的接口调用.
 * 
 * 使用在Spring applicaitonContext.xml中用<jaxws:client/>创建的Client.
 * 
 * @author calvin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/applicationContext-ws-client.xml" })
public class AccountWebServiceWithPredefineClientIT extends BaseFunctionalTestCase {

	@Autowired
	private AccountWebService accountWebServiceClient;

	/**
	 * 测试创建用户,在Spring applicaitonContext.xml中用<jaxws:client/>创建Client.
	 */
	@Test
	public void createUser() {
		User user = AccountData.getRandomUser();

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
		User user = AccountData.getRandomUser();
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
