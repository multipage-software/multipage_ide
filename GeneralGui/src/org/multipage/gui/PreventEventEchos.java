/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-06-17
 *
 */
package org.multipage.gui;

import java.util.LinkedList;

/**
 * Interface for modules that must avoid infinite message cycles caused by event echos.
 * @author vakol
 *
 */
public interface PreventEventEchos {
		
	/**
	 * Get list of previous messages.
	 */
	public LinkedList<Message> getPreviousMessages();
}

