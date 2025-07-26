/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.maclan.server;

import java.util.Properties;

/**
 * Database login properties.
 * @author vakol
 *
 */
public class LoginProperties extends Properties {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public LoginProperties() {
		
		put("server", "localhost");
		put("port", "5432");
		put("ssl", "false");
		put("username", "administrator");
		put("password", "1");
	}
}
