/**
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 * 
 */
package org.multipage.gui;

/**
 * Interface for value editor.
 * @author vakol
 *
 */
public interface EditorValueHandler {

	/**
	 * Ask user for value.
	 */
	public boolean ask();
	
	/**
	 * Get area reference string.
	 */
	public String getText();
	
	/**
	 * Get area reference command.
	 */
	public String getValue();
}
