package org.springside.examples.miniservice.webservice.ws.response;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.springside.examples.miniservice.webservice.WsConstants;
import org.springside.examples.miniservice.webservice.dto.UserDTO;
import org.springside.examples.miniservice.webservice.ws.response.base.WSResponse;

/**
 * 包含UserList的返回结果.
 * 
 * @author calvin
 * @author badqiu
 */
@XmlType(name = "UserListResponse", namespace = WsConstants.NS)
public class UserListResponse extends WSResponse {

	private List<UserDTO> userList;

	public UserListResponse() {
	}

	public UserListResponse(List<UserDTO> userList) {
		this.userList = userList;
	}

	@XmlElementWrapper(name = "userList")
	@XmlElement(name = "user")
	public List<UserDTO> getUserList() {
		return userList;
	}

	public void setUserList(List<UserDTO> userList) {
		this.userList = userList;
	}
}
