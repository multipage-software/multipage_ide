/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-05-13
 *
 */

package org.multipage.gui;

import java.net.MalformedURLException;

/**
 * Exception thrown when URL protocol is missing.
 * @author vakol
 *
 */
public class MissingUrlProtocolException extends MalformedURLException {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * @param message
	 */
	public MissingUrlProtocolException(String message) {
		super(message);
	}
}
