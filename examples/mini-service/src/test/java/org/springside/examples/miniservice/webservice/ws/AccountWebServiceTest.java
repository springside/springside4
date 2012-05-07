package org.springside.examples.miniservice.webservice.ws;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springside.examples.miniservice.data.AccountData;
import org.springside.examples.miniservice.entity.Department;
import org.springside.examples.miniservice.service.AccountManager;
import org.springside.examples.miniservice.webservice.dto.DepartmentDTO;
import org.springside.examples.miniservice.webservice.ws.impl.AccountWebServiceImpl;
import org.springside.examples.miniservice.webservice.ws.response.DepartmentResponse;
import org.springside.examples.miniservice.webservice.ws.response.base.WSResponse;

/**
 * Account WebService的单元测试用例.
 * 
 * 使用Mockito对AccountManager进行模拟.
 * 
 * @author calvin
 */
public class AccountWebServiceTest {

	private AccountWebServiceImpl accountWebService;

	@Mock
	private AccountManager mockAccountManager;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		accountWebService = new AccountWebServiceImpl();
		accountWebService.setAccountManager(mockAccountManager);
	}

	/**
	 * 测试dozer正确映射.
	 */
	@Test
	public void dozerBinding() {
		Department department = AccountData.getRandomDepartment();
		Mockito.when(mockAccountManager.getDepartmentDetail(1L)).thenReturn(department);

		DepartmentResponse result = accountWebService.getDepartmentDetail(1L);

		assertEquals(WSResponse.SUCCESS, result.getCode());

		DepartmentDTO dto = result.getDepartment();

		assertEquals(department.getName(), dto.getName());
		assertEquals(department.getUserList().get(0).getName(), dto.getUserList().get(0).getName());
	}

	/**
	 * 测试系统内部抛出异常时的处理.
	 */
	@Test
	public void handleException() {
		Mockito.when(mockAccountManager.getDepartmentDetail(1L)).thenThrow(new RuntimeException("Expected exception."));

		DepartmentResponse result = accountWebService.getDepartmentDetail(1L);

		assertEquals(WSResponse.SYSTEM_ERROR, result.getCode());
		assertEquals(WSResponse.SYSTEM_ERROR_MESSAGE, result.getMessage());
	}
}
