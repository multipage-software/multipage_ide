/*
 * Copyright 2010-2017 (C) vakol
 * 
 * Created on : 2017-06-24
 *
 */

package org.multipage.util;

/**
 * Result of the progress dialog.
 * @author vakol
 *
 */
public enum ProgressResult {
	
	/**
	 * Thread not started.
	 */
	NONE,
	
	/**
	 * Result OK.
	 */
	OK,
	
	/**
	 * Thread cancelled.
	 */
	CANCELLED,
	
	/**
	 * Thread interrupted.
	 */
	INTERRUPTED,
	
	/**
	 * Process execution custom exception.
	 */
	EXECUTION_EXCEPTION;

	/**
	 * Returns true value if the result is OK.
	 * @return
	 */
	public boolean isOk() {
		
		return this == OK;
	}
}
