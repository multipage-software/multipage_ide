/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-03-10
 *
 */
package org.maclan.server;

import java.util.LinkedList;

/**
 * CSS selector rules.
 * @author vakol
 *
 */
class SelectorRules {
	
	public String selector;
	public LinkedList<CssPropertyValue> cssPropertiesValues = new LinkedList<CssPropertyValue>();

	public SelectorRules(String selector) {
		
		this.selector = selector;
	}

	/**
	 * Insert property and value.
	 * @param property
	 * @param value
	 * @param isImportant 
	 */
	public void insert(String property, String value, boolean isImportant) {
		
		CssPropertyValue cssPropertyValue = new CssPropertyValue(property, value, isImportant);
		cssPropertiesValues.add(cssPropertyValue);
	}
}
