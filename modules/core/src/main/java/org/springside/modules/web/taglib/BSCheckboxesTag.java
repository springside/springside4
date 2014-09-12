/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.web.taglib;

import javax.servlet.jsp.JspException;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.tags.form.TagWriter;

public class BSCheckboxesTag extends BSAbstractMultiCheckedElementTag {

	private static final long serialVersionUID = 4310358928301706621L;

	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		super.writeTagContent(tagWriter);

		if (!isDisabled()) {
			// Write out the 'field was present' marker.
			tagWriter.startTag("input");
			tagWriter.writeAttribute("type", "hidden");
			String name = WebDataBinder.DEFAULT_FIELD_MARKER_PREFIX + getName();
			tagWriter.writeAttribute("name", name);
			tagWriter.writeAttribute("value", processFieldValue(name, "on", getInputType()));
			tagWriter.endTag();
		}

		return SKIP_BODY;
	}

	@Override
	protected String getInputType() {
		return "checkbox";
	}
}
