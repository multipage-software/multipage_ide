/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-03-10
 *
 */
package org.maclan.server;

/**
 * CSS lookup table value.
 * @author vakol
 *
 */
class CssLookupTableValue {
	
	// Fields.
	public String propertyName;
	public boolean isImportant;
	public boolean process;

	// Constructor.
	public CssLookupTableValue(String propertyName, boolean isImportant, boolean process) {
		
		this.propertyName = propertyName;
		this.isImportant = isImportant;
		this.process = process;
	}
}