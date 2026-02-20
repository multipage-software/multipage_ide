/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2024-11-30
 *
 */
package org.multipage.gui;

/**
 * Interface for receiver object that can be removed automatically.
 * @author vakol
 */
public interface ReceiverAutoRemove {

	/**
	 * Check if the receiver can be removed automatically.
	 */
	boolean canAutoRemove();
}
