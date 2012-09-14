package org.springside.examples.showcase.webservice.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springside.examples.showcase.entity.User;
import org.springside.examples.showcase.service.AccountEffectiveService;
import org.springside.examples.showcase.webservice.rest.UserDTO;
import org.springside.modules.mapper.BeanMapper;

@Path("/user")
public class AccountJaxRsService {

	@Autowired
	private AccountEffectiveService accountService;

	@GET
	@Path("/{id}.xml")
	@Produces(MediaType.APPLICATION_XML)
	public UserDTO getAsXml(@PathParam("id") Long id) {
		User user = accountService.getUser(id);
		return bindDTO(user);
	}

	@GET
	@Path("/{id}.json")
	@Produces(MediaType.APPLICATION_JSON)
	public UserDTO getAsJson(@PathParam("id") Long id) {
		User user = accountService.getUser(id);
		return bindDTO(user);
	}

	private UserDTO bindDTO(User user) {
		UserDTO dto = BeanMapper.map(user, UserDTO.class);
		//补充Dozer不能自动绑定的属性
		dto.setTeamId(user.getTeam().getId());
		return dto;
	}
}
