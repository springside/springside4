package org.springside.examples.miniservice.functional.ws;

import static org.junit.Assert.*;

import javax.xml.ws.BindingProvider;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springside.examples.miniservice.Start;
import org.springside.examples.miniservice.functional.BaseFunctionalTestCase;
import org.springside.examples.miniservice.functional.category.Smoke;
import org.springside.examples.miniservice.webservice.ws.AccountWebService;
import org.springside.examples.miniservice.webservice.ws.result.UserListResult;

/**
 * UserService Web服务的功能测试, 测试主要的接口调用.
 * 
 * 以用JAXWS的API自行创建.
 * 
 * @author calvin
 */

public class AccountWebServiceWithDynamicCreateClientIT extends BaseFunctionalTestCase {

	/**
	 * 测试搜索用户
	 */
	@Test
	@Category(Smoke.class)
	public void searchUser() {
		String address = Start.TEST_BASE_URL + "/ws/accountservice";

		JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
		proxyFactory.setAddress(address);
		proxyFactory.setServiceClass(AccountWebService.class);
		AccountWebService accountWebServiceCreated = (AccountWebService) proxyFactory.create();

		//(可选)演示重新设定endpoint address.
		((BindingProvider) accountWebServiceCreated).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				address);

		UserListResult result = accountWebServiceCreated.searchUser(null, null);

		assertTrue(result.getUserList().size() >= 4);
		assertEquals("Jack", result.getUserList().get(0).getName());
	}

}
