/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.web.taglib;

public class BSRadioButtonsTag extends BSAbstractMultiCheckedElementTag {
	private static final long serialVersionUID = 6257615872362092808L;

	@Override
	protected String getInputType() {
		return "radio";
	}
}
