package org.springside.examples.miniservice.webservice.rs.server;

import java.net.URI;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springside.examples.miniservice.entity.User;
import org.springside.examples.miniservice.service.AccountManager;
import org.springside.examples.miniservice.webservice.WsConstants;
import org.springside.examples.miniservice.webservice.dto.UserDTO;
import org.springside.modules.beanvalidator.BeanValidators;
import org.springside.modules.mapper.BeanMapper;
import org.springside.modules.rest.RsResponse;

/**
 * User资源的REST服务.
 * 
 * @author calvin
 */
@Path("/users")
public class UserResouceService {

	private AccountManager accountManager;

	@Context
	private UriInfo uriInfo;

	/**
	 * 获取用户信息.
	 */
	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML + WsConstants.CHARSET })
	public UserDTO getUser(@PathParam("id") Long id) {
		try {
			User entity = accountManager.getUser(id);

			if (entity == null) {
				String message = "用户不存在(id:" + id + ")";
				throw RsResponse.buildException(Status.NOT_FOUND, message);
			}

			return BeanMapper.map(entity, UserDTO.class);
		} catch (RuntimeException e) {
			throw RsResponse.buildDefaultException(e);
		}
	}

	/**
	 * 查询用户, 请求数据为URL中的请求参数, 返回用户列表.
	 */
	@GET
	@Path("search")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML + WsConstants.CHARSET })
	public List<UserDTO> searchUser(@QueryParam("loginName") String loginName, @QueryParam("name") String name) {
		try {
			List<User> entityList = accountManager.searchUser(loginName, name);

			return BeanMapper.mapList(entityList, UserDTO.class);
		} catch (RuntimeException e) {
			throw RsResponse.buildDefaultException(e);
		}
	}

	/**
	 * 创建用户, 请求数据为POST过来的JSON/XML格式编码的DTO, 返回表示所创建用户的URI.
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML + WsConstants.CHARSET })
	public Response createUser(UserDTO user) {
		//转换并创建用户
		try {
			User userEntity = BeanMapper.map(user, User.class);

			Long id = accountManager.saveUser(userEntity);

			URI createdUri = uriInfo.getAbsolutePathBuilder().path(id.toString()).build();
			return Response.created(createdUri).build();
		} catch (ConstraintViolationException e) {
			String message = StringUtils.join(BeanValidators.extractPropertyAndMessage(e), "\n");
			throw RsResponse.buildException(Status.BAD_REQUEST, message);
		} catch (DataIntegrityViolationException e) {
			String message = "新建用户参数存在唯一性冲突(用户:" + user + ")";
			throw RsResponse.buildException(Status.BAD_REQUEST, message);
		} catch (RuntimeException e) {
			throw RsResponse.buildDefaultException(e);
		}
	}

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}
}
