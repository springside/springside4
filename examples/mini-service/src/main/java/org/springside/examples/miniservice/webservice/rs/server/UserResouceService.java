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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springside.examples.miniservice.entity.User;
import org.springside.examples.miniservice.service.AccountManager;
import org.springside.examples.miniservice.webservice.WsConstants;
import org.springside.examples.miniservice.webservice.dto.UserDTO;
import org.springside.modules.jersey.WebExceptionFactory;
import org.springside.modules.mapper.BeanMapper;
import org.springside.modules.utils.ee.Validators;

/**
 * User资源的REST服务.
 * 
 * @author calvin
 */
@Path("/users")
public class UserResouceService {

	private static Logger logger = LoggerFactory.getLogger(UserResouceService.class);

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
				throw WebExceptionFactory.buildException(Status.NOT_FOUND, message, logger);
			}

			return BeanMapper.map(entity, UserDTO.class);
		} catch (RuntimeException e) {
			throw WebExceptionFactory.buildDefaultException(e, logger);
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
			throw WebExceptionFactory.buildDefaultException(e, logger);
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
			String message = Validators.convertMessage(e, "\n");
			throw WebExceptionFactory.buildException(Status.BAD_REQUEST, message, logger);
		} catch (DataIntegrityViolationException e) {
			String message = "新建用户参数存在唯一性冲突(用户:" + user + ")";
			throw WebExceptionFactory.buildException(Status.BAD_REQUEST, message, logger);
		} catch (RuntimeException e) {
			throw WebExceptionFactory.buildDefaultException(e, logger);
		}
	}

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}
}
