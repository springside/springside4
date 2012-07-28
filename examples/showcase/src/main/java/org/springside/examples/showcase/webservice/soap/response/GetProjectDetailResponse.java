package org.springside.examples.showcase.webservice.soap.response;

import javax.xml.bind.annotation.XmlType;

import org.springside.examples.showcase.webservice.soap.WsConstants;
import org.springside.examples.showcase.webservice.soap.response.base.WSResponse;
import org.springside.examples.showcase.webservice.soap.response.dto.ProjectDTO;

@XmlType(name = "GetProjectDetailResponse", namespace = WsConstants.NS)
public class GetProjectDetailResponse extends WSResponse {

	private ProjectDTO department;

	public GetProjectDetailResponse() {
	}

	public GetProjectDetailResponse(ProjectDTO department) {
		this.department = department;
	}

	public ProjectDTO getDepartment() {
		return department;
	}

	public void setDepartment(ProjectDTO department) {
		this.department = department;
	}
}
