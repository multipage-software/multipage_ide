/*
 * Copyright 2010-2024 (C) vakol
 * 
 * Created on : 31-10-2024
 *
 */
package org.maclan.server;

/**
 * Watched item type.
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
