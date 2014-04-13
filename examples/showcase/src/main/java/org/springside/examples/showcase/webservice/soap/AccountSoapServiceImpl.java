/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.webservice.soap;

import java.util.List;

import javax.jws.WebService;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.cxf.feature.Features;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springside.examples.showcase.entity.Team;
import org.springside.examples.showcase.entity.User;
import org.springside.examples.showcase.service.AccountEffectiveService;
import org.springside.examples.showcase.webservice.soap.response.GetTeamDetailResult;
import org.springside.examples.showcase.webservice.soap.response.GetUserResult;
import org.springside.examples.showcase.webservice.soap.response.SearchUserResult;
import org.springside.examples.showcase.webservice.soap.response.base.IdResult;
import org.springside.examples.showcase.webservice.soap.response.base.WSResult;
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
// serviceName指明WSDL中<wsdl:service>与<wsdl:binding>元素的名称, endpointInterface属性指向Interface类全称.
@WebService(serviceName = "AccountService", endpointInterface = "org.springside.examples.showcase.webservice.soap.AccountSoapService", targetNamespace = WsConstants.NS)
// 增加inbound/outbound SOAP内容的日志
@Features(features = "org.apache.cxf.feature.LoggingFeature")
public class AccountSoapServiceImpl implements AccountSoapService {

	private static Logger logger = LoggerFactory.getLogger(AccountSoapServiceImpl.class);

	@Autowired
	private AccountEffectiveService accountService;

	@Autowired
	private Validator validator;

	/**
	 * @see AccountSoapService#getTeamDetail(Long)
	 */
	@Override
	public GetTeamDetailResult getTeamDetail(Long id) {
		GetTeamDetailResult result = new GetTeamDetailResult();
		try {

			Validate.notNull(id, "id参数为空");

			Team team = accountService.getTeamWithDetail(id);

			Validate.notNull(team, "项目不存在(id:" + id + ")");

			TeamDTO dto = BeanMapper.map(team, TeamDTO.class);
			result.setTeam(dto);

			return result;
		} catch (IllegalArgumentException e) {
			return handleParameterError(result, e);
		} catch (RuntimeException e) {
			return handleGeneralError(result, e);
		}
	}

	/**
	 * @see AccountSoapService#getUser(Long)
	 */
	@Override
	public GetUserResult getUser(Long id) {
		GetUserResult result = new GetUserResult();
		try {

			Validate.notNull(id, "id参数为空");

			User user = accountService.getUser(id);

			Validate.notNull(user, "用户不存在(id:" + id + ")");

			UserDTO dto = BeanMapper.map(user, UserDTO.class);
			result.setUser(dto);

			return result;

		} catch (IllegalArgumentException e) {
			return handleParameterError(result, e);
		} catch (RuntimeException e) {
			return handleGeneralError(result, e);
		}
	}

	/**
	 * @see AccountSoapService#searchUser(String, String)
	 */
	@Override
	public SearchUserResult searchUser(String loginName, String name) {
		SearchUserResult result = new SearchUserResult();
		try {
			List<User> userList = accountService.searchUser(loginName, name);

			List<UserDTO> dtoList = BeanMapper.mapList(userList, UserDTO.class);
			result.setUserList(dtoList);
			return result;
		} catch (RuntimeException e) {
			return handleGeneralError(result, e);
		}
	}

	/**
	 * @see AccountSoapService#createUser(UserDTO)
	 */
	@Override
	public IdResult createUser(UserDTO user) {
		IdResult result = new IdResult();
		try {
			Validate.notNull(user, "用户参数为空");

			User userEntity = BeanMapper.map(user, User.class);
			BeanValidators.validateWithException(validator, userEntity);

			accountService.saveUser(userEntity);

			return new IdResult(userEntity.getId());
		} catch (ConstraintViolationException e) {
			String message = StringUtils.join(BeanValidators.extractPropertyAndMessageAsList(e, " "), "\n");
			return handleParameterError(result, e, message);
		} catch (RuntimeException e) {
			if (Exceptions.isCausedBy(e, DuplicateKeyException.class)) {
				String message = "新建用户参数存在唯一性冲突(用户:" + user + ")";
				return handleParameterError(result, e, message);
			} else {
				return handleGeneralError(result, e);
			}
		}
	}

	private <T extends WSResult> T handleParameterError(T result, Exception e, String message) {
		logger.error(message, e.getMessage());
		result.setError(WSResult.PARAMETER_ERROR, message);
		return result;
	}

	private <T extends WSResult> T handleParameterError(T result, Exception e) {
		logger.error(e.getMessage());
		result.setError(WSResult.PARAMETER_ERROR, e.getMessage());
		return result;
	}

	private <T extends WSResult> T handleGeneralError(T result, Exception e) {
		logger.error(e.getMessage());
		result.setDefaultError();
		return result;
	}
}
