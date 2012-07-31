package org.springside.examples.showcase.webservice.soap;

import java.util.List;

import javax.jws.WebService;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springside.examples.showcase.entity.Team;
import org.springside.examples.showcase.entity.User;
import org.springside.examples.showcase.service.AccountEffectiveService;
import org.springside.examples.showcase.webservice.soap.response.GetTeamDetailResponse;
import org.springside.examples.showcase.webservice.soap.response.GetUserResponse;
import org.springside.examples.showcase.webservice.soap.response.SearchUserResponse;
import org.springside.examples.showcase.webservice.soap.response.base.IdResponse;
import org.springside.examples.showcase.webservice.soap.response.base.WSResponse;
import org.springside.examples.showcase.webservice.soap.response.dto.TeamDTO;
import org.springside.examples.showcase.webservice.soap.response.dto.UserDTO;
import org.springside.modules.beanvalidator.BeanValidators;
import org.springside.modules.mapper.BeanMapper;
import org.springside.modules.utils.Exceptions;

/**
 * WebService服务端实现类.
 * 
 * 为演示方便，直接调用了Dao层.客户端实现见功能测试用例.
 * 
 * @author calvin
 */
//serviceName指明WSDL中的名称, endpointInterface属性指向Interface定义类.
@WebService(serviceName = "AccountService", endpointInterface = "org.springside.examples.showcase.webservice.soap.AccountWebService", targetNamespace = WsConstants.NS)
public class AccountWebServiceImpl implements AccountWebService {

	private static Logger logger = LoggerFactory.getLogger(AccountWebServiceImpl.class);

	@Autowired
	private AccountEffectiveService accountService;

	@Autowired
	private Validator validator;

	/**
	 * @see AccountWebService#getTeamDetail()
	 */
	@Override
	public GetTeamDetailResponse getTeamDetail(Long id) {
		GetTeamDetailResponse response = new GetTeamDetailResponse();
		try {

			Validate.notNull(id, "id参数为空");

			Team team = accountService.getTeamWithDetail(id);

			Validate.notNull(team, "项目不存在(id:" + id + ")");

			TeamDTO dto = BeanMapper.map(team, TeamDTO.class);
			response.setTeam(dto);

			return response;
		} catch (IllegalArgumentException e) {
			return handleParameterError(response, e);
		} catch (RuntimeException e) {
			return handleGeneralError(response, e);
		}
	}

	/**
	 * @see AccountWebService#getUser()
	 */
	@Override
	public GetUserResponse getUser(Long id) {
		GetUserResponse response = new GetUserResponse();
		try {

			Validate.notNull(id, "id参数为空");

			User user = accountService.getUser(id);

			Validate.notNull(user, "用户不存在(id:" + id + ")");

			UserDTO dto = BeanMapper.map(user, UserDTO.class);
			response.setUser(dto);

			return response;

		} catch (IllegalArgumentException e) {
			return handleParameterError(response, e);
		} catch (RuntimeException e) {
			return handleGeneralError(response, e);
		}
	}

	/**
	 * @see AccountWebService#searchUser()
	 */
	@Override
	public SearchUserResponse searchUser(String loginName, String name) {
		SearchUserResponse response = new SearchUserResponse();
		try {
			List<User> userList = accountService.searchUser(loginName, name);

			List<UserDTO> dtoList = BeanMapper.mapList(userList, UserDTO.class);
			response.setUserList(dtoList);
			return response;
		} catch (RuntimeException e) {
			return handleGeneralError(response, e);
		}
	}

	/**
	 * @see AccountWebService#createUser()
	 */
	@Override
	public IdResponse createUser(UserDTO user) {
		IdResponse response = new IdResponse();
		try {
			Validate.notNull(user, "用户参数为空");

			User userEntity = BeanMapper.map(user, User.class);
			BeanValidators.validateWithException(validator, userEntity);

			accountService.saveUser(userEntity);

			return new IdResponse(userEntity.getId());
		} catch (ConstraintViolationException e) {
			String message = StringUtils.join(BeanValidators.extractPropertyAndMessageAsList(e, " "), "\n");
			return handleParameterError(response, e, message);
		} catch (RuntimeException e) {
			if (Exceptions.isCausedBy(e, org.hibernate.exception.ConstraintViolationException.class)) {
				String message = "新建用户参数存在唯一性冲突(用户:" + user + ")";
				return handleParameterError(response, e, message);
			} else {
				return handleGeneralError(response, e);
			}
		}
	}

	private <T extends WSResponse> T handleParameterError(T response, Exception e, String message) {
		logger.error(message, e.getMessage());
		response.setError(WSResponse.PARAMETER_ERROR, message);
		return response;
	}

	private <T extends WSResponse> T handleParameterError(T response, Exception e) {
		logger.error(e.getMessage());
		response.setError(WSResponse.PARAMETER_ERROR, e.getMessage());
		return response;
	}

	private <T extends WSResponse> T handleGeneralError(T response, Exception e) {
		logger.error(e.getMessage());
		response.setDefaultError();
		return response;
	}
}
