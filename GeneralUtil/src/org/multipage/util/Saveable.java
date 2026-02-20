/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-09-25
 *
 */
package org.multipage.util;

/**
 * All saveable objects can inherit from this interface.
 * @author vakol
 */
public interface Saveable {
	
	/**
	 * Save object.
	 * @return True if saved.
	 */
	boolean save();
}
