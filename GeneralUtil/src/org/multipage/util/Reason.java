/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2018-11-20
 *
 */
package org.multipage.util;

import org.multipage.util.Safe;

/**
 * A reasons for thread unlock.
 * @author vakol
 *
 */
public enum Reason {
	
	/**
	 * Unlock reasons.
	 */
	UNLOCKED("UNLOCKED", 0),
	TIMEOUT("TIMEOUT", 1),
	UNKNOWN("UNKNOWN", -1),
	INTERRUPTED("INTERRUPTED", -3);
	
	/**
	 * Code.
	 */
	private int code;
	
	/**
	 * Description.
	 */
	private String description;
	
	/**
	 * Constructor.
	 */
	Reason(String description, int code) {
		
		this.description = description;
		this.code = code;
	}
	
	/**
	 * Check value.
	 */
	public boolean is(Reason value) {
		
		return value == this;
	}
	
	/**
	 * Get exception.
	 * @return
	 */
	public Exception exception() {
		
		Exception e = null;
		try {
			if (this == TIMEOUT) {
				e = new Exception("Lock timeout ellapsed");
			}
			else if (this == UNKNOWN) {
				e = new Exception("Unknown lock error");
			}
		}
		catch (Throwable exc) {
			Safe.exception(exc);
		}
		return e;
	}
	
	/**
	 * Get description.
	 * @return
	 */
	public String getDescription() {
		
		return description;
	}
	
	/**
	 * Get code.
	 * @return
	 */
	public int getCode() {
		
		return code;
	}
}