/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-12-07
 *
 */
package org.multipage.gui;

/**
 * Class for HTTP exception object.
 * @author vakol
 *
 */
public class HttpException extends Exception {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Exception body.
	 */
	private String exceptionBody = null;
	
	/**
	 * Constructor.
	 * @param errorMessage
	 * @param 
	 */
	public HttpException(String errorMessage, String exceptionBody) {
		super(errorMessage);
		
		// Remember the exception body
		this.exceptionBody = exceptionBody;
	}
	
	/**
	 * Get exception body.
	 * @return
	 */
	public String getExceptionBody() {
		
		return this.exceptionBody;
	}
}
