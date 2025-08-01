/**
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-02-19
 * 
 */
package org.multipage.gui;

import java.util.function.Consumer;

/**
 * Class for application event handler.
 * @author vakol
 * 
 */
public class EventHandle {
	
	/**
	 * An action is a lambda function. It consumes the message if it is not coalesced in a time span.
	 */
	public Consumer<Message> action;
	
	/**
	 * Priority of the event handle.
	 */
	public Integer priority;
	
	/**
	 * Time span in milliseconds for coalescing the same messages. They do not trigger the action.
	 */
	public Long coalesceTimeSpanMs;
	
	/**
	 * Reflection of the signal receiver.
	 */
	public StackTraceElement reflection;
	
	/**
	 *  Identifier of this event handle (only for debugging purposes; this property can be removed from
	 *  the class code along with the debugging code).
	 */
	public String identifier;
	
	/**
	 * An event handle key. For debugging purposes.
	 */
	public Object key;
	
	/**
	 * Constructor.
	 * @param action
	 * @param priority 
	 * @param eventPriority 
	 * @param timeSpanMs
	 * @param reflection
	 * @param identifier
	 */
	EventHandle(Consumer<Message> action, int priority, Long timeSpanMs, StackTraceElement reflection, String identifier) {
		
		this.action = action;
		this.priority = priority;
		this.coalesceTimeSpanMs = timeSpanMs;
		this.reflection = reflection;
		this.identifier = identifier;
	}
	
	/**
	 * Trim the identifier string.
	 * @return
	 */
	String identifier() {
		return identifier != null ? identifier : "";
	}
}