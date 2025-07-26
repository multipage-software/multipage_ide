/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Window;
import java.util.LinkedList;

import javax.swing.JList;

import org.maclan.MimeType;

/**
 * Interface for list of resources that can be searched.
 * @author vakol
 *
 */
public interface SearchableResourcesList {
	
	/**
	 * Get window.
	 * @return
	 */
	Window getWindow();
	
	/**
     * Get MIME types.
     * @return
     */
	LinkedList<MimeType> getMimeTypes();
	
	/**
	 * Get list of resources.
	 * @return
	 */
	JList getList();

}
