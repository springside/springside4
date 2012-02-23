package org.springside.examples.miniservice.webservice.ws.result;

import javax.xml.bind.annotation.XmlType;

import org.springside.examples.miniservice.webservice.WsConstants;
import org.springside.examples.miniservice.webservice.dto.DepartmentDTO;
import org.springside.examples.miniservice.webservice.ws.result.base.WSResult;

/**
 * GetDepartment方法的返回结果.
 * 
 * @author calvin
 * @author badqiu
 */
@XmlType(name = "DepartmentResult", namespace = WsConstants.NS)
public class DepartmentResult extends WSResult {

	private DepartmentDTO department;

	public DepartmentResult() {
	}

	public DepartmentResult(DepartmentDTO department) {
		this.department = department;
	}

	public DepartmentDTO getDepartment() {
		return department;
	}

	public void setDepartment(DepartmentDTO department) {
		this.department = department;
	}
}
