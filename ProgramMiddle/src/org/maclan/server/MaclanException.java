/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2023-03-06
 *
 */
package org.maclan.server;

/**
 * Maclan exception object.
 * @author vakol
 *
 */
public class MaclanException  {
	
	/**
	 * Reference to thrown exception.
	 */
	public Exception exception = null;
	
	/**
	 * Set exception reference.
	 * @param exception
	 */
	public void set(Exception exception) {
		
		this.exception = exception;
	}
	
	/**
	 * Get message text.
	 * @return
	 */
	public String getMessageText() {
		
		String messageText = exception.getLocalizedMessage();
		return messageText;
	}
}
