package org.springside.examples.miniservice.webservice.ws.result;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.springside.examples.miniservice.webservice.WsConstants;
import org.springside.examples.miniservice.webservice.dto.UserDTO;
import org.springside.examples.miniservice.webservice.ws.result.base.WSResult;

/**
 * 包含User
 * 
 * @author calvin
 * @author badqiu
 */
@XmlType(name = "UserPageResult", namespace = WsConstants.NS)
public class UserListResult extends WSResult {

	private List<UserDTO> userList;

	public UserListResult() {
	}

	public UserListResult(List<UserDTO> userList) {
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
