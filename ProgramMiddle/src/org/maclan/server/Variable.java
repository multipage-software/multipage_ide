/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

/**
 * Variable object.
 * @author vakol
 *
 */
public class Variable {

	/**
	 * Name.
	 */
	public String name;
	
	/**
	 * Value.
	 */
	public Object value;
	
	/**
	 * Constructor.
	 * @param name
	 * @param value
	 */
	public Variable(String name, Object value) {
		
		this.name = name;
		this.value = value;
	}

	/**
	 * Constructor.
	 */
	public Variable() {
		
		name = "";
		value = null;
	}
}
