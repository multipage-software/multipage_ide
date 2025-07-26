/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.maclan.server;

/**
 * Class for redirection object.
 * @author vakol
 *
 */
public class Redirection {

	/**
	 * Redirection URI
	 */
	private String uri = "";
	
	/**
	 * Flag "direct".
	 */
	private boolean direct = false;
	
	/**
	 * Redirection active
	 */
	private boolean active = false;
	
	/**
	 * Set URI
	 * @param uri
	 * @param direct 
	 */
	public void setUri(String uri, boolean direct) {
		
		this.uri = uri;
		this.direct = direct;
		active = true;
	}

	/**
	 * Get URI
	 * @return
	 */
	public String getUri() {
		
		return uri;
	}
	
	/**
	 * Returns true if this is a direct URL redirection.
	 * @return
	 */
	public boolean isDirect() {
		
		return direct;
	}
	
	/**
	 * Returns true if the redirection was set
	 * @return
	 */
	public boolean isActive() {
		
		return active;
	}
}
