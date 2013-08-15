package org.springside.examples.showcase.webservice.soap;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.springside.examples.showcase.webservice.soap.response.GetTeamDetailResult;
import org.springside.examples.showcase.webservice.soap.response.GetUserResult;
import org.springside.examples.showcase.webservice.soap.response.SearchUserResult;
import org.springside.examples.showcase.webservice.soap.response.base.IdResult;
import org.springside.examples.showcase.webservice.soap.response.dto.UserDTO;

/**
 * JAX-WS2.0的WebService接口定义类.
 * 
 * 使用JAX-WS2.0 annotation设置WSDL中的定义.
 * 使用WSResult及其子类类包裹返回结果.
 * 使用DTO传输对象隔绝系统内部领域对象的修改对外系统的影响.
 * 
 * @author calvin
 */
// name 指明wsdl中<wsdl:portType>元素的名称
@WebService(name = "AccountService", targetNamespace = WsConstants.NS)
public interface AccountSoapService {
	/**
	 * 获取团队的详细信息.
	 */
	GetTeamDetailResult getTeamDetail(@WebParam(name = "id") Long id);

	/**
	 * 获取用户信息.
	 */
	GetUserResult getUser(@WebParam(name = "id") Long id);

	/**
	 * 搜索用户信息.
	 */
	SearchUserResult searchUser(@WebParam(name = "loginName") String loginName, @WebParam(name = "name") String name);

	/**
	 * 新建用户.
	 */
	IdResult createUser(@WebParam(name = "user") UserDTO user);

}
