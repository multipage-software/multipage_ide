/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-02-23
 *
 */

package org.multipage.gui;

import java.util.Objects;
import java.util.function.Function;

/**
 * Initiator of conditional event.
 * @author vakol
 *
 */
public class EventSource {
	
	/**
	 * Enumeration of sources.
	 */
	public static final EventSource UNKNOWN = new EventSource("UNKNOWN");
	public static final EventSource GENERATOR_MAIN_FRAME = new EventSource("GENERATOR_MAIN_FRAME");
	public static final EventSource AREA_EDITOR = new EventSource("AREA_EDITOR");
	public static final EventSource SLOT_EDITOR = new EventSource("SLOT_EDITOR");
	public static final EventSource AREA_SLOTS_EDITOR = new EventSource("AREA_SLOTS_EDITOR");
	public static final EventSource LOCAL_POPUP_MENU = new EventSource("LOCAL_POPUP_MENU");
	public static final EventSource AREA_TRACE = new EventSource("AREA_TRACE");
	public static final EventSource AREAS_DIAGRAM = new EventSource("AREAS_DIAGRAM");
	public static final EventSource AREA_LOCAL_MENU = new EventSource("AREA_LOCAL_MENU");
	public static final EventSource SETTINGS = new EventSource("SETTINGS");
	public static final EventSource AREA_TREE_EDITOR_FRAME = new EventSource("AREA_TREE_EDITOR_FRAME");
	
	/**
	 * Name of the event source.
	 */
	public String name = null;
		
	/**
	 * Flag that determines user direct event.
	 */
	public boolean isUserAction = true;
		
	/**
	 * Reference to source object .
	 */
	public Object sourceObject = null;
	
	/**
	 * Source message.
	 */
	public Message sourceMessage = null;
		
	/**
	 * Reference to previous event source that causes the event.
	 */
	public EventSource previousEventSource = null;
	
	/**
	 * Basic event source which is set to one of the above listed basic constant sources.
	 */
	public EventSource basicEventSource = null;
	
	/**
	 * Constructor.
	 */
	public EventSource() {
		
	}
	
	/**
	 * Constructor.
	 * @param name
	 */
	public EventSource(String name) {
		
		this.name = name;
	}
	
	/**
	 * Get description of the event source.
	 * @return
	 */
	public String getDescription() {
		
		String className = getClass().getSimpleName();
		String sourceName = name != null ? name : "unknown";
		String initiator = isUserAction ? "user" : "machine";
		
		String description = String.format("%s %s initiated by %s", className, sourceName, initiator);
		return description;
	}

	/**
	 * Clone new event source for user action.
	 * @param initiatorObject
	 * @param initiatorMessage
	 * @return
	 */
	public EventSource user(Object initiatorObject, Message initiatorMessage) {
		
		// Clone the event source.
		EventSource clonedEventSource = clone(initiatorObject, true, initiatorMessage);
		
		// Return cloned event source.
		return clonedEventSource;
	}
	
	/**
	 * Clone new event source for user action.
	 * @param initiatorObject
	 * @return
	 */
	public EventSource user(Object initiatorObject) {
		
		// Delegate the call.
		return user(initiatorObject, null);
	}
	
	/**
	 * Clone new event source for machine action.
	 * @param initiatorObject
	 * @param sourceMessage
	 * @return
	 */
	public EventSource machine(Object initiatorObject, Message intiatorMessage) {
		
		// Clone the event source.
		EventSource clonedEventSource = clone(initiatorObject, false, sourceMessage);
		
		// Return cloned event source.
		return clonedEventSource;
	}
	
	/**
	 * Clone new event source for machine action.
	 * @param initiatorObject
	 * @return
	 */
	public EventSource machine(Object initiatorObject) {
		
		// Delegate the call.
		return machine(initiatorObject, null);		
	}
	
	/**
	 * Clone event source.
	 * @param initiatorObject
	 * @param userInitiated
	 * @param sourceMessage
	 * @return
	 */
	public EventSource clone(Object initiatorObject, boolean userInitiated, Message sourceMessage) {
		
		// Create new the event source.
		EventSource clonedEventSource = new EventSource();
		
		// Set name.
		clonedEventSource.name = name;
				
		// Set user initiation flag.
		clonedEventSource.isUserAction = userInitiated;
		
		// Preserve basic source reference.
		clonedEventSource.basicEventSource = basicEventSource;
		
		// Set source object.
		clonedEventSource.sourceObject = initiatorObject;
		
		// Set source message.
		clonedEventSource.sourceMessage = sourceMessage;
		
		// Return cloned event source.
		return clonedEventSource;
	}
	
	/**
	 * Check if input object equals to this event source.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventSource other = (EventSource) obj;
		return Objects.equals(basicEventSource, other.basicEventSource)
				&& Objects.equals(sourceMessage, other.sourceMessage)
				&& Objects.equals(sourceObject, other.sourceObject) && isUserAction == other.isUserAction;
	}

	/**
	 * Get initiator message.
	 * @return
	 */
	public Message getInitiatorMessage() {
		
		return sourceMessage;
	}
	
	/**
	 * Traverse previously sent messages and find the matching message.
	 * @param signal
	 * @param receivingObject
	 * @param previousMessageLambda
	 * @return
	 */
	public Message traversePreviousMessages(Signal signal, Object receivingObject, Function<Message, Boolean> previousMessageLambda) {
		
		// Check input.
		if (previousMessageLambda == null) {
			return null;
		}
		
		EventSource eventSource = this;
		do {
			
			// Get source message.
			Message previousMessage = eventSource.sourceMessage;
			
			// Check the previous message for null value.
			if (previousMessage == null) {
				return null;
			}
			
			// If the message matches input parameters and lambda function returns true value,
			// return the message.
			if (previousMessage.signal == signal && previousMessage.source == receivingObject) {
				
				if (previousMessageLambda.apply(previousMessage)) {
					return previousMessage;
				}
			}
			
			// Get previous event source.
			eventSource = eventSource.previousEventSource;
		}
		while (eventSource != null);
		
		// Message not found.
		return null;
	}
}
