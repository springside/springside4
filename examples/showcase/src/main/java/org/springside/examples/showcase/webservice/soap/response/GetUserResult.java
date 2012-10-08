package org.springside.examples.showcase.webservice.soap.response;

import javax.xml.bind.annotation.XmlType;

import org.springside.examples.showcase.webservice.soap.WsConstants;
import org.springside.examples.showcase.webservice.soap.response.base.WSResult;
import org.springside.examples.showcase.webservice.soap.response.dto.UserDTO;

@XmlType(name = "GetUserResult", namespace = WsConstants.NS)
public class GetUserResult extends WSResult {
	private UserDTO user;

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}
}
