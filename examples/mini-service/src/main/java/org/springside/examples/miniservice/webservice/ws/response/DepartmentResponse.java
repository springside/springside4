package org.springside.examples.miniservice.webservice.ws.response;

import javax.xml.bind.annotation.XmlType;

import org.springside.examples.miniservice.webservice.WsConstants;
import org.springside.examples.miniservice.webservice.dto.DepartmentDTO;
import org.springside.examples.miniservice.webservice.ws.response.base.WSResponse;

/**
 * 包含Department的返回结果.
 * 
 * @author calvin
 * @author badqiu
 */
@XmlType(name = "DepartmentResponse", namespace = WsConstants.NS)
public class DepartmentResponse extends WSResponse {

	private DepartmentDTO department;

	public DepartmentResponse() {
	}

	public DepartmentResponse(DepartmentDTO department) {
		this.department = department;
	}

	public DepartmentDTO getDepartment() {
		return department;
	}

	public void setDepartment(DepartmentDTO department) {
		this.department = department;
	}
}
