/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

import org.multipage.util.Resources;

/**
 * Types of text holders.
 * @author vakol
 *
 */
public enum TextHolderType {

	UNKNOWN("middle.textTextHolderUnknown"),
	AREA("middle.textTextHolderArea"),
	RESOURCE("middle.textTextHolderResource"),
	AREASLOT("middle.textTextHolderAreaSlot"),
	PROGRAM("middle.textTextHolderProgram"),
	VERSION("middle.textHolderVersion");
	
	/**
	 * Text.
	 */
	private String text;
	
	/**
	 * Constructor.
	 */
	TextHolderType(String text) {
		
		this.text = Resources.getString(text);
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
}
