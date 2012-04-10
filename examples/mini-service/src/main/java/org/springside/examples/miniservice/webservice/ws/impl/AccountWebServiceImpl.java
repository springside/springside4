package org.springside.examples.miniservice.webservice.ws.impl;

import java.util.List;

import javax.jws.WebService;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springside.examples.miniservice.entity.Department;
import org.springside.examples.miniservice.entity.User;
import org.springside.examples.miniservice.service.AccountManager;
import org.springside.examples.miniservice.webservice.WsConstants;
import org.springside.examples.miniservice.webservice.dto.DepartmentDTO;
import org.springside.examples.miniservice.webservice.dto.UserDTO;
import org.springside.examples.miniservice.webservice.ws.AccountWebService;
import org.springside.examples.miniservice.webservice.ws.result.DepartmentResult;
import org.springside.examples.miniservice.webservice.ws.result.UserListResult;
import org.springside.examples.miniservice.webservice.ws.result.base.IdResult;
import org.springside.examples.miniservice.webservice.ws.result.base.WSResult;
import org.springside.modules.beanvalidator.BeanValidators;
import org.springside.modules.mapper.BeanMapper;

/**
 * WebService服务端实现类.
 * 
 * 客户端实现见功能测试用例.
 * 
 * @author calvin
 */
//serviceName与portName属性指明WSDL中的名称, endpointInterface属性指向Interface定义类.
@WebService(serviceName = "AccountService", portName = "AccountServicePort", endpointInterface = "org.springside.examples.miniservice.webservice.ws.AccountWebService", targetNamespace = WsConstants.NS)
public class AccountWebServiceImpl implements AccountWebService {

	private static Logger logger = LoggerFactory.getLogger(AccountWebServiceImpl.class);

	private AccountManager accountManager;

	/**
	 * @see AccountWebService#getDepartmentDetail()
	 */
	@Override
	public DepartmentResult getDepartmentDetail(Long id) {
		try {
			Department entity = accountManager.getDepartmentDetail(id);

			Validate.notNull(entity, "部门不存在(id:" + id + ")");

			DepartmentDTO dto = BeanMapper.map(entity, DepartmentDTO.class);

			return new DepartmentResult(dto);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			return new DepartmentResult().setError(WSResult.PARAMETER_ERROR, e.getMessage());
		} catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			return new DepartmentResult().setDefaultError();
		}
	}

	/**
	 * @see AccountWebService#searchUser()
	 */
	@Override
	public UserListResult searchUser(String loginName, String name) {
		try {
			List<User> entityList = accountManager.searchUser(loginName, name);

			List<UserDTO> dtoList = BeanMapper.mapList(entityList, UserDTO.class);

			return new UserListResult(dtoList);
		} catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			return new UserListResult().setDefaultError();
		}
	}

	/**
	 * @see AccountWebService#createUser()
	 */
	@Override
	public IdResult createUser(UserDTO user) {
		try {
			User userEntity = BeanMapper.map(user, User.class);

			Long userId = accountManager.saveUser(userEntity);

			return new IdResult(userId);
		} catch (ConstraintViolationException e) {
			String message = StringUtils.join(BeanValidators.extractPropertyAndMessage(e), "\n");
			return new IdResult().setError(WSResult.PARAMETER_ERROR, message);
		} catch (DataIntegrityViolationException e) {
			String message = "新建用户参数存在唯一性冲突(用户:" + user + ")";
			logger.error(message, e);
			return new IdResult().setError(WSResult.PARAMETER_ERROR, message);
		} catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			return new IdResult().setDefaultError();
		}
	}

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}
}
