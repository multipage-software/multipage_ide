/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-01-21
 *
 */
package org.multipage.gui;

/**
 * Application event interface.
 * @author vakol
 *
 */
public interface ApplicationEvent {
	
	/**
	 * Get condition name.
	 * @return
	 */
	String name();

	/**
	 * Get ordinal number specifying the priority of the condition.
	 * @return
	 */
	int ordinal();
	
	/**
	 * Returns true if the event matches incoming message.
	 * @param incomingMessage
	 * @return
	 */
	boolean matches(Message incomingMessage);
}
