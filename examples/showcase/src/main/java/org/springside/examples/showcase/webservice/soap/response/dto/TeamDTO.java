package org.springside.examples.showcase.webservice.soap.response.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springside.examples.showcase.webservice.soap.WsConstants;

import com.google.common.collect.Lists;

@XmlRootElement
@XmlType(name = "Team", namespace = WsConstants.NS)
public class TeamDTO {

	private String name;
	private UserDTO master;
	private List<UserDTO> userList = Lists.newArrayList();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserDTO getMaster() {
		return master;
	}

	public void setMaster(UserDTO master) {
		this.master = master;
	}

	// 配置输出xml为<userList><user><id>1</id></user></userList>
	@XmlElementWrapper(name = "userList")
	@XmlElement(name = "user")
	public List<UserDTO> getUserList() {
		return userList;
	}

	public void setUserList(List<UserDTO> userList) {
		this.userList = userList;
	}

	/**
	 * 重新实现toString()函数方便在日志中打印DTO信息.
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
