/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.multipage.generator;

import org.multipage.util.Safe;

/**
 * List of editor states.
 * @author vakol
 *
 */
public enum EditorState {
	
	/**
	 * Enumerations
	 */
	initial(".lino { background-color: @linenoColor@; }"),
	debugging(".lino { background-color: @debugColor@; }"),
	notDebugging(".lino { background-color: @linenoColor@; }");
	
	/**
	 * Constants
	 */
	String linenoColor = "#DDDDDD";
	String debugColor = "#DD0000";

	/**
	 * CSS rule for code editor
	 */
	String cssRule;
	
	/**
	 * Constructor
	 * @param cssRule
	 */
	EditorState(String cssRule) {
		try {
			
			cssRule = cssRule.replaceAll("@debugColor@", debugColor);
			this.cssRule = cssRule.replaceAll("@linenoColor@", linenoColor);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
