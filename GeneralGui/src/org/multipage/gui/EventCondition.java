/*
 * Copyright 2010-2020 (C) vakol
 * 
 * Created on : 21-01-2021
 *
 */
package org.multipage.gui;

/**
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
