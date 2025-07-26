/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

/**
 * Class for tag processor.
 * @author vakol
 *
 */
public class FullTagProcessor {

	/**
	 * Process text.
	 * @param server
	 * @param properties 
	 */
	public String processText(AreaServer server, String innerText, TagProperties properties)
		throws Exception {
		
		// Override this method.
		return "";
	}
}
