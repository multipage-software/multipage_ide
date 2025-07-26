/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

/**
 * Interface for callback functions.
 * @author vakol
 *
 */
@FunctionalInterface
public interface Callback {

	/**
	 * Callback method.
	 * @param input
	 * @return
	 */
	public abstract Object run(Object input);
}
