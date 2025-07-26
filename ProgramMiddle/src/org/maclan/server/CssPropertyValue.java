/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on :2020-03-10
 *
 */
package org.maclan.server;

/**
 * Class for CSS property.
 * @author vakol
 */
class CssPropertyValue {
	
	public String property;
	public String value;
	public boolean isImportant;

	public CssPropertyValue(String property, String value, boolean isImportant) {
		
		this.property = property;
		this.value = value;
		this.isImportant = isImportant;
	}
}