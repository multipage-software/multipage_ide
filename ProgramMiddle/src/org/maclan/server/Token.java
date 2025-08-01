/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

/**
 * Token object.
 * @author vakol
 */
class Token {

	/**
	 * Token type.
	 */
	int type;
	
	/**
	 * Token start.
	 */
	int start;
	
	/**
	 * Token end.
	 */
	int end;
	
	/**
	 * Properties.
	 */
	TagProperties properties = new TagProperties();
	
	/**
	 * Constructor.
	 * @param type
	 */
	public Token(int type) {

		this.type = type;
	}

	/**
	 * Constructor.
	 * @param type
	 * @param start
	 * @param end
	 */
	public Token(int type, int start, int end) {
		
		this.type = type;
		this.start = start;
		this.end = end;
	}

	/**
	 * Constructor.
	 * @param type
	 * @param start
	 * @param end
	 * @param properties 
	 * @param ref 
	 */
	public Token(int type, int start, int end, TagProperties properties) {

		this(type, start, end);

		this.properties = properties;
	}
}