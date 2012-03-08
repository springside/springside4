package org.springside.examples.showcase.webservice.rs.server;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springside.modules.rest.jersey.Jerseys;

@Provider
@Component
public class ShiroExceptionMapper implements ExceptionMapper<UnauthorizedException> {
	public Response toResponse(UnauthorizedException ex) {
		return Jerseys.buildResponse(Status.UNAUTHORIZED, ex.getMessage());
	}

}
