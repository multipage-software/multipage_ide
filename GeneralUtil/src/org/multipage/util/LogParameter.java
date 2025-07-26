/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-10-23
 *
 */
package org.multipage.util;

/**
 * Input parameter for j.log(...).
 * @author vakol
 *
 */
public class LogParameter {
	
	/**
	 * Output type.
	 */
	private String type = "out";
	
	/**
	 * Indentation.
	 */
	private String indentation = "";
	
	/**
	 * Constructor.
	 */
	public LogParameter(String type, String indentation) {
		
		this.type = type;
		this.indentation = indentation;
	}
	
	/**
	 * Get type.
	 */
	public String getType() {
		
		return type;
	}
	
	/**
	 * Get indentation.
	 */
	public String getIndentation() {
		
		return indentation;
	}
	
	/**
	 * Returns output type.
	 */
	@Override
	public String toString() {
		
		return getType();
	}
}
