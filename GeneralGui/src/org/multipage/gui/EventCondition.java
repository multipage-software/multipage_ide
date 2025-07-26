/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-01-21
 *
 */
package org.multipage.gui;

/**
 * Interface for application event condition.
 * @author vakol
 *
 */
public interface EventCondition {
	
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
	 * Returns true if the event condition matches incoming message.
	 * @param incomingMessage
	 * @return
	 */
	boolean matches(Message incomingMessage);
}
