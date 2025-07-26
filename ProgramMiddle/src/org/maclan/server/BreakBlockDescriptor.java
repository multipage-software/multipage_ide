/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

/**
 * Block descriptor with break and discard operations.
 * @author
 *
 */
public class BreakBlockDescriptor extends BlockDescriptor {

	/**
	 * Last item flag.
	 */
	protected boolean breaked = false;

	/**
	 * Discard flag.
	 */
	protected boolean discard = false;

	/**
	 * Set last list item.
	 * @param discard 
	 */
	public void setBreaked(boolean discard) {
		
		this.breaked = true;
		this.discard = discard;
	}

	/**
	 * Returns true if the loop is breaked by @LAST tag.
	 * @return
	 */
	public boolean isBreaked() {
		
		return breaked;
	}
	
	/**
	 * Get discard flag.
	 */
	public boolean getDiscard() {
		
		return discard;
	}
}
