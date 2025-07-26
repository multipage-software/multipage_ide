/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2024-10-31
 *
 */
package org.maclan.server;

/**
 * Watch types used in the debugger.
 * @author vakol
 */
public enum DebugWatchItemType {

	UNKNOWN("Unknown"),
	AREA("Area"),
	VERSION("Version"),
	LANGUAGE("Language"),
	START_RESOURCE("Start resource");
	
	/**
	 * Type name.
	 */
	private String name = "";

	/**
	 * Constructor.
	 * @param name
	 */
	DebugWatchItemType(String name) {

		this.name  = name;
	}
	
	/**
	 * Get type name.
	 * @return
	 */
	public String getName() {
		
		return name;
	}
}
