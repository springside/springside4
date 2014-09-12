/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.webservice.soap.response;

import javax.xml.bind.annotation.XmlType;

import org.springside.examples.showcase.webservice.soap.WsConstants;
import org.springside.examples.showcase.webservice.soap.response.base.WSResult;
import org.springside.examples.showcase.webservice.soap.response.dto.TeamDTO;

@XmlType(name = "GetTeamDetailResult", namespace = WsConstants.NS)
public class GetTeamDetailResult extends WSResult {

	private TeamDTO team;

	public GetTeamDetailResult() {
	}

	public GetTeamDetailResult(TeamDTO team) {
		this.team = team;
	}

	public TeamDTO getTeam() {
		return team;
	}

	public void setTeam(TeamDTO team) {
		this.team = team;
	}
}
