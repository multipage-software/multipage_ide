/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

/**
 * Callback function that returns true value if checkbox item matches.
 * @author vakol
 *
 */
public interface CheckBoxCallback<T> {

	/**
	 * Must return true if the object matches.
	 * @param object
	 * @return
	 */
	 boolean matches(T object);
}
