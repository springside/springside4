package org.springside.examples.miniservice.webservice.rs.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springside.examples.miniservice.entity.Department;
import org.springside.examples.miniservice.service.AccountManager;
import org.springside.examples.miniservice.webservice.WsConstants;
import org.springside.examples.miniservice.webservice.dto.DepartmentDTO;
import org.springside.modules.mapper.BeanMapper;
import org.springside.modules.rest.RsResponse;

/**
 * Department资源的REST服务.
 * 
 * @author calvin
 */
@Path("/departments")
public class DepartmentResouceService {

	private AccountManager accountManager;

	/**
	 * 获取部门详细信息.
	 */
	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML + WsConstants.CHARSET })
	public DepartmentDTO getDepartmentDetail(@PathParam("id") Long id) {
		try {
			Department entity = accountManager.getDepartmentDetail(id);

			if (entity == null) {
				String message = "部门不存在(id:" + id + ")";
				throw RsResponse.buildException(Status.NOT_FOUND, message);
			}

			return BeanMapper.map(entity, DepartmentDTO.class);
		} catch (RuntimeException e) {
			throw RsResponse.buildDefaultException(e);
		}
	}

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}
}
