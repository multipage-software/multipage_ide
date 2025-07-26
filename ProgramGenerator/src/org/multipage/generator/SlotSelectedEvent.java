/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.multipage.generator;

import org.maclan.Slot;

/**
 * Interface for slot selection.
 * @author vakol
 *
 */
public interface SlotSelectedEvent {

	/**
	 * Get the slot
	 */
	public void selected(Slot slot);
}
