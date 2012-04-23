package org.springside.examples.showcase.webservice.rs.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springside.examples.showcase.common.entity.User;
import org.springside.examples.showcase.common.service.AccountManager;
import org.springside.examples.showcase.webservice.WsConstants;
import org.springside.examples.showcase.webservice.rs.dto.UserDTO;
import org.springside.modules.mapper.BeanMapper;
import org.springside.modules.rest.RsResponse;

import com.sun.jersey.multipart.MultiPart;

/**
 * 用户资源 REST服务演示.
 * 在Mini-Service演示的基础上添加更多演示.
 * 
 * @author calvin
 */
@Path("/users")
public class UserResourceService {

	@Autowired
	private AccountManager accountManager;

	/**
	 * 获取所有用户, 演示与Shiro结合的方法级权限检查.
	 */
	@GET
	@RequiresRoles("Admin")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML + WsConstants.CHARSET })
	public List<UserDTO> getAllUser() {
		try {
			List<User> entityList = accountManager.getAllUserInitialized();
			return BeanMapper.mapList(entityList, UserDTO.class);
		} catch (RuntimeException e) {
			throw RsResponse.buildDefaultException(e);
		}
	}

	/**
	 * 获取用户.
	 */
	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML + WsConstants.CHARSET })
	public UserDTO getUser(@PathParam("id") Long id) {
		try {
			User entity = accountManager.getUserEffective(id);

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
	 * 搜索用户.
	 * 
	 * 演示QueryParam与不同格式不同返回内容的Response.
	 */
	@GET
	@Path("/search")
	public Response searchUser(@QueryParam("name") String name,
			@QueryParam("format") @DefaultValue("json") String format) {
		try {
			User entity = accountManager.findUserByNameInitialized(name);

			if (entity == null) {
				String message = "用户不存在";
				throw RsResponse.buildException(Status.NOT_FOUND, message);
			}

			if ("html".equals(format)) {
				//返回html格式的特定字符串.
				String html = "<div>" + entity.getName() + ", your mother call you...</div>";
				return Response.ok(html, MediaType.TEXT_HTML + WsConstants.CHARSET).build();
			} else {
				//返回JSON格式的对象.
				UserDTO dto = BeanMapper.map(entity, UserDTO.class);
				return Response.ok(dto, MediaType.APPLICATION_JSON).build();
			}
		} catch (RuntimeException e) {
			throw RsResponse.buildDefaultException(e);
		}
	}

	/**
	 * 演示直接获取灵活,不固定的参数.
	 * 可以从原版HttpServletRequest中获取,也可以用封装好的更方便的UriInfo和HttpHeaders.
	 */
	@GET
	public String searchUserByFlexibleParameter(@Context HttpServletRequest request, @Context UriInfo ui,
			@Context HttpHeaders hh) {
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();

		//先尝试从Http Header获取参数,如没有再尝试从URL参数中获取.
		String userName = null;
		if (hh.getRequestHeader("userName") != null) {
			userName = hh.getRequestHeader("userName").get(0);
		} else {
			userName = queryParams.getFirst("userName");
		}

		if (userName == null) {
			//抛出自定义的异常450
			throw RsResponse.buildException(450, "用戶名既不在Http Header也不在URL参数中");
		}

		return userName;
	}

	/**
	 * 演示接收Multi-Part的请求内容.
	 */
	@Path("/multipart")
	@POST
	@Consumes("multipart/mixed")
	public String multiPart(MultiPart multiPart) {
		User user = multiPart.getBodyParts().get(0).getEntityAs(User.class);
		String text = multiPart.getBodyParts().get(1).getEntityAs(String.class);
		return user.getName() + ":" + text;
	}
}
